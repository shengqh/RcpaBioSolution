package cn.ac.rcpa.bio.tools.modification;

import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class BuildSummaryPeptideHitAmbigiousDeltaCnFilter implements
		IFileProcessor {
	public static String version = "1.0.0";
	
	private double maxAmbigiousDeltaCn;

	public BuildSummaryPeptideHitAmbigiousDeltaCnFilter(double maxAmbigiousDeltaCn) {
		this.maxAmbigiousDeltaCn = maxAmbigiousDeltaCn;
	}

	public List<String> process(String originFile) throws Exception {
		final List<BuildSummaryPeptideHit> peptides = new BuildSummaryPeptideHitReader()
				.read(originFile);
		
		for(BuildSummaryPeptideHit pephit:peptides){
			for(int i = pephit.getFollowCandidates().size() - 1; i >= 0;i--){
				if(pephit.getFollowCandidates().get(i).getDeltaScore() > maxAmbigiousDeltaCn){
					pephit.getFollowCandidates().remove(i);
				}
			}
		}
		
		final String resultFilename = RcpaFileUtils.changeExtension(originFile, "DeltaCn"
				+ maxAmbigiousDeltaCn + ".peptides");
		new BuildSummaryPeptideHitWriter().write(resultFilename, peptides);
		
		return Arrays.asList(new String[]{resultFilename});
	}

}
