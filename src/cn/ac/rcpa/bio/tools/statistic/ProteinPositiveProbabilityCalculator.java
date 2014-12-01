/*
 * Created on 2006-2-14
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;

public class ProteinPositiveProbabilityCalculator implements IFileProcessor {
	private double peptidePositiveRate;

	public ProteinPositiveProbabilityCalculator(double peptidePositiveRate) {
		this.peptidePositiveRate = peptidePositiveRate;
	}

	public List<String> process(String originFile) throws Exception {
		BuildSummaryResult br = new BuildSummaryResultReader().read(originFile);
		List<BuildSummaryPeptideHit> peptideHits = br.getPeptideHits();

		int cycleCount = 100;
		double averageRate = 0.0;
		for (int i = 0; i < cycleCount; i++) {
			Map<String, Boolean> positiveMap = getPositiveMap(peptideHits);
			double proteinPositiveRate = calculateProteinPositiveRate(br, positiveMap);
			System.out.println("PositiveGroupRate = " + proteinPositiveRate);
			averageRate += proteinPositiveRate;
		}
		averageRate /= cycleCount;
		System.out.println("AveragePositiveGroupRate = " + averageRate);

		return Arrays.asList(new String[] { "AveragePositiveGroupRate = "
				+ averageRate });
	}

	private double calculateProteinPositiveRate(BuildSummaryResult br,
			Map<String, Boolean> positiveMap) {
		int positiveGroupCount = 0;
		for (int i = 0; i < br.getProteinGroupCount(); i++) {
			BuildSummaryProteinGroup group = br.getProteinGroup(i);
			Map<String, List<String>> uniquePeptideMap = getUniquePeptideMap(group);
			int positiveUniPepCount = 0;
			for (String key : uniquePeptideMap.keySet()) {
				List<String> filenames = uniquePeptideMap.get(key);
				int positivePepCount = 0;
				for (String filename : filenames) {
					if (positiveMap.get(filename)) {
						positivePepCount++;
					}
				}

				int negativePepCount = filenames.size() - positivePepCount;
				if (positivePepCount > negativePepCount) {
					positiveUniPepCount++;
				}
			}

			int negativeUniPepCount = uniquePeptideMap.size() - positiveUniPepCount;
			if (positiveUniPepCount > negativeUniPepCount) {
				positiveGroupCount++;
			}
		}

		return (double) positiveGroupCount / br.getProteinGroupCount();
	}

	private Map<String, List<String>> getUniquePeptideMap(
			BuildSummaryProteinGroup group) {
		List<BuildSummaryPeptideHit> hits = group.getPeptideHits();
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (BuildSummaryPeptideHit hit : hits) {
			String seq = PeptideUtils.getPurePeptideSequence(hit.getPeptide(0)
					.getSequence());
			if (!result.containsKey(seq)) {
				result.put(seq, new ArrayList<String>());
			}
			result.get(seq).add(hit.getPeakListInfo().getLongFilename());
		}
		return result;
	}

	private Map<String, Boolean> getPositiveMap(
			List<BuildSummaryPeptideHit> peptideHits) {
		List<BuildSummaryPeptideHit> tempHits = new ArrayList<BuildSummaryPeptideHit>(
				peptideHits);
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		int falsePositiveCount = (int) (peptideHits.size() * (1 - peptidePositiveRate));
		int falseCount = 0;
		while (falseCount < falsePositiveCount) {
			int randomIndex = RandomUtils.nextInt(tempHits.size() - 1);
			result.put(tempHits.get(randomIndex).getPeptide(0).getPeakListInfo()
					.getLongFilename(), false);
			tempHits.remove(randomIndex);
			falseCount++;
		}

		for (BuildSummaryPeptideHit hit : tempHits) {
			result.put(hit.getPeptide(0).getPeakListInfo().getLongFilename(), true);
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		new ProteinPositiveProbabilityCalculator(0.95)
				.process("F:\\Science\\Data\\HPPP\\2DLC_Micro_LCQ\\1.9_2.5_3.75_0.1_4\\HPPP_LCQ_2.5.noredundant");
		new ProteinPositiveProbabilityCalculator(0.99)
				.process("F:\\Science\\Data\\HPPP\\2DLC_Micro_LCQ\\1.9_2.5_3.75_0.1_4\\HPPP_LCQ_2.5.noredundant");
	}

}
