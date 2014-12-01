package cn.ac.rcpa.bio.tools.other;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class MergePeptideBuilder implements IFileProcessor {
	public static String version = "1.0.0";

	private String secondPeptideFile;

	public MergePeptideBuilder(String subPeptideFile) {
		super();
		this.secondPeptideFile = subPeptideFile;
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> firstPeptides = new BuildSummaryPeptideHitReader()
				.read(originFile);
		Map<String, BuildSummaryPeptideHit> firstPeptideMap = new LinkedHashMap<String, BuildSummaryPeptideHit>();
		for (BuildSummaryPeptideHit hit : firstPeptides) {
			firstPeptideMap.put(hit.getPeakListInfo().getLongFilename(), hit);
		}

		List<BuildSummaryPeptideHit> hits2 = new BuildSummaryPeptideHitReader()
				.read(secondPeptideFile);

		for (BuildSummaryPeptideHit hit : hits2) {
			String filename = hit.getPeakListInfo().getLongFilename();
			if (!firstPeptideMap.containsKey(filename)) {
				firstPeptideMap.put(filename, hit);
			}
		}

		String resultFilename = RcpaFileUtils.changeExtension(originFile,
				".merged.peptides");
		new BuildSummaryPeptideHitWriter().write(resultFilename, firstPeptideMap
				.values());

		return Arrays.asList(new String[] { resultFilename });
	}
}
