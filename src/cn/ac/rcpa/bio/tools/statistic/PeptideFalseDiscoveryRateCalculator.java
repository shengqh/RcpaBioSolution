package cn.ac.rcpa.bio.tools.statistic;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.utils.Pair;

public class PeptideFalseDiscoveryRateCalculator implements IFileProcessor {
	public static String version = "1.0.0";

	private Pattern decoyPattern;

	public PeptideFalseDiscoveryRateCalculator(String decoyDbPattern) {
		super();
		this.decoyPattern = Pattern.compile(decoyDbPattern);
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> peptides = IdentifiedResultIOFactory
				.readBuildSummaryPeptideHit(originFile);

		List<BuildSummaryPeptide> uniquePeptides = PeptideUtils
				.getUniquePeptides(peptides);

		Pair<Integer, Integer> fdr = calculate1(peptides);
		Pair<Integer, Integer> uniqueFdr = calculate2(uniquePeptides);

		String resultFile = originFile + ".fdr";
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			DecimalFormat df2 = new DecimalFormat("0.00");
			pw.println("decoy  = " + fdr.snd);
			pw.println("target = " + fdr.fst);
			pw.println("fdr    = "
					+ df2.format((fdr.snd * 2.0 * 100) / (fdr.fst + fdr.snd)) + "%");

			pw.println("unique_decoy  = " + uniqueFdr.snd);
			pw.println("unique_target = " + uniqueFdr.fst);
			pw.println("unique_fdr    = "
					+ df2.format((uniqueFdr.snd * 2.0 * 100) / (uniqueFdr.fst + uniqueFdr.snd)) + "%");
		} finally {
			pw.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}

	private Pair<Integer, Integer> calculate2(
			List<BuildSummaryPeptide> uniquePeptides) {
		int target = 0;
		int decoy = 0;
		for (BuildSummaryPeptide pep : uniquePeptides) {
			boolean isDecoy = false;
			for (String protein : pep.getProteinNames()) {
				if (decoyPattern.matcher(protein).find()) {
					isDecoy = true;
					break;
				}
			}
			if (isDecoy) {
				decoy++;
			} else {
				target++;
			}
		}
		return new Pair<Integer, Integer>(target, decoy);
	}

	private Pair<Integer, Integer> calculate1(
			List<BuildSummaryPeptideHit> peptides) {
		List<BuildSummaryPeptide> peps = new ArrayList<BuildSummaryPeptide>();
		for (BuildSummaryPeptideHit hit : peptides) {
			peps.add(hit.getPeptide(0));
		}
		return calculate2(peps);
	}

	public static void main(String[] args) throws Exception {
		new PeptideFalseDiscoveryRateCalculator("REVERSE")
				.process("F:\\sqh\\Project\\yuanchao\\shotgun\\summary_new\\summary.20ppm_new.peptides");
		new PeptideFalseDiscoveryRateCalculator("REVERSE")
				.process("F:\\sqh\\Project\\yuanchao\\shotgun\\summary_new\\summary.7ppm_new.peptides");
	}

}
