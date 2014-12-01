/*
 * Created on Jun 25, 2005
 */
package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.HypergeometricDistributionCalculator;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class GOAnnotationSpecialCategoryIdentifier implements IFileProcessor {
	private static DecimalFormat df = new DecimalFormat("00.0");

	private static DecimalFormat scienceDf = new DecimalFormat("0.###E0");

	private static String[] suffixes = { ".go_biological_process.tree",
			".go_molecular_function.tree", ".go_cellular_component.tree" };

	private GOAClassificationEntry[] totalGoEntries = new GOAClassificationEntry[3];

	public GOAnnotationSpecialCategoryIdentifier(String referenceDirectory) {
		for (int i = 0; i < suffixes.length; i++) {
			totalGoEntries[i] = new GOAClassificationEntry();

			File[] statFiles = new File(referenceDirectory)
					.listFiles(new SpecialIOFileFilter(suffixes[i], true));
			totalGoEntries[i].loadFromFile(statFiles[0].getAbsolutePath());
		}
	}

	private void calculatePercent(GOAClassificationEntry individualGoEntry,
			GOAClassificationEntry totalGoEntry, int sampleTotalCount,
			int referTotalCount) {
		if (totalGoEntry.getProteins().size() != 0) {
			final double percent = (double) individualGoEntry.getProteins().size()
					* 100 / totalGoEntry.getProteins().size();

			individualGoEntry.getAnnotations().put("Percent",
					df.format(percent) + "%");

			try {
				final double overProbability = HypergeometricDistributionCalculator
						.getInstance().pValueOfOverRepresentedByHypergeometric(
								individualGoEntry.getProteins().size(), sampleTotalCount,
								totalGoEntry.getProteins().size(), referTotalCount);
				individualGoEntry.getAnnotations().put("OverRepresentedProbability",
						scienceDf.format(overProbability));

			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException(individualGoEntry.getAccession()
						+ " : " + ex.getMessage());
			}

			for (IGOEntry child : individualGoEntry.getChildren()) {
				for (IGOEntry totalChild : totalGoEntry.getChildren()) {
					if (child.getAccession().equals(totalChild.getAccession())) {
						calculatePercent((GOAClassificationEntry) child,
								(GOAClassificationEntry) totalChild, sampleTotalCount,
								referTotalCount);
						break;
					}
				}
			}
		}
	}

	public List<String> process(String treeDirectory) throws Exception {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < suffixes.length; i++) {
			GOAClassificationEntry totalGoEntry = totalGoEntries[i];
			GOAClassificationEntry individualGoEntry = new GOAClassificationEntry();

			File[] treeFiles = new File(treeDirectory)
					.listFiles(new SpecialIOFileFilter(suffixes[i], true));
			for (File treeFile : treeFiles) {
				System.out.println(treeFile.getAbsolutePath());
				individualGoEntry.loadFromFile(treeFile.getAbsolutePath());
				calculatePercent(individualGoEntry, totalGoEntry, individualGoEntry
						.getProteins().size(), totalGoEntry.getProteins().size());
				final String resultFile = RcpaFileUtils.changeExtension(treeFile
						.getAbsolutePath(), "special.tree");
				individualGoEntry.saveToFile(resultFile);
				result.add(resultFile);
			}
		}

		result.addAll(new GOAnnotationToHtmlDirectoryProcessor()
				.process(treeDirectory));

		return result;
	}

	public static void main(String[] args) throws Exception {
		new GOAnnotationSpecialCategoryIdentifier(
				"F:\\Science\\Data\\ratdb\\STATISTIC")
				.process("F:\\Science\\Data\\HIPP\\summary\\ipi.RAT.3.04.mono\\hippocampi_2d_1.9_2.2_3.75_0.1\\Statistic");
	}
}
