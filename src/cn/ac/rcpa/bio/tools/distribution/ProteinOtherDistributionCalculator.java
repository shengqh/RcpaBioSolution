package cn.ac.rcpa.bio.tools.distribution;

import java.util.Collections;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.comparison.IdentifiedProteinGroupComparator;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;

public class ProteinOtherDistributionCalculator extends
		AbstractProteinDistributionCalculator {
	public ProteinOtherDistributionCalculator(boolean exportIndividual) {
		super("OTHER", exportIndividual);
	}

	@Override
	protected void setTheoreticalValue(
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item,
			BuildSummaryProteinGroup proteinGroup) {
		item.setTheoreticalValue(proteinGroup.getProtein(0).getPI());
	}

	@Override
	protected void sortIdentifiedProtein(
			List<BuildSummaryProteinGroup> proteinHits) {
		Collections.sort(proteinHits, IdentifiedProteinGroupComparator
				.getInstance());
	}

}
