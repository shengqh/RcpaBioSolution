package cn.ac.rcpa.bio.tools.distribution;

import java.io.PrintWriter;

import cn.ac.rcpa.bio.proteomics.modification.ModificationUtils;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.utils.Pair;

public class ModifiedPeptideDistributionCalculator extends
		PeptideDistributionCalculator {
	private String modifiedAminoacids;

	protected ModifiedPeptideDistributionCalculator(IValueCalculator calc,
			boolean exportIndividual, String modifiedAminoacids) {
		super(calc, exportIndividual);
		this.modifiedAminoacids = modifiedAminoacids;
	}

	@Override
	protected String getKey(BuildSummaryPeptideHit pephit) {
		Pair<String, Integer> modifiedInfo = ModificationUtils.getModificationInfo(
				modifiedAminoacids, pephit);
		return modifiedInfo.fst + "," + modifiedInfo.snd;
	}

	@Override
	protected void printKeyHeader(PrintWriter pw) {
		pw.print("Sequence\tModifiedCount");
	}

	@Override
	protected void printKey(
			PrintWriter pw,
			CalculationItem<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItem) {
		Pair<String, Integer> modifiedInfo = ModificationUtils.getModificationInfo(
				modifiedAminoacids, calculationItem.getPeptides().get(0));
		pw.print(modifiedInfo.fst + "\t" + modifiedInfo.snd);
	}
}
