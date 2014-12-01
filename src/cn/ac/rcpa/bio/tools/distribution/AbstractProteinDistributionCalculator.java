package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IIdentifiedResult;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.io.impl.FastaResultWriter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.RcpaStringUtils;

abstract public class AbstractProteinDistributionCalculator extends
		AbstractDistributionCalculator<BuildSummaryPeptide, BuildSummaryPeptideHit> {
	protected IAccessNumberParser parser;

	@Override
	protected void init(File optionFile) throws MarshalException,
			ValidationException, IOException, RcpaParseException {
		super.init(optionFile);
		final SequenceDatabaseType dbType = SequenceDatabaseType.valueOf(option.getDatabaseType());
		parser = AccessNumberParserFactory.getParser(dbType);
	}

	protected AbstractProteinDistributionCalculator(String theoreticalValueTitle,
			boolean exportIndividual) {
		super("Protein", theoreticalValueTitle, exportIndividual);
	}

	protected List<BuildSummaryProteinGroup> getSortedProteinGroups()
			throws RcpaParseException, IOException {
		final BuildSummaryResult sr = IdentifiedResultIOFactory
				.readBuildSummaryResult(option.getSourceFile().getFileName());

		ArrayList<BuildSummaryProteinGroup> result = new ArrayList<BuildSummaryProteinGroup>(
				sr.getProteinGroups());

		sortIdentifiedProtein(result);

		return result;
	}

	/**
	 * parseToCalculationItems
	 */
	@Override
	protected void parseToCalculationItems() throws IOException,
			RcpaParseException {
		List<BuildSummaryProteinGroup> prohits = getSortedProteinGroups();

		calculationItems.clear();

		for (BuildSummaryProteinGroup proteinGroup : prohits) {
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item = new CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit>();

			item.setKey(proteinGroup);
			item.setPeptides(proteinGroup.getPeptideHits());

			setTheoreticalValue(item, proteinGroup);

			calculationItems.add(item);
		}
	}

	@Override
	protected void printHeader(PrintWriter pw) {
		pw.print("Protein\tDescription");

		if (needStatistic()) {
			pw.print("\t" + theoreticalValueTitle + "\tExperimentValue");
		}

		printClassifiedNames(pw, "_UniPepCount");

		printClassifiedNames(pw, "_PepCount");
	}

	/**
	 * printItem
	 * 
	 * @param pw
	 *          PrintWriter
	 * @param calculationItem
	 *          CalculationItem
	 */
	@Override
	protected void printItem(
			PrintWriter pw,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItem) {
		BuildSummaryProteinGroup progroup = (BuildSummaryProteinGroup) calculationItem
				.getKey();

		for (int i = 0; i < progroup.getProteinCount(); i++) {
			BuildSummaryProtein prohit = progroup.getProtein(i);

			if (i != 0) {
				pw.print(" ! ");
			}
			pw.print(parser.getValue(prohit.getProteinName()));
		}

		pw.print("\t");
		for (int i = 0; i < progroup.getProteinCount(); i++) {
			BuildSummaryProtein prohit = progroup.getProtein(i);

			if (i != 0) {
				pw.print(" ! ");
			}

			pw.print(SequenceUtils.getProteinReference(prohit.getReference()));
		}

		if (needStatistic()) {
			pw.print("\t" + calculationItem.getTheoreticalValue() + "\t"
					+ calculationItem.getExperimentValue());
		}

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
		final BuildSummaryResult sr = DistributionOptionUtils
				.getIdentifiedResultFromProteinCalculationItemList(items, fileType);

		IdentifiedResultIOFactory.writeBuildSummaryResult(file.getAbsolutePath(),
				sr);
	}

	@Override
	protected void writeItemObjectsFastaFormat(File file,
			CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> items)
			throws IOException {
		final IIdentifiedResult sr = DistributionOptionUtils
				.getIdentifiedResultFromProteinCalculationItemList(items, fileType);

		final FastaResultWriter writer = IdentifiedResultIOFactory.getFastaWriter();

		writer.write(file.getAbsolutePath(), sr);
	}

	/**
	 * 输出每次条件下，每个fraction的protein group文件
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
						+ "."
						+ keptClassifiedName
						+ "."
						+ FilenameUtils.getExtension(sourceFile.getName()));
				CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> currentItems = getCalculationItemPeptideCountLargeThan(iMinCount);

				CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> classifiedItems = new CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit>();
				for (int j = 0; j < currentItems.size(); j++) {
					if (currentItems.get(j).getClassifiedCount(classifiedNames[i]) >= iMinCount) {
						BuildSummaryProteinGroup group = (BuildSummaryProteinGroup) currentItems
								.get(j).getKey();
						BuildSummaryProteinGroup clonedGroup = getGroupContainClassifiedPeptideHitOnly(
								keptClassifiedName, group);

						CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item = new CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit>();
						item.setKey(clonedGroup);
						classifiedItems.add(item);
					}
				}
				writeItemObjects(result_file, classifiedItems);
			}
		}
	}

	private BuildSummaryProteinGroup getGroupContainClassifiedPeptideHitOnly(
			final String keptClassifiedName, BuildSummaryProteinGroup group) {
		BuildSummaryProteinGroup result = (BuildSummaryProteinGroup) group.clone();

		List<BuildSummaryPeptide> peptides = new ArrayList<BuildSummaryPeptide>(
				result.getProtein(0).getPeptides());

		/**
		 * 有时候，会产生一个group中一个蛋白包含两个peptide，这两个peptide对应同一个
		 * filename，同时该group中另一个蛋白质包含其中一个peptide，因此删除的时候需
		 * 要按照filename来删除，而不是直接通过index。
		 * 
		 * <code>
		 * ZhouHu_Ins_IMDL_pH4_050911,12683    K.LPEGQLPEAELPAAKAAAAAGAGLK.G ! K.LPEGQLPEAELPAAQAAAAAGAGLK.G   2345.63750  -1.63250    2   1   2.9674  0.1740  168.2   33  13|48   IPI:IPI00381608.3|REFSEQ_XP:XP_356602|ENSEMBL:ENSMUSP00000076373 ! IPI:IPI00381608.3|REFSEQ_XP:XP_356602|ENSEMBL:ENSMUSP00000076373/IPI:IPI00467482.1|ENSEMBL:ENSMUSP00000078256/IPI:IPI00475424.1|ENSEMBL:ENSMUSP00000047768       4.79    1   3
		 * </code>
		 */
		for (BuildSummaryPeptide pep : peptides) {
			if (!sphc.getClassification(pep).equals(keptClassifiedName)) {
				String longfilename = pep.getPeakListInfo().getLongFilename();
				for (int l = 0; l < result.getProteinCount(); l++) {
					result.getProtein(l).removePeptide(longfilename);
				}
			}
		}
		return result;
	}

	protected void printClassifiedPeptideCount(
			PrintWriter pw,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItem) {
		for (int i = 0; i < classifiedNames.length; i++) {
			final PeptideCount count = (PeptideCount) calculationItem
					.getClassifiedPeptideCount(classifiedNames[i]);
			pw.print("\t" + count.getUniquePeptideCount());
			pw.print("\t"
					+ getRank(uniquePeptideCounts, classifiedNames[i], count
							.getUniquePeptideCount()));
		}

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

	protected abstract void sortIdentifiedProtein(
			List<BuildSummaryProteinGroup> proteinHits);

	protected abstract void setTheoreticalValue(
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item,
			BuildSummaryProteinGroup proteinGroup);
}
