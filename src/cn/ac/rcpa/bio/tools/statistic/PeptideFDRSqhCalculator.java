package cn.ac.rcpa.bio.tools.statistic;

import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;

public class PeptideFDRSqhCalculator extends AbstractPeptideFDRCalculator {
	public PeptideFDRSqhCalculator(String decoyDbPattern) {
		super(decoyDbPattern);
	}

	public double calculate(List<? extends IIdentifiedPeptideHit> peptides) {
		double incorrect = 0;
		double correct = 0;

		for (IIdentifiedPeptideHit peptide : peptides) {
			if (decoyDbFilter.accept(peptide)) {
				incorrect += 1;
			} else {
				correct += 1;
			}
		}

		return (incorrect >= correct) ? 1.0 : (double) incorrect / correct;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Num(Decoy) / Num(Target)";
	}
}
