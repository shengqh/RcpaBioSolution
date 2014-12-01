package cn.ac.rcpa.bio.tools.filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.processor.IFileProcessor;

public class IdentifiedPeptideInUniqueXProteinFilter implements IFileProcessor {
	public final static String version = "1.0.0";

	private int uniquePeptideCount;

	private int peptideCharge = 0;

	private boolean currentUniquePeptideCountOnly;

	public IdentifiedPeptideInUniqueXProteinFilter(int uniquePeptideCount) {
		this.uniquePeptideCount = uniquePeptideCount;
		this.currentUniquePeptideCountOnly = false;
	}

	public IdentifiedPeptideInUniqueXProteinFilter(int uniquePeptideCount,
			boolean currentUniquePeptideCountOnly) {
		this.uniquePeptideCount = uniquePeptideCount;
		this.currentUniquePeptideCountOnly = currentUniquePeptideCountOnly;
	}

	public IdentifiedPeptideInUniqueXProteinFilter(int uniquePeptideCount,
			boolean currentUniquePeptideCountOnly, int peptideChargeOnly) {
		this.uniquePeptideCount = uniquePeptideCount;
		this.currentUniquePeptideCountOnly = currentUniquePeptideCountOnly;
		this.peptideCharge = peptideChargeOnly;
	}

	public List<String> process(String originFile) throws Exception {
		System.out.println("Reading " + originFile + " ...");
		BufferedReader br = new BufferedReader(new FileReader(originFile));
		Set<String> peptides = new HashSet<String>();
		String peptideHeaders;

		Pattern p = Pattern
				.compile("\\t+.+?\\t.+?\\t[\\d.]+\\t[\\d.-]+\\t(\\d)\\t");

		try {
			br.readLine();
			peptideHeaders = br.readLine();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("$")) {
					break;
				}
			}

			int uniPepCount = Integer.MAX_VALUE;
			while (line != null) {
				String trimLine = line.trim();
				if (trimLine.length() == 0) {
					break;
				}

				if (trimLine.startsWith("$")) {
					String[] lines = trimLine.split("\t");
					uniPepCount = Integer.parseInt(lines[3].trim());
					if (uniPepCount < uniquePeptideCount) {
						break;
					}
				} else if (trimLine.startsWith("@")) {
					continue;
				} else {
					if ((!currentUniquePeptideCountOnly || uniPepCount == uniquePeptideCount)) {
						if (peptideCharge == 0) {
							peptides.add(line);
						} else {
							Matcher m = p.matcher(line);
							if (!m.find()) {
								continue;
							}

							if (Integer.parseInt(m.group(1)) == peptideCharge) {
								peptides.add(line);
							}
						}
					}
				}

				line = br.readLine();
			}
		} finally {
			br.close();
		}

		String resultFile = originFile + ".unique" + uniquePeptideCount;
		if (!currentUniquePeptideCountOnly) {
			resultFile += '+';
		}
		if (0 != peptideCharge) {
			resultFile += ".charge" + peptideCharge;
		}
		resultFile += ".peptides";

		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
		try {
			pw.println(peptideHeaders);
			for (String peptide : peptides) {
				pw.println(peptide);
			}
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

	public void setPeptideCharge(int peptideCharge) {
		this.peptideCharge = peptideCharge;
	}

	public int getPeptideCharge() {
		return peptideCharge;
	}
}
