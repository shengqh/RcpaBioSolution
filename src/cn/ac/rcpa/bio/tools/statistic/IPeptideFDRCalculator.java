package cn.ac.rcpa.bio.tools.statistic;

import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;

public interface IPeptideFDRCalculator {

	public abstract double calculate(
			List<? extends IIdentifiedPeptideHit> peptides, int charge, boolean identicalCharge);

	public abstract double calculate(
			List<? extends IIdentifiedPeptideHit> peptides);

}