package cn.ac.rcpa.bio.tools.statistic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;

public class IdentifiedPeptideAminoacidStatisticCalculator implements
		IFileProcessor {
	private String aminoacids;

	public IdentifiedPeptideAminoacidStatisticCalculator(String aminoacids) {
		this.aminoacids = aminoacids;
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> peptides = new BuildSummaryPeptideHitReader()
				.read(originFile);
		Set<String> sequences = new HashSet<String>();
		for (BuildSummaryPeptideHit phit : peptides) {
			if (!sequences.contains(phit.getPeptides().get(0).getSequence())) {
				sequences.add(phit.getPeptides().get(0).getSequence());
			}
		}

		final String resultFile = originFile + "." + aminoacids + ".stat";
		new AminoacidStatisticCalculator(aminoacids).process(resultFile, sequences);
		return Arrays.asList(new String[] { resultFile });
	}
}
