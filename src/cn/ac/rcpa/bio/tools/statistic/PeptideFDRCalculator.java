package cn.ac.rcpa.bio.tools.statistic;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;

public class PeptideFDRCalculator implements IFileProcessor {
	private IPeptideFDRCalculator calculator;

	public PeptideFDRCalculator(IPeptideFDRCalculator calculator) {
		this.calculator = calculator;
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> peptides = IdentifiedResultIOFactory
				.readBuildSummaryPeptideHit(originFile);

		String resultFile = originFile + ".peptideFDR";
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			final DecimalFormat df = new DecimalFormat("##.##");
			pw.println();

			int[] charges = new int[] { 1, 2, 3 };
			pw.println("Charge\tFDR");
			for (int i = 0; i < charges.length; i++) {
				boolean identicalCharge = i < charges.length - 1;
				pw.println(charges[i]
						+ "\t"
						+ df.format(100 * calculator.calculate(peptides, charges[i],
								identicalCharge)) + "%");
			}
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

}
