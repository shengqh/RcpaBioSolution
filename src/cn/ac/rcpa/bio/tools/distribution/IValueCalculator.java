package cn.ac.rcpa.bio.tools.distribution;

import java.util.List;

import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;

public interface IValueCalculator {
	String getTitle();

	double getValue(
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item);

	void sort(List<BuildSummaryPeptideHit> pephits);
}
