package cn.ac.rcpa.bio.tools.statistic;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;

public class ProteinFalseDiscoveryRateCalculator implements IFileProcessor {
	private Pattern decoyPattern;

	private int uniquePeptideCount = 2;

	public ProteinFalseDiscoveryRateCalculator(String decoyDbPattern,
			int uniquePeptideCount) {
		super();
		this.decoyPattern = Pattern.compile(decoyDbPattern);
		this.uniquePeptideCount = uniquePeptideCount;
	}

	public List<String> process(String originFile) throws Exception {
		BuildSummaryResult br = new BuildSummaryResultReader().readOnly(originFile);

		// protein level
		int[] proteinTarget = new int[uniquePeptideCount + 1];
		int[] proteinDecoy = new int[uniquePeptideCount + 1];

		// group level
		int[] groupTarget = new int[uniquePeptideCount + 1];
		int[] groupDecoy = new int[uniquePeptideCount + 1];

		for (int i = 0; i <= uniquePeptideCount; i++) {
			proteinTarget[i] = 0;
			proteinDecoy[i] = 0;
			groupTarget[i] = 0;
			groupDecoy[i] = 0;
		}

		for (BuildSummaryProteinGroup group : br.getProteinGroups()) {
			int unique = group.getProtein(0).getUniquePeptides().length;
			int proteinIndex = unique >= uniquePeptideCount ? uniquePeptideCount
					: unique;

			boolean bDecoy = false;
			for (String proteinName : group.getProteinNames()) {
				if (decoyPattern.matcher(proteinName).find()) {
					proteinDecoy[proteinIndex]++;
					bDecoy = true;
				} else {
					proteinTarget[proteinIndex]++;
				}
			}

			if (bDecoy) {
				groupDecoy[proteinIndex]++;
			} else {
				groupTarget[proteinIndex]++;
			}
		}

		String resultFile = originFile + ".fdr";
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			pw.println("\tDecoy\tTarget\tFDR");

			printResult(pw, "protein", proteinTarget, proteinDecoy);
			printResult(pw, "group", groupTarget, groupDecoy);

		} finally {
			pw.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}

	private void printResult(PrintWriter pw, String session, int[] target,
			int[] decoy) {
		DecimalFormat df2 = new DecimalFormat("0.00");

		int targetTotal = sum(target);
		int decoyTotal = sum(decoy);
		printIt(pw, df2, session, "", targetTotal, decoyTotal);

		for (Integer index = 1; index < target.length; index++) {
			printIt(pw, df2, session, index.toString(), target[index], decoy[index]);
		}
	}

	private void printIt(PrintWriter pw, DecimalFormat df2, String session,
			String suffix, int targetTotal, int decoyTotal) {
		pw.println(session + suffix + "\t" + decoyTotal + "\t" + targetTotal + "\t"
				+ df2.format((decoyTotal * 2.0 * 100) / (targetTotal + decoyTotal))
				+ "%");
	}

	private int sum(int[] target) {
		int result = 0;
		for (int t : target) {
			result += t;
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		new ProteinFalseDiscoveryRateCalculator("REVERSE", 2)
				.process("F:\\sqh\\Project\\yuanchao\\shotgun\\summary_new\\summary.20ppm_new.noredundant");
		new ProteinFalseDiscoveryRateCalculator("REVERSE", 2)
				.process("F:\\sqh\\Project\\yuanchao\\shotgun\\summary_new\\summary.7ppm_new.noredundant");
	}

}
