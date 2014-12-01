package cn.ac.rcpa.bio.tools.distribution;

import java.util.Collections;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.comparison.IdentifiedPeptideHitPIComparator;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;

public class ValuePICalculator implements IValueCalculator {
	private String title;

	public ValuePICalculator() {
		this.title = "PI";
	}

	public String getTitle() {
		return title;
	}

	public double getValue(
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> item) {
		if (item.getPeptides().size() == 0
				|| item.getPeptides().get(0).getPeptideCount() == 0) {
			throw new IllegalArgumentException("There is no peptide in item!");
		}

		return item.getPeptides().get(0).getPeptide(0).getPI();
	}

	public void sort(List<BuildSummaryPeptideHit> pephits) {
		Collections.sort(pephits, IdentifiedPeptideHitPIComparator.getInstance());
	}

}
