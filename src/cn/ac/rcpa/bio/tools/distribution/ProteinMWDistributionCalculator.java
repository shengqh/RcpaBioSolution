package cn.ac.rcpa.bio.tools.distribution;

import java.util.Collections;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.comparison.IdentifiedProteinGroupComparator;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;

public class ProteinMWDistributionCalculator extends
		AbstractProteinDistributionCalculator {
	public ProteinMWDistributionCalculator(boolean exportIndividual) {
		super("MW", exportIndividual);
	}

	@Override
	protected void sortIdentifiedProtein(
			List<BuildSummaryProteinGroup> proteinHits) {
		Collections.sort(proteinHits, IdentifiedProteinGroupComparator
				.getInstance());
	}

	@Override
	protected void setTheoreticalValue(
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item,
			BuildSummaryProteinGroup proteinGroup) {
		item.setTheoreticalValue(proteinGroup.getProtein(0).getMW());
	}

}
