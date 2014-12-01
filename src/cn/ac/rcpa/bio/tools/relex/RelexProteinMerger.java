package cn.ac.rcpa.bio.tools.relex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class RelexProteinMerger {
	public RelexProteinMerger() {
	}

	public static void merge(File[] proteinFiles, File resultFile,
			String normalizeDir, SequenceDatabaseType dbType) throws IOException {
		List<List<RelexProtein>> proteins = getRelexProteinFromFile(proteinFiles);

		Map<Set<String>, List<List<RelexProtein>>> proteinMap = getExperimentProteinMapping(proteins);

		List<Set<String>> sets = getSortedExperimentSetList(proteinMap);

		Set<String> totalSets = new HashSet<String>();
		for (Set<String> experimentList : sets) {
			totalSets.addAll(experimentList);
		}

		IAccessNumberParser parser = AccessNumberParserFactory.getParser(dbType);

		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));

		DecimalFormat df = new DecimalFormat("#.##");

		printHeader(totalSets, pw);

		for (Set<String> experimentList : sets) {
			List<List<RelexProtein>> proteinLists = proteinMap.get(experimentList);

			// pw.println(experimentList + "\t" + proteinLists.size());
			for (List<RelexProtein> proteinList : proteinLists) {
				double standardRatio = getStandardRatio(normalizeDir, proteinList);

				RelexProtein firstProtein = proteinList.get(0);

				pw.print(getProteinNames(firstProtein, parser) + "\t");
				pw.print(StringUtils
						.join(firstProtein.getReferences().iterator(), ", "));

				for (String experiment : totalSets) {
					boolean bFound = false;
					for (RelexProtein protein : proteinList) {
						if (protein.getDirectory().equals(experiment)) {
							pw.print("\t"
									+ df.format(Double.parseDouble(protein.getRatio())
											/ standardRatio) + "\t" + protein.getSd() + "\t"
									+ protein.getPeptideCount());
							bFound = true;
							break;
						}
					}

					if (!bFound) {
						pw.print("\t\t\t");
					}
				}
				pw.println();
			}
		}

		pw.println();
		pw.println("fraction\tprotein count");
		for (Set<String> experimentList : sets) {
			List<List<RelexProtein>> proteinLists = proteinMap.get(experimentList);
			pw.println(experimentList + "\t" + proteinLists.size());
		}

		pw.close();
	}

	private static String getProteinNames(RelexProtein relexProtein,
			IAccessNumberParser parser) {
		String result = "";
		List<String> proteins = relexProtein.getProteins();
		for (String protein : proteins) {
			if (result != "") {
				result += ", " + parser.getValue(protein);
			} else {
				result = parser.getValue(protein);
			}
		}
		return result;
	}

	private static double getStandardRatio(String normalizeDir,
			List<RelexProtein> proteinList) throws NumberFormatException {
		double result = 1.0;
		if (proteinList.size() > 2) {
			for (RelexProtein protein : proteinList) {
				if (protein.getDirectory().equals(normalizeDir)) {
					result = Double.parseDouble(protein.getRatio());
					if (result == 0.0) {
						result = 1.0;
					}
					break;
				}
			}
		}
		return result;
	}

	private static void printHeader(Set<String> totalSets, PrintWriter pw) {
		pw.print("\t");
		for (String experiment : totalSets) {
			pw.print("\t" + experiment + "\t\t");
		}
		pw.println();

		pw.print("ProteinID\tReference");
		for (int i = 0; i < totalSets.size(); i++) {
			pw.print("\tRatio\tSD\tPepCount");
		}
		pw.println();
	}

	private static List<Set<String>> getSortedExperimentSetList(
			Map<Set<String>, List<List<RelexProtein>>> proteinMap) {
		List<Set<String>> sets = new ArrayList<Set<String>>(proteinMap.keySet());
		Collections.sort(sets, new Comparator<Set<String>>() {
			@Override
			public boolean equals(Object obj) {
				return false;
			}

			public int compare(Set<String> set1, Set<String> set2) {
				return set2.size() - set1.size();
			}
		});
		return sets;
	}

	private static List<List<RelexProtein>> getRelexProteinFromFile(
			File[] proteinFiles) throws IOException {
		Map<String, List<RelexProtein>> result = new HashMap<String, List<RelexProtein>>();
		for (int i = 0; i < proteinFiles.length; i++) {
			final String dir = proteinFiles[i].getParentFile().getName();
			String[] lines = RcpaFileUtils
					.readFile(proteinFiles[i].getAbsolutePath());
			for (int j = 0; j < lines.length; j++) {
				if (RelexProtein.isRelexProtein(lines[j])) {
					RelexProtein protein = RelexProtein.parse(lines[j]);
					protein.setDirectory(dir);
					String proteinNames = StringUtils.join(protein.getProteins()
							.iterator(), ", ");
					if (!result.containsKey(proteinNames)) {
						result.put(proteinNames, new ArrayList<RelexProtein>());
					}
					List<RelexProtein> list = result.get(proteinNames);
					list.add(protein);
				}
			}
		}
		Map<String, List<RelexProtein>> proteins = result;

		List<List<RelexProtein>> values = new ArrayList<List<RelexProtein>>(
				proteins.values());
		return values;
	}

	private static Map<Set<String>, List<List<RelexProtein>>> getExperimentProteinMapping(
			List<List<RelexProtein>> values) {
		Map<Set<String>, List<List<RelexProtein>>> result = new HashMap<Set<String>, List<List<RelexProtein>>>();
		for (List<RelexProtein> relexProteins : values) {
			final HashSet<String> set = new HashSet<String>();

			for (RelexProtein protein : relexProteins) {
				set.add(protein.getDirectory());
			}

			if (!result.containsKey(set)) {
				result.put(set, new ArrayList<List<RelexProtein>>());
			}
			List<List<RelexProtein>> proteinList = result.get(set);
			proteinList.add(relexProteins);
		}
		return result;
	}

	public static void merge(File rootDirectory, File resultFile,
			String normalizeDir, SequenceDatabaseType dbType) throws IOException {
		File[] dirs = RcpaFileUtils.getSubDirectories(rootDirectory);
		ArrayList<File> files = new ArrayList<File>();
		for (int i = 0; i < dirs.length; i++) {
			File proteinFile = new File(dirs[i], "RelEx-Protein.txt");
			if (proteinFile.exists()) {
				files.add(proteinFile);
			}
		}
		merge((File[]) files.toArray(new File[0]), resultFile, normalizeDir, dbType);
	}

	public static void main(String[] args) throws IOException {
		RelexProteinMerger.merge(new File("F:\\Science\\Data\\caoxj\\3T3L1"),
				new File("F:\\Science\\Data\\caoxj\\3T3L1\\merge.xls"), "CI2_merge",
				SequenceDatabaseType.IPI);
	}
}
