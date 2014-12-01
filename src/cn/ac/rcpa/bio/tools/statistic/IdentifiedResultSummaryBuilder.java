package cn.ac.rcpa.bio.tools.statistic;

import java.io.BufferedReader;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class IdentifiedResultSummaryBuilder implements IFileProcessor {
	public enum ValueType {
		SCAN_COUNT, DTA_COUNT, PEPTIDE_COUNT, UNIQUE_PEPTIDE_COUNT, GROUP_COUNT, GROUP_UNIQUE_2_COUNT, GROUP_UNIQUE_2_PERCENT, PROTEIN_COUNT, PROTEIN_UNIQUE_2_COUNT, PROTEIN_UNIQUE_2_PERCENT, NOREDUNDANT_GROUP_COUNT, NOREDUNDANT_GROUP_UNIQUE_2_COUNT, NOREDUNDANT_GROUP_UNIQUE_2_PERCENT, NOREDUNDANT_PROTEIN_COUNT, NOREDUNDANT_PROTEIN_UNIQUE_2_COUNT, NOREDUNDANT_PROTEIN_UNIQUE_2_PERCENT
	}

	public IdentifiedResultSummaryBuilder() {
	}

	String getScanCount(String logFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(logFile));
		try {
			String line;
			final Pattern spectraPattern = Pattern.compile("TotalScan\\s*(\\d+)$");
			while ((line = br.readLine()) != null) {
				Matcher matcher = spectraPattern.matcher(line);
				if (matcher.find()) {
					return matcher.group(1);
				}
			}
		} finally {
			br.close();
		}
		return "";
	}

	String getDtaCount(String logFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(logFile));
		try {
			String line;
			final Pattern spectraPattern = Pattern
					.compile("(?:Total\\s*|TotalDta\\s*)(\\d+)$");
			while ((line = br.readLine()) != null) {
				Matcher matcher = spectraPattern.matcher(line);
				if (matcher.find()) {
					return matcher.group(1);
				}
			}
		} finally {
			br.close();
		}
		return "";
	}

	public void parsePeptideCount(Map<ValueType, String> values,
			String peptideFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(peptideFile));
		try {
			String line;
			Pattern peptidePattern = Pattern.compile("Total peptides:\\s(\\d+)");
			Pattern uniquePeptidePattern = Pattern
					.compile("Unique peptides:\\s(\\d+)");
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}
			}

			while ((line = br.readLine()) != null) {
				Matcher peptideMatcher = peptidePattern.matcher(line);
				if (peptideMatcher.find()) {
					values.put(ValueType.PEPTIDE_COUNT, peptideMatcher.group(1));
					continue;
				}

				Matcher uniquePeptideMatcher = uniquePeptidePattern.matcher(line);
				if (uniquePeptideMatcher.find()) {
					values.put(ValueType.UNIQUE_PEPTIDE_COUNT, uniquePeptideMatcher
							.group(1));
					break;
				}
			}
		} finally {
			br.close();
		}
	}

	IdentificationCount getIdentificationCount(String proteinFile)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(proteinFile));
		IdentificationCount result = new IdentificationCount();
		try {
			String line;
			final Pattern proteinPattern = Pattern
					.compile("Total protein\\s+:\\s(\\d+)");
			final Pattern proteinGroupPattern = Pattern
					.compile("Total protein group\\s+:\\s(\\d+)");
			final Pattern unique1Pattern = Pattern
					.compile("^1\\s+(\\d+)\\s+([\\d|\\.]+)%\\s+(\\d+)\\s+([\\d|\\.]+)%");
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}
			}

			boolean unique1Find = false;
			while ((line = br.readLine()) != null) {
				Matcher proteinMatcher = proteinPattern.matcher(line);
				if (proteinMatcher.find()) {
					result.setProteinCount(Integer.parseInt(proteinMatcher.group(1)));
					continue;
				}

				Matcher proteinGroupMatcher = proteinGroupPattern.matcher(line);
				if (proteinGroupMatcher.find()) {
					result.setGroupCount(Integer.parseInt(proteinGroupMatcher.group(1)));
					continue;
				}

				Matcher unique1Matcher = unique1Pattern.matcher(line);
				if (unique1Matcher.find()) {
					unique1Find = true;
					result.setGroupUnique2Count(result.getGroupCount()
							- Integer.parseInt(unique1Matcher.group(1)));
					result.setProteinUnique2Count(result.getProteinCount()
							- Integer.parseInt(unique1Matcher.group(3)));
					break;
				}
			}

			if (!unique1Find) {
				result.setGroupUnique2Count(result.getGroupCount());
				result.setProteinUnique2Count(result.getProteinCount());
			}
		} finally {
			br.close();
		}
		return result;
	}

	private void parseInformation(File[] subdirs, String resultFile)
			throws FileNotFoundException, IOException, IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
		DecimalFormat df = new DecimalFormat("00.00");
		try {
			Map<String, Map<ValueType, String>> allValues = new HashMap<String, Map<ValueType, String>>();
			for (File subdir : subdirs) {
				File[] files = subdir.listFiles(new SpecialIOFileFilter("param", true));
				String prefix = files.length > 1 ? subdir.getName() + "_" : "";
				for (File file : files) {
					System.out.println(file.getAbsolutePath());

					Map<ValueType, String> values = new HashMap<ValueType, String>();
					final String filename = prefix
							+ FilenameUtils.removeExtension(file.getName());
					allValues.put(filename, values);

					String sequestLogFilename = RcpaFileUtils.changeExtension(file
							.getAbsolutePath(), "sequest.log");
					if (new File(sequestLogFilename).exists()) {
						String scanCount = getScanCount(sequestLogFilename);
						if (scanCount.length() > 0) {
							values.put(ValueType.SCAN_COUNT, scanCount);
						}
						values.put(ValueType.DTA_COUNT, getDtaCount(sequestLogFilename));
					}

					parsePeptideCount(values, RcpaFileUtils.changeExtension(file
							.getAbsolutePath(), "peptides"));

					final IdentificationCount proteinCount = getIdentificationCount(RcpaFileUtils
							.changeExtension(file.getAbsolutePath(), "proteins"));
					values.put(ValueType.GROUP_COUNT, Integer.toString(proteinCount
							.getGroupCount()));
					values.put(ValueType.GROUP_UNIQUE_2_COUNT, Integer
							.toString(proteinCount.getGroupUnique2Count()));
					values.put(ValueType.GROUP_UNIQUE_2_PERCENT, df.format(proteinCount
							.getGroupUnique2Percent())
							+ "%");
					values.put(ValueType.PROTEIN_COUNT, Integer.toString(proteinCount
							.getProteinCount()));
					values.put(ValueType.PROTEIN_UNIQUE_2_COUNT, Integer
							.toString(proteinCount.getProteinUnique2Count()));
					values.put(ValueType.PROTEIN_UNIQUE_2_PERCENT, df.format(proteinCount
							.getProteinUnique2Percent())
							+ "%");

					final IdentificationCount noredundantCount = getIdentificationCount(RcpaFileUtils
							.changeExtension(file.getAbsolutePath(), "noredundant"));
					values.put(ValueType.NOREDUNDANT_GROUP_COUNT, Integer
							.toString(noredundantCount.getGroupCount()));
					values.put(ValueType.NOREDUNDANT_GROUP_UNIQUE_2_COUNT, Integer
							.toString(noredundantCount.getGroupUnique2Count()));
					values.put(ValueType.NOREDUNDANT_GROUP_UNIQUE_2_PERCENT, df
							.format(noredundantCount.getGroupUnique2Percent())
							+ "%");
					values.put(ValueType.NOREDUNDANT_PROTEIN_COUNT, Integer
							.toString(noredundantCount.getProteinCount()));
					values.put(ValueType.NOREDUNDANT_PROTEIN_UNIQUE_2_COUNT, Integer
							.toString(noredundantCount.getProteinUnique2Count()));
					values.put(ValueType.NOREDUNDANT_PROTEIN_UNIQUE_2_PERCENT, df
							.format(noredundantCount.getProteinUnique2Percent())
							+ "%");
				}
			}
			pw.print("File");
			List<String> filenames = new ArrayList<String>(allValues.keySet());
			Collections.sort(filenames);

			for (String filename : filenames) {
				pw.print("\t" + filename);
			}
			pw.println();

			for (ValueType aType : ValueType.values()) {
				boolean bFound = false;
				for (Map<ValueType, String> value : allValues.values()) {
					if (value.containsKey(aType)) {
						bFound = true;
						break;
					}
				}

				if (!bFound) {
					continue;
				}

				pw.print(aType);
				for (String filename : filenames) {
					final String value = allValues.get(filename).get(aType);
					if (value != null) {
						pw.print("\t" + value);
					} else {
						pw.print("\t");
					}
				}
				pw.println();
			}
		} finally {
			pw.close();
		}
	}

	private File[] getSubDirectories(File dir) {
		List<File> result = new ArrayList<File>(Arrays.asList(RcpaFileUtils
				.getSubDirectories(dir)));
		result.add(dir);
		return result.toArray(new File[0]);
	}

	public List<String> process(String dirname) throws Exception {
		final File dir = new File(dirname);
		final File[] subdirs = getSubDirectories(dir);
		final String resultFile = dir.getAbsolutePath() + "/" + dir.getName()
				+ ".xls";

		parseInformation(subdirs, resultFile);
		System.out.println("Finished!");
		return Arrays.asList(new String[] { resultFile });
	}

}
