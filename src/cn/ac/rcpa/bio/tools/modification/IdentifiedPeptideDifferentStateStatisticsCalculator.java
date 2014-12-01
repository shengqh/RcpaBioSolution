package cn.ac.rcpa.bio.tools.modification;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.modification.DiffStateModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.SameModificationPeptides;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;

public class IdentifiedPeptideDifferentStateStatisticsCalculator extends
		AbstractIdentifiedPeptideModificationStatisticsCalculator {
	private enum PeptideType {
		UnmodifiedAndMultipleState, UnmodifiedAndOneState, MultipleState
	};

	public static final String version = "1.0.3";

	public IdentifiedPeptideDifferentStateStatisticsCalculator(
			String modifiedAminoacids) {
		super(modifiedAminoacids);
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader()
				.read(originFile);

		BuildSummaryPeptideHitWriter writer = new BuildSummaryPeptideHitWriter();

		// 根据修饰序列分为不同Set
		Map<String, List<BuildSummaryPeptideHit>> pepSetMap = getPepSetMap(pephits);

		// 合并/删除被包含的Set
		List<List<BuildSummaryPeptideHit>> uniquePeptidesSet = getUniquePeptidesSet(pepSetMap);

		// 将List<BuildSummaryPeptideHit>转换为SameModificationPeptides
		List<SameModificationPeptides> transedPeptides = getModificationPeptidesList(uniquePeptidesSet);

		sortByLengthDesc(transedPeptides);

		List<DiffStateModificationPeptides> modificationPeptides = mergeSameModifiedSequenceButDifferentState(transedPeptides);

		keepMultipleStateOnly(modificationPeptides);

		Collections.sort(modificationPeptides,
				new Comparator<DiffStateModificationPeptides>() {
					public int compare(DiffStateModificationPeptides o1,
							DiffStateModificationPeptides o2) {
						return o2.getPeptideHitCount()
								- o1.getPeptideHitCount();
					}
				});

		return printStatFile(writer, modificationPeptides, originFile);
	}

	private List<String> printStatFile(BuildSummaryPeptideHitWriter writer,
			List<DiffStateModificationPeptides> homoPeptides, String originFile)
			throws IOException, IOException {
		List<String> result = new ArrayList<String>();

		Map<PeptideType, List<DiffStateModificationPeptides>> pepMap = new LinkedHashMap<PeptideType, List<DiffStateModificationPeptides>>();
		pepMap.put(PeptideType.UnmodifiedAndMultipleState,
				new ArrayList<DiffStateModificationPeptides>());
		pepMap.put(PeptideType.UnmodifiedAndOneState,
				new ArrayList<DiffStateModificationPeptides>());
		pepMap.put(PeptideType.MultipleState,
				new ArrayList<DiffStateModificationPeptides>());

		for (DiffStateModificationPeptides pep : homoPeptides) {
			PeptideType type = getPeptideType(pep);
			pepMap.get(type).add(pep);
		}

		for (PeptideType aType : PeptideType.values()) {
			String filename = originFile + "." + aType + ".stat";
			List<DiffStateModificationPeptides> peps = pepMap.get(aType);

			PrintWriter pw = new PrintWriter(new FileWriter(filename));
			try {
				printItemBrief(pw, peps, writer);
				printStatistic(pw, peps);
			} finally {
				pw.close();
			}

			result.add(filename);
		}

		return result;
	}

	private PeptideType getPeptideType(DiffStateModificationPeptides pep) {
		List<SameModificationPeptides> list = pep
				.getSameModificationPeptidesList();

		boolean bUnmodified = false;
		for (SameModificationPeptides spep : list) {
			if (0 == spep.getSequence().getModifiedCount()) {
				bUnmodified = true;
				break;
			}
		}

		if (2 == list.size() && bUnmodified) {
			return PeptideType.UnmodifiedAndOneState;
		}

		if (bUnmodified) {
			return PeptideType.UnmodifiedAndMultipleState;
		} else {
			return PeptideType.MultipleState;
		}
	}

	private void printStatistic(PrintWriter pw,
			List<DiffStateModificationPeptides> peps) {
		pw.println();
		pw.println("Unique Peptide Count\t" + peps.size());

		int ipepcount = 0;
		for (DiffStateModificationPeptides diff : peps) {
			ipepcount += diff.getPeptideHitCount();
		}
		pw.println("Peptide Count\t" + +ipepcount);
	}

	private void printItemBrief(PrintWriter pw,
			List<DiffStateModificationPeptides> peps,
			BuildSummaryPeptideHitWriter writer) throws IOException {
		pw.println("Index\tSequence");

		int index = 0;
		for (DiffStateModificationPeptides diff : peps) {
			index++;
			pw.println(index + "\t" + diff.getPureSequence());
			for (SameModificationPeptides homo : diff
					.getSameModificationPeptidesList()) {
				for (BuildSummaryPeptideHit pep : homo.getPeptideHits()) {
					writer.writePeptideHit(pw, pep);
					pw.println();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new IdentifiedPeptideDifferentStateStatisticsCalculator("STY")
				.process("data/phospho.peptides");
		// .process("data/TestPhospho4.peptides");
	}

}
