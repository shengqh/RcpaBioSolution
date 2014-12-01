package cn.ac.rcpa.bio.tools.statistic;

import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideChargeFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideChargePlusFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideHitFilterByPeptideFilter;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.filter.IFilter;

abstract public class AbstractPeptideFDRCalculator implements
		IPeptideFDRCalculator {
	protected IFilter<IIdentifiedPeptideHit> decoyDbFilter;

	public AbstractPeptideFDRCalculator(String decoyDbPattern) {
		decoyDbFilter = new ReversedDbPeptideHitFilter(decoyDbPattern);
	}

	public AbstractPeptideFDRCalculator(IFilter<IIdentifiedPeptideHit> filter) {
		this.decoyDbFilter = filter;
	}

	public double calculate(List<? extends IIdentifiedPeptideHit> peptides,
			int charge, boolean identicalCharge) {
		IFilter<IIdentifiedPeptideHit> hitFilter;

		if (identicalCharge) {
			hitFilter = new IdentifiedPeptideHitFilterByPeptideFilter(
					new IdentifiedPeptideChargeFilter(charge));
		} else {
			hitFilter = new IdentifiedPeptideHitFilterByPeptideFilter(
					new IdentifiedPeptideChargePlusFilter(charge));
		}

		return calculate(PeptideUtils.getSubset(peptides, hitFilter));
	}
}
