package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import bijnum.BIJStats;
import cn.ac.rcpa.bio.annotation.StatisticRanges;
import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultFileType;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide;
import cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType;
import cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType;
import cn.ac.rcpa.bio.tools.distribution.option.types.FilterType;
import cn.ac.rcpa.utils.RcpaStringUtils;

abstract public class AbstractDistributionCalculator<E extends IIdentifiedPeptide, F extends IIdentifiedPeptideHit<E>>
		implements IFileProcessor {
	protected int maxPeptideCountWidth;

	protected DistributionOption option;

	protected IdentifiedResultFileType fileType;

	protected File resultDir;

	protected File sourceFile;

	protected IClassification<IIdentifiedPeptide> sphc;

	protected Map ExperimentValues;

	protected String[] classifiedNames;

	protected CalculationItemList<E, F> calculationItems = new CalculationItemList<E, F>();

	protected HashMap<String, ArrayList<Integer>> uniquePeptideCounts = new HashMap<String, ArrayList<Integer>>();

	protected HashMap<String, ArrayList<Integer>> peptideCounts = new HashMap<String, ArrayList<Integer>>();

	protected String typeTitle;

	protected String theoreticalValueTitle;

	private boolean exportIndividual;

	protected AbstractDistributionCalculator(String typeTitle,
			String theoreticalValueTitle, boolean exportIndividual) {
		super();
		this.typeTitle = typeTitle;
		this.theoreticalValueTitle = theoreticalValueTitle;
		this.exportIndividual = exportIndividual;
	}

	public List<String> process(String optionFileName) throws Exception {
		File optionFile = new File(optionFileName);

		init(optionFile);

		parseToCalculationItems();

		calculatePeptideCount();

		doStatisticBaseOnPeptideCount();

		doRangeStatistic();

		doCorrelationStatistic();

		doDuplicationStatistic();

		if (exportIndividual) {
			doDecreaseStatistic();
			exportIndividualFractionFile();
		}

		return new ArrayList<String>();
	}

	class RangeValue {
		public int uniqueCount;

		public int totalCount;

		public RangeValue() {
			uniqueCount = 0;
			totalCount = 0;
		}
	}

	/**
	 * doRangeStatistic
	 */
	private void doRangeStatistic() throws IOException {
		final double[] ranges = getRange();
		if (ranges == null) {
			return;
		}

		final FilterByPeptide fbp = option.getFilterByPeptide();

		for (int iMinCount = fbp.getFrom(); iMinCount <= fbp.getTo(); iMinCount += fbp
				.getStep()) {
			final File result_file = new File(resultDir, sourceFile.getName() + "."
					+ sphc.getPrinciple() + "."
					+ RcpaStringUtils.intToString(iMinCount, maxPeptideCountWidth)
					+ ".stat");
			PrintWriter pw = new PrintWriter(new FileWriter(result_file));

			CalculationItemList<E, F> currentItems = getCalculationItemPeptideCountLargeThan(iMinCount);

			ArrayList<HashMap<String, RangeValue>> statisticMapList = new ArrayList<HashMap<String, RangeValue>>();
			for (int i = 0; i <= ranges.length; i++) {
				HashMap<String, RangeValue> map = new HashMap<String, RangeValue>();
				statisticMapList.add(map);
				for (int j = 0; j < classifiedNames.length; j++) {
					map.put(classifiedNames[j], new RangeValue());
				}
			}

			for (int i = 0; i < currentItems.size(); i++) {
				double theoreticalValue = currentItems.get(i).getTheoreticalValue();
				HashMap<String, RangeValue> map = null;
				for (int j = 0; j < ranges.length; j++) {
					if (theoreticalValue < ranges[j]) {
						map = statisticMapList.get(j);
						break;
					}
				}
				if (map == null) {
					map = statisticMapList.get(statisticMapList.size() - 1);
				}

				for (int j = 0; j < classifiedNames.length; j++) {
					int iCount = currentItems.get(i).getClassifiedCount(
							classifiedNames[j]);
					if (iCount > 0) {
						RangeValue value = (RangeValue) map.get(classifiedNames[j]);
						value.totalCount = value.totalCount + iCount;
						value.uniqueCount = value.uniqueCount + 1;
					}
				}
			}

			// 输出unique分布
			pw.println("Unique " + option.getDistributionType() + " Distribution");
			printClassifiedNames(pw, false);
			pw.println();
			for (int i = 0; i < ranges.length + 1; i++) {
				if (i == 0) {
					pw.print("<" + ranges[i]);
				} else if (i == ranges.length) {
					pw.print(">=" + ranges[i - 1]);
				} else {
					pw.print(ranges[i - 1]);
					pw.print("--");
					pw.print(ranges[i]);
				}

				HashMap<String, RangeValue> map = statisticMapList.get(i);
				for (int j = 0; j < classifiedNames.length; j++) {
					pw.print("\t"
							+ ((RangeValue) map.get(classifiedNames[j])).uniqueCount);
				}
				pw.println();
			}

			// 输出total分布
			pw.println();
			pw.println(option.getDistributionType() + " Distribution");
			printClassifiedNames(pw, false);
			pw.println();
			for (int i = 0; i < ranges.length + 1; i++) {
				if (i == 0) {
					pw.print("<" + ranges[i]);
				} else if (i == ranges.length) {
					pw.print(">=" + ranges[i - 1]);
				} else {
					pw.print(ranges[i - 1]);
					pw.print("--");
					pw.print(ranges[i]);
				}

				HashMap<String, RangeValue> map = statisticMapList.get(i);
				for (int j = 0; j < classifiedNames.length; j++) {
					pw
							.print("\t"
									+ ((RangeValue) map.get(classifiedNames[j])).totalCount);
				}
				pw.println();
			}

			pw.close();
		}
	}

	private double[] getRange() throws FileNotFoundException, IOException {
		if (option.getClassificationInfo().getClassificationType() == ClassificationType.PI) {
			return StatisticRanges.getPIRange();
		}

		if (option.getClassificationInfo().getClassificationType() == ClassificationType.MW) {
			if (option.getDistributionType() == DistributionType.PROTEIN) {
				return StatisticRanges.getProteinMWRange();
			}

			return StatisticRanges.getPeptideMWRange();
		}

		return null;
	}

	protected void init(File optionFile) throws RcpaParseException, IOException,
			FileNotFoundException, ValidationException, MarshalException {
		option = (DistributionOption) DistributionOption.unmarshal(new FileReader(
				optionFile));

		if (option.getFilterByPeptide().getFrom() <= 0) {
			option.getFilterByPeptide().setFrom(1);
		}

		if (option.getDistributionType() == DistributionType.PEPTIDE) {
			option.getFilterByPeptide().setFilterType(FilterType.PEPTIDECOUNT);
		}

		fileType = IdentifiedResultFileType.valueOf(option.getSourceFile()
				.getFileType());

		resultDir = optionFile.getParentFile();

		sourceFile = new File(option.getSourceFile().getFileName());

		sphc = DistributionOptionUtils.getClassification(option);

		ExperimentValues = DistributionOptionUtils.getExperimentValues(option);

		classifiedNames = DistributionOptionUtils.getClassifiedNames(option);

		maxPeptideCountWidth = DistributionOptionUtils
				.getMaxPeptideCountWidth(option);
	}

	protected boolean needStatistic() {
		return ExperimentValues != null && ExperimentValues.size() != 0;
	}

	protected void printClassifiedNames(PrintWriter pw, boolean showRank) {
		for (int i = 0; i < classifiedNames.length; i++) {
			pw.print("\t" + classifiedNames[i]);
			if (showRank) {
				pw.print("\t" + classifiedNames[i] + "_rank");
			}
		}
	}

	protected void printClassifiedNames(PrintWriter pw, String appendix) {
		for (int i = 0; i < classifiedNames.length; i++) {
			pw.print("\t" + classifiedNames[i] + appendix);
			pw.print("\t" + classifiedNames[i] + appendix + "_rank");
		}
	}

	protected String getRank(HashMap<String, ArrayList<Integer>> map,
			String classifiedName, int count) {
		if (count == 0) {
			return "-";
		}

		ArrayList<Integer> intList = map.get(classifiedName);
		for (int i = 0; i < intList.size(); i++) {
			if (intList.get(i) == count) {
				return Integer.toString(i + 1);
			}
		}
		throw new IndexOutOfBoundsException(Integer.toString(count) + " is not in "
				+ intList);
	}

	/**
	 * 根据calculationItems中保存信息，计算在各个classifiedName中每个Object对应的肽段数量。
	 */
	private void calculatePeptideCount() {
		for (int i = 0; i < calculationItems.size(); i++) {
			final CalculationItem<E, F> item = calculationItems.get(i);
			item.classifyPeptideHit(sphc, classifiedNames, option
					.getFilterByPeptide().getFilterType());

			if (needStatistic()) {
				item.calculateExperimentalValue(ExperimentValues);
			}
		}

		calculatePeptideCountDistribution();
	}

	private void calculatePeptideCountDistribution() {
		uniquePeptideCounts.clear();
		peptideCounts.clear();

		for (int i = 0; i < classifiedNames.length; i++) {
			uniquePeptideCounts.put(classifiedNames[i], new ArrayList<Integer>());
			peptideCounts.put(classifiedNames[i], new ArrayList<Integer>());
		}

		for (int i = 0; i < calculationItems.size(); i++) {
			final CalculationItem item = calculationItems.get(i);
			for (int j = 0; j < classifiedNames.length; j++) {
				PeptideCount pepcount = item
						.getClassifiedPeptideCount(classifiedNames[j]);

				uniquePeptideCounts.get(classifiedNames[j]).add(
						pepcount.getUniquePeptideCount());

				peptideCounts.get(classifiedNames[j]).add(pepcount.getPeptideCount());
			}
		}

		Comparator<Integer> intComparator = new Comparator<Integer>() {
			public int compare(Integer i1, Integer i2) {
				return i2.compareTo(i1);
			}

			@Override
			public boolean equals(Object obj) {
				return false;
			}
		};

		for (int i = 0; i < classifiedNames.length; i++) {
			Collections.sort(uniquePeptideCounts.get(classifiedNames[i]),
					intComparator);
			Collections.sort(peptideCounts.get(classifiedNames[i]), intComparator);
		}
	}

	/**
	 * 对两次条件之间的差异进行分析
	 * 
	 * @param option
	 *          DistributionOption
	 * @throws IOException
	 */
	private void doDecreaseStatistic() throws IOException {
		final FilterByPeptide fbp = option.getFilterByPeptide();
		CalculationItemList<E, F> lastCalculationItems = getCalculationItemPeptideCountLargeThan(fbp
				.getFrom());

		final File decreaseDir = new File(resultDir, "decrease");

		for (int iMinCount = fbp.getFrom() + fbp.getStep(); iMinCount <= fbp
				.getTo(); iMinCount += fbp.getStep()) {
			if (!decreaseDir.exists()) {
				decreaseDir.mkdir();
			}

			final File result_file = new File(decreaseDir, sourceFile.getName() + "."
					+ sphc.getPrinciple() + "."
					+ RcpaStringUtils.intToString(iMinCount, maxPeptideCountWidth)
					+ ".decreased");
			CalculationItemList<E, F> currentItems = getCalculationItemPeptideCountLargeThan(iMinCount);
			for (int i = lastCalculationItems.size() - 1; i >= 0; i--) {
				for (int j = 0; j < currentItems.size(); j++) {
					if (lastCalculationItems.get(i).equals(currentItems.get(j))) {
						lastCalculationItems.remove(i);
						break;
					}
				}
			}

			writeItemObjects(result_file, lastCalculationItems);
			lastCalculationItems = currentItems;
		}
	}

	/**
	 * doDuplicationStatistic
	 * 
	 * @param option
	 *          DistributionOption
	 */
	private void doDuplicationStatistic() throws IOException {
		final FilterByPeptide fbp = option.getFilterByPeptide();

		for (int iMinCount = fbp.getFrom(); iMinCount <= fbp.getTo(); iMinCount += fbp
				.getStep()) {
			final File result_file = new File(resultDir, sourceFile.getName() + "."
					+ sphc.getPrinciple() + "."
					+ RcpaStringUtils.intToString(iMinCount, maxPeptideCountWidth)
					+ ".duplication.comparation");
			PrintWriter pw = new PrintWriter(new FileWriter(result_file));

			printClassifiedNames(pw, false);
			pw.println();

			CalculationItemList<E, F> currentItems = getCalculationItemPeptideCountLargeThan(iMinCount);

			printDuplicationOverlap(pw, currentItems, iMinCount);

			printDuplicationCount(pw, currentItems, iMinCount);

			pw.close();

			printDuplicationCorrespondingObjects(result_file, currentItems, iMinCount);
		}
	}

	/**
	 * printDuplicationCorrespondingObjects
	 * 
	 * @param result_file
	 *          File
	 * @param currentItems
	 *          CalculationItemList
	 */
	private void printDuplicationCorrespondingObjects(File result_file,
			CalculationItemList<E, F> currentItems, int iMinPeptideCount)
			throws IOException {
		iMinPeptideCount = iMinPeptideCount > 0 ? iMinPeptideCount : 1;
		ArrayList<CalculationItemList<E, F>> duplicatedObjs = new ArrayList<CalculationItemList<E, F>>();
		for (int i = 0; i < classifiedNames.length + 1; i++) {
			duplicatedObjs.add(new CalculationItemList<E, F>());
		}

		for (int k = 0; k < currentItems.size(); k++) {
			int iCount = 0;
			for (int j = 0; j < classifiedNames.length; j++) {
				final int pepCount = currentItems.get(k).getClassifiedCount(
						classifiedNames[j]);
				if (pepCount >= iMinPeptideCount) {
					iCount++;
				}
			}
			duplicatedObjs.get(iCount).add(currentItems.get(k));
		}

		for (int i = 1; i < duplicatedObjs.size(); i++) {
			final File resultFile = new File(result_file.getAbsolutePath() + ".COVER"
					+ i + ".fasta");
			writeItemObjectsFastaFormat(resultFile, duplicatedObjs.get(i));
		}
	}

	private void printDuplicationCount(PrintWriter pw,
			CalculationItemList<E, F> currentItems, int iMinPeptideCount) {
		iMinPeptideCount = iMinPeptideCount > 0 ? iMinPeptideCount : 1;

		int[] iDuplicatedCount = new int[classifiedNames.length + 1];
		Arrays.fill(iDuplicatedCount, 0);
		for (int k = 0; k < currentItems.size(); k++) {
			int iCount = 0;
			for (int j = 0; j < classifiedNames.length; j++) {
				if (currentItems.get(k).getClassifiedCount(classifiedNames[j]) >= iMinPeptideCount) {
					iCount++;
				}
			}
			iDuplicatedCount[iCount]++;
		}

		pw.println();
		pw.println("OverlapCount\tMatchedCount\tPercent");
		DecimalFormat df = new DecimalFormat("##.##");
		for (int i = iDuplicatedCount.length - 1; i > 0; i--) {
			pw.println(i
					+ "\t"
					+ iDuplicatedCount[i]
					+ "\t"
					+ df.format((double) iDuplicatedCount[i] * 100
							/ (double) currentItems.size()) + "%");
		}
	}

	private void printDuplicationOverlap(PrintWriter pw,
			CalculationItemList<E, F> currentItems, int iMinPeptideCount) {
		iMinPeptideCount = iMinPeptideCount > 0 ? iMinPeptideCount : 1;
		for (int i = 0; i < classifiedNames.length; i++) {
			pw.print(classifiedNames[i]);

			for (int j = 0; j < classifiedNames.length; j++) {
				int iCount = 0;
				for (int k = 0; k < currentItems.size(); k++) {
					final int count_i = currentItems.get(k).getClassifiedCount(
							classifiedNames[i]);
					final int count_j = currentItems.get(k).getClassifiedCount(
							classifiedNames[j]);
					if (count_i >= iMinPeptideCount && count_j >= iMinPeptideCount) {
						iCount++;
					}
				}
				pw.print("\t" + iCount);
			}
			pw.println();
		}
	}

	protected CalculationItemList<E, F> getCalculationItemPeptideCountLargeThan(
			int iMinCount) {
		CalculationItemList<E, F> currentItems = new CalculationItemList<E, F>();
		for (int i = 0; i < calculationItems.size(); i++) {
			CalculationItem<E, F> item = calculationItems.get(i);
			for (int j = 0; j < classifiedNames.length; j++) {
				if (item.getClassifiedCount(classifiedNames[j]) >= iMinCount) {
					currentItems.add(calculationItems.get(i));
					break;
				}
			}
		}
		return currentItems;
	}

	private void doStatisticBaseOnPeptideCount() throws IOException {
		final FilterByPeptide fbp = option.getFilterByPeptide();

		for (int iMinCount = fbp.getFrom(); iMinCount <= fbp.getTo(); iMinCount += fbp
				.getStep()) {
			final File result_file = new File(resultDir, sourceFile.getName() + "."
					+ sphc.getPrinciple() + "."
					+ RcpaStringUtils.intToString(iMinCount, maxPeptideCountWidth)
					+ ".distribution");
			PrintWriter pw = new PrintWriter(new FileWriter(result_file));

			printHeader(pw);

			pw.println();

			CalculationItemList<E, F> currentItems = getCalculationItemPeptideCountLargeThan(iMinCount);

			for (int i = 0; i < currentItems.size(); i++) {
				printItem(pw, currentItems.get(i));
				pw.println();
			}

			pw.close();
		}
	}

	private void doCorrelationStatistic() throws IOException {
		if (!needStatistic()) {
			return;
		}

		final File result_file = new File(resultDir, sourceFile.getName() + "."
				+ sphc.getPrinciple() + ".correl.distribution");
		PrintWriter pw = new PrintWriter(new FileWriter(result_file));
		pw.println("MinPepCount\tMatchedObjectCount\tCorrelation");

		final FilterByPeptide fbp = option.getFilterByPeptide();

		for (int iMinCount = fbp.getFrom(); iMinCount <= fbp.getTo(); iMinCount += fbp
				.getStep()) {
			CalculationItemList<E, F> currentItems = getCalculationItemPeptideCountLargeThan(iMinCount);

			final double correl = getCorrelationValue(currentItems);

			pw.println(iMinCount + "\t" + currentItems.size() + "\t" + correl);
		}

		pw.close();
	}

	/**
	 * 根据给定的CalculationItemList中的experimentValue和theoreticalValue，计算相关系数
	 * 
	 * @param currentItems
	 *          CalculationItemList
	 * @return double
	 */
	private double getCorrelationValue(CalculationItemList<E, F> currentItems) {
		double[] theoreticalPIs = new double[currentItems.size()];
		double[] experimentPIs = new double[currentItems.size()];
		for (int i = 0; i < currentItems.size(); i++) {
			theoreticalPIs[i] = currentItems.get(i).getTheoreticalValue();
			experimentPIs[i] = currentItems.get(i).getExperimentValue();
		}

		return BIJStats.correl(theoreticalPIs, experimentPIs);
	}

	protected void exportIndividualFractionFile() throws IOException {
	}

	/**
	 * 根据option中指定的sourceFile，读取并且解析为CalculationItemList。
	 */
	protected abstract void parseToCalculationItems() throws Exception;

	protected abstract void printHeader(PrintWriter pw);

	/**
	 * 打印item相关的信息，主要是标识、理论值、实验值、以及分布count数，应该与printHeader中定义的title一致。
	 * 
	 * @param pw
	 *          PrintWriter
	 * @param calculationItem
	 *          CalculationItem
	 */
	protected abstract void printItem(PrintWriter pw,
			CalculationItem<E, F> calculationItem);

	/**
	 * 输出一组CalculationItem中Object的实际内容到文件。
	 * 
	 * @param file
	 *          File
	 * @param items
	 *          CalculationItemList
	 */
	protected abstract void writeItemObjects(File file,
			CalculationItemList<E, F> items) throws IOException;

	/**
	 * printItemObjectsFastaFormat
	 * 
	 * @param pw
	 *          PrintWriter
	 * @param calculationItemList
	 *          CalculationItemList
	 */
	protected abstract void writeItemObjectsFastaFormat(File file,
			CalculationItemList<E, F> items) throws IOException;

	public void test() throws Exception {
		File optionFile = new File(
				"F:\\Science\\Data\\daijie\\20041123_Citric_TMA\\Peptide_PI_CLASSIFICATION\\total.noredundant.Peptide_PI.statistic.xml");

		init(optionFile);

		parseToCalculationItems();

		calculatePeptideCount();

		doStatisticBaseOnPeptideCount();

		doRangeStatistic();

		doCorrelationStatistic();

		doDuplicationStatistic();

		doDecreaseStatistic();
	}
}
