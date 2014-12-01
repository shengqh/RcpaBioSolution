/*
 * Created on 2006-2-16
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ObverseReverseIdentifiedPeptideSeparator implements IFileProcessor {
	private ReversedDbPeptideHitFilter filter;

	public ObverseReverseIdentifiedPeptideSeparator(String reversedDbPattern) {
		filter = new ReversedDbPeptideHitFilter(reversedDbPattern);
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> peptides = IdentifiedResultIOFactory
				.readBuildSummaryPeptideHit(originFile);

		List<BuildSummaryPeptideHit> obverseHits = new ArrayList<BuildSummaryPeptideHit>();
		List<BuildSummaryPeptideHit> reverseHits = new ArrayList<BuildSummaryPeptideHit>();
		for (BuildSummaryPeptideHit hit : peptides) {
			if (filter.accept(hit)) {
				reverseHits.add(hit);
			} else {
				obverseHits.add(hit);
			}
		}

		String obverseFile = RcpaFileUtils.changeExtension(originFile,
				"obverse.peptides");
		String reverseFile = RcpaFileUtils.changeExtension(originFile,
				"reverse.peptides");
		IdentifiedResultIOFactory.writeBuildSummaryPeptideHit(obverseFile,
				obverseHits);
		IdentifiedResultIOFactory.writeBuildSummaryPeptideHit(reverseFile,
				reverseHits);

		return Arrays.asList(new String[] { obverseFile, reverseFile });
	}

}
