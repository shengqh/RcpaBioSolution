package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.RcpaStringUtils;

public class PeptideDistributionCalculator extends
		AbstractDistributionCalculator<BuildSummaryPeptide, BuildSummaryPeptideHit> {
	private static DecimalFormat df4 = new DecimalFormat("0.####");

	private IValueCalculator calc;

	protected PeptideDistributionCalculator(IValueCalculator calc,
			boolean exportIndividual) {
		super("Peptide", calc.getTitle(), exportIndividual);
		this.calc = calc;
	}

	protected String getKey(BuildSummaryPeptideHit pephit) {
		return pephit.getPeptide(0).getSequence();
	}

	/**
	 * 读取文件，解析为CalculationItemList
	 * 
	 * @throws IOException
	 */
	@Override
	protected void parseToCalculationItems() throws Exception {
		List<BuildSummaryPeptideHit> pephits = DistributionOptionUtils
				.getPeptideHits(option);

		calc.sort(pephits);

		calculationItems.clear();

		Map<String, List<BuildSummaryPeptideHit>> pephitMap = new LinkedHashMap<String, List<BuildSummaryPeptideHit>>();
		for (BuildSummaryPeptideHit pephit : pephits) {
			String key = getKey(pephit);
			if (!pephitMap.containsKey(key)) {
				pephitMap.put(key, new ArrayList<BuildSummaryPeptideHit>());
			}
			pephitMap.get(key).add(pephit);
		}

		ArrayList<String> keys = new ArrayList<String>(pephitMap.keySet());
		for (String key : keys) {
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item = new CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit>();

			item.setKey(key);
			item.setPeptides(pephitMap.get(key));
			item.setTheoreticalValue(calc.getValue(item));

			calculationItems.add(item);
		}
	}

	protected void printKeyHeader(PrintWriter pw){
		pw.print("Sequence");
	}
	
	@Override
	protected void printHeader(PrintWriter pw) {
		printKeyHeader(pw);
		
		pw.print("\tTheoretical" + calc.getTitle());

		if (needStatistic()) {
			pw.print("\tExperiment" + calc.getTitle());
		}

		printClassifiedNames(pw, true);
	}

	protected void printKey(
			PrintWriter pw,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItem) {
		pw.print(calculationItem.getKey());
	}

	protected void printValuePair(
			PrintWriter pw,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItem) {
		pw.print("\t" + df4.format(calculationItem.getTheoreticalValue()));

		if (needStatistic()) {
			pw.print("\t" + df4.format(calculationItem.getExperimentValue()));
		}
	}

	@Override
	protected void printItem(
			PrintWriter pw,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItem) {
		printKey(pw, calculationItem);

		printValuePair(pw, calculationItem);

		printClassifiedPeptideCount(pw, calculationItem);
	}

	/**
	 * writeItemObjects
	 * 
	 * @param file
	 *          File
	 * @param items
	 *          CalculationItemList
	 */
	@Override
	protected void writeItemObjects(File file,
			CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> items)
			throws IOException {
		List<BuildSummaryPeptideHit> pephits = new ArrayList<BuildSummaryPeptideHit>();
		for (int i = 0; i < items.size(); i++) {
			pephits.addAll(items.get(i).getPeptides());
		}

		IdentifiedResultIOFactory.writeBuildSummaryPeptideHit(file
				.getAbsolutePath(), pephits);
	}

	/**
	 * writeItemObjectsFastaFormat
	 * 
	 * @param file
	 *          File
	 * @param items
	 *          CalculationItemList
	 */
	@Override
	protected void writeItemObjectsFastaFormat(File file,
			CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> items)
			throws IOException {
		return;
	}

	/**
	 * 输出每次条件下，每个fraction的peptides文件
	 * 
	 * @param option
	 *          DistributionOption
	 * @throws IOException
	 */
	@Override
	protected void exportIndividualFractionFile() throws IOException {
		final FilterByPeptide fbp = option.getFilterByPeptide();

		final File individualDir = new File(resultDir, "individual");

		for (int iMinCount = fbp.getFrom(); iMinCount <= fbp.getTo(); iMinCount += fbp
				.getStep()) {
			if (!individualDir.exists()) {
				individualDir.mkdir();
			}

			for (int i = 0; i < classifiedNames.length; i++) {
				final String keptClassifiedName = classifiedNames[i];
				final File result_file = new File(individualDir, RcpaFileUtils
						.changeExtension(sourceFile.getName(), "")
						+ "."
						+ RcpaStringUtils.intToString(iMinCount, maxPeptideCountWidth)
						+ "." + keptClassifiedName + ".peptides");
				CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> currentItems = getCalculationItemPeptideCountLargeThan(iMinCount);

				CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> classifiedItems = new CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit>();
				for (int j = 0; j < currentItems.size(); j++) {
					if (currentItems.get(j).getClassifiedCount(classifiedNames[i]) >= iMinCount) {
						CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> clonedItem = getCalculationItemContainClassifiedPeptideHitOnly(
								keptClassifiedName, currentItems.get(j));
						classifiedItems.add(clonedItem);
					}
				}
				writeItemObjects(result_file, classifiedItems);
			}
		}
	}

	protected void printClassifiedPeptideCount(
			PrintWriter pw,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItem) {
		for (int i = 0; i < classifiedNames.length; i++) {
			final PeptideCount count = (PeptideCount) calculationItem
					.getClassifiedPeptideCount(classifiedNames[i]);
			pw.print("\t" + count.getPeptideCount());
			pw
					.print("\t"
							+ getRank(peptideCounts, classifiedNames[i], count
									.getPeptideCount()));
		}
	}

	private CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> getCalculationItemContainClassifiedPeptideHitOnly(
			String keptClassifiedName,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item) {
		CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> result = new CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit>();
		result.setKey(item.getKey());
		for (BuildSummaryPeptideHit hit : item.getPeptides()) {
			if (keptClassifiedName.equals(sphc.getClassification(hit.getPeptide(0)))) {
				result.getPeptides().add(hit);
			}
		}

		return result;
	}

}
