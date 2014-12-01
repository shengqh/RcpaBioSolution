package cn.ac.rcpa.bio.tools.statistic;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;

public class IdentifiedScanDistributionCalculator implements IFileProcessor {
	private String msLevelFile;

	public IdentifiedScanDistributionCalculator(String msLevelFile) {
		this.msLevelFile = msLevelFile;
	}

	public static void main(String[] args) throws Exception {
		String scanFilename = "F:\\Science\\Data\\HPPP\\2DLC_Micro_LTQ\\HPPP_Salt.msLevel";
		String peptideFile = "F:\\Science\\Data\\HPPP\\2DLC_Micro_LTQ\\1.9_2.2_3.75_0.1_4\\2D_LTQ_HPPP.peptides";

		System.out.println(new IdentifiedScanDistributionCalculator(scanFilename)
				.process(peptideFile));
	}

	private Map<Integer, Integer> getIndexCount(
			Map<String, Map<Integer, Integer>> rawfileMap,
			List<? extends IIdentifiedPeptideHit> peptides) {
		Map<Integer, Integer> indexCount = new HashMap<Integer, Integer>();
		for (IIdentifiedPeptideHit peptide : peptides) {
			Map<Integer, Integer> scanNumberMap = rawfileMap.get(peptide
					.getPeakListInfo().getExperiment());
			int index = scanNumberMap.get(peptide.getPeakListInfo().getFirstScan());
			if (!indexCount.containsKey(index)) {
				indexCount.put(index, 0);
			}

			indexCount.put(index, indexCount.get(index).intValue() + 1);
		}
		return indexCount;
	}

	private Map<Integer, Integer> getMinIndexCount(
			Map<String, Map<Integer, Integer>> rawfileMap,
			List<? extends IIdentifiedPeptideHit> peptides) {
		Map<String, Integer> peptideMinScan = new HashMap<String, Integer>();
		for (IIdentifiedPeptideHit peptide : peptides) {
			final String pureSeq = PeptideUtils.getPurePeptideSequence(peptide
					.getPeptide(0).getSequence());
			Map<Integer, Integer> scanNumberMap = rawfileMap.get(peptide
					.getPeakListInfo().getExperiment());
			int index = scanNumberMap.get(peptide.getPeakListInfo().getFirstScan());
			if (!peptideMinScan.containsKey(pureSeq)) {
				peptideMinScan.put(pureSeq, index);
			} else {
				Integer oldIndex = peptideMinScan.get(pureSeq);
				if (oldIndex > index) {
					peptideMinScan.put(pureSeq, index);
				}
			}
		}

		Map<Integer, Integer> indexCount = new HashMap<Integer, Integer>();
		for (Integer value : peptideMinScan.values()) {
			if (!indexCount.containsKey(value)) {
				indexCount.put(value, 1);
			} else {
				indexCount.put(value, indexCount.get(value) + 1);
			}
		}

		return indexCount;
	}

	public List<String> process(String peptideFile) throws Exception {
		Map<String, Map<Integer, Integer>> rawfileMap = RawScanFile
				.getRawScanMap(msLevelFile);

		List<BuildSummaryPeptideHit> peptides = IdentifiedResultIOFactory
				.readBuildSummaryPeptideHit(peptideFile);

		Map<Integer, Integer> indexCount = getIndexCount(rawfileMap, peptides);
		Map<Integer, Integer> minIndexCount = getMinIndexCount(rawfileMap, peptides);

		final String resultFile = peptideFile + ".scan";
		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
		try {
			pw.println("ScanIndex\tCount");
			printMap(indexCount, pw);

			pw.println();

			pw.println("MinScanIndex\tCount");
			printMap(minIndexCount, pw);
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

	private void printMap(Map<Integer, Integer> indexCount, PrintWriter pw) {
		List<Integer> indexs = new ArrayList<Integer>(indexCount.keySet());
		Collections.sort(indexs);
		for (Integer index : indexs) {
			pw.println(index + "\t" + indexCount.get(index));
			System.out.println(index + "\t" + indexCount.get(index));
		}
	}

}
