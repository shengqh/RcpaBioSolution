package cn.ac.rcpa.bio.tools.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class SubstractPeptideBuilder implements IFileProcessor {
	public static String version = "1.0.0";

	private String subPeptideFile;

	private boolean ignoreMissPeptide = false;

	public SubstractPeptideBuilder(String subPeptideFile) {
		super();
		this.subPeptideFile = subPeptideFile;
	}

	public SubstractPeptideBuilder(String subPeptideFile,
			boolean ignoreMissPeptide) {
		super();
		this.subPeptideFile = subPeptideFile;
		this.ignoreMissPeptide = ignoreMissPeptide;
	}

	public static void remove(String originFile, String resultFilename,
			Set<String> removeShortFilenames, boolean ignoreMissPeptide)
			throws Exception {
		Set<String> actualRemoveFilename = new HashSet<String>();

		PrintWriter pw = new PrintWriter(resultFilename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(originFile));

			String line = br.readLine();
			pw.println(line);

			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}

				String[] parts = line.split("\t");
				if (removeShortFilenames.contains(parts[1])) {
					if (!actualRemoveFilename.contains(parts[1])) {
						actualRemoveFilename.add(parts[1]);
					}
					continue;
				}

				pw.println(line);
			}
		} finally {
			pw.close();
		}

		if (!ignoreMissPeptide
				&& !removeShortFilenames.equals(actualRemoveFilename)) {
			throw new IllegalArgumentException(
					"Some of file didn't exist in main peptide file");
		}
	}

	public List<String> process(String originFile) throws Exception {
		Set<String> removeFilenames = getShortFilenames(subPeptideFile);

		String resultFilename = RcpaFileUtils.changeExtension(originFile,
				".extracted.peptides");

		remove(originFile, resultFilename, removeFilenames, this.ignoreMissPeptide);

		File originEnzymeFile = new File(originFile + ".enzyme");
		if (originEnzymeFile.exists()) {
			File resultEnzymeFile = new File(resultFilename + ".enzyme");
			FileUtils.copyFile(originEnzymeFile, resultEnzymeFile);
		}

		return Arrays.asList(new String[] { resultFilename });
	}

	public static Set<String> getShortFilenames(String peptideFilename)
			throws Exception {
		Set<String> result = new HashSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(peptideFilename));
		try {
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}

				String[] parts = line.split("\t");
				if (!result.contains(parts[1])) {
					result.add(parts[1]);
				}
			}
		} finally {
			br.close();
		}
		return result;
	}
}
