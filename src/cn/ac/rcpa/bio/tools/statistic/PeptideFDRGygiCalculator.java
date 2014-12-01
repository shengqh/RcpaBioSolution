package cn.ac.rcpa.bio.tools.statistic;

import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.filter.IFilter;

public class PeptideFDRGygiCalculator extends AbstractPeptideFDRCalculator {
	public PeptideFDRGygiCalculator(String decoyDbPattern) {
		super(decoyDbPattern);
	}

	public PeptideFDRGygiCalculator(IFilter<IIdentifiedPeptideHit> filter) {
		super(filter);
	}

	public double calculate(List<? extends IIdentifiedPeptideHit> peptides) {
		if (0 == peptides.size()) {
			return 0.0;
		}

		double incorrect = 0;

		for (IIdentifiedPeptideHit peptide : peptides) {
			if (decoyDbFilter.accept(peptide)) {
				incorrect += 1;
			}
		}

		double result = (2.0 * incorrect) / peptides.size();
		if (result > 1.0) {
			result = 1.0;
		}

		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Num(Decoy) * 2 / [Num(Decoy) + Num(Target)]";
	}
	
	
}
