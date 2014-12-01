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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biojava.bio.proteomics.Protease;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.classification.impl.IdentifiedPeptideMissedCleavagesClassification;
import cn.ac.rcpa.bio.proteomics.classification.impl.IdentifiedPeptideNumberOfProteaseTerminalClassification;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class MissCleavageIdentifiedPeptideSeparator implements IFileProcessor {
	private IdentifiedPeptideMissedCleavagesClassification missCleavageClassification;

	private IdentifiedPeptideNumberOfProteaseTerminalClassification nptClassification;

	public MissCleavageIdentifiedPeptideSeparator(Protease protease) {
		super();
		this.missCleavageClassification = new IdentifiedPeptideMissedCleavagesClassification(
				protease);
		this.nptClassification = new IdentifiedPeptideNumberOfProteaseTerminalClassification(
				protease);
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> peptides = IdentifiedResultIOFactory
				.readBuildSummaryPeptideHit(originFile);

		List<BuildSummaryPeptideHit> unCleavagePeptides = new ArrayList<BuildSummaryPeptideHit>();
		Map<String, List<BuildSummaryPeptideHit>> hitMap = new HashMap<String, List<BuildSummaryPeptideHit>>();
		for (BuildSummaryPeptideHit hit : peptides) {
			String npt = nptClassification.getClassification(hit.getPeptide(0));
			if (!npt.equals("2")) {
				unCleavagePeptides.add(hit);
			} else {
				String missCleavageCount = missCleavageClassification
						.getClassification(hit.getPeptide(0));
				if (!hitMap.containsKey(missCleavageCount)) {
					hitMap
							.put(missCleavageCount, new ArrayList<BuildSummaryPeptideHit>());
				}
				hitMap.get(missCleavageCount).add(hit);
			}
		}

		ArrayList<String> result = new ArrayList<String>();

		String resultFile = RcpaFileUtils.changeExtension(originFile,
				"UnCleavage.peptides");
		IdentifiedResultIOFactory.writeBuildSummaryPeptideHit(resultFile,
				unCleavagePeptides);
		result.add(resultFile);

		for (String missCleavageCount : hitMap.keySet()) {
			String missResultFile = RcpaFileUtils.changeExtension(originFile,
					"MissCleavage." + missCleavageCount + ".peptides");
			IdentifiedResultIOFactory.writeBuildSummaryPeptideHit(missResultFile,
					hitMap.get(missCleavageCount));
			result.add(missResultFile);
		}

		return result;
	}

}
