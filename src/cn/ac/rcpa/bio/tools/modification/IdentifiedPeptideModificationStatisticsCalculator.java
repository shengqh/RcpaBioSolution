package cn.ac.rcpa.bio.tools.modification;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.CountMap;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideHitFilterByPeptideFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideModificationFilter;
import cn.ac.rcpa.bio.proteomics.modification.DiffStateModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.HomologModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.HomologModificationPeptidesList;
import cn.ac.rcpa.bio.proteomics.modification.ModificationSiteFilterFactory;
import cn.ac.rcpa.bio.proteomics.modification.SameModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.SequenceModificationSitePair;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.Pair;

public class IdentifiedPeptideModificationStatisticsCalculator extends
AbstractIdentifiedPeptideModificationStatisticsCalculator {
	private IFilter<IIdentifiedPeptideHit> filter;

	public static final String version = "1.0.2";

	public IdentifiedPeptideModificationStatisticsCalculator(
			String modifiedAminoacids) {
		super(modifiedAminoacids);
		this.filter = new IdentifiedPeptideHitFilterByPeptideFilter(
				new IdentifiedPeptideModificationFilter(modifiedAminoacids, 0, false));
	}

	public List<String> process(String originFile) throws Exception {
		ArrayList<String> result = new ArrayList<String>();

		List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader()
				.read(originFile);

		BuildSummaryPeptideHitWriter writer = new BuildSummaryPeptideHitWriter();

		// 取出所有修饰的肽段
		List<BuildSummaryPeptideHit> modifiedPeptides = PeptideUtils.getSubset(
				pephits, filter);

		// 根据修饰序列分为不同Set
		Map<String, List<BuildSummaryPeptideHit>> pepSetMap = getPepSetMap(modifiedPeptides);

		// 合并/删除被包含的Set
		List<List<BuildSummaryPeptideHit>> uniquePeptidesSet = getUniquePeptidesSet(pepSetMap);

		// 将List<BuildSummaryPeptideHit>转换为SameModificationPeptides
		List<SameModificationPeptides> transedPeptides = getModificationPeptidesList(uniquePeptidesSet);

		sortByLengthDesc(transedPeptides);

		List<DiffStateModificationPeptides> modificationPeptides = mergeSameModifiedSequenceButDifferentState(transedPeptides);

		HomologModificationPeptidesList homoPeptides = getHomologModificationPeptides(modificationPeptides);

		Collections.sort(homoPeptides,
				new Comparator<HomologModificationPeptides>() {
					public int compare(HomologModificationPeptides o1,
							HomologModificationPeptides o2) {
						return o2.getPeptideHitCount() - o1.getPeptideHitCount();
					}
				});
		String statFile = originFile + ".stat";

		printStatFile(writer, homoPeptides, statFile);

		result.add(statFile);

		return result;
	}

	private void printStatFile(BuildSummaryPeptideHitWriter writer,
			HomologModificationPeptidesList homoPeptides, String statFile)
			throws IOException, IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(statFile));
		try {
			printItemBrief(pw, homoPeptides, writer);
			printStatistic(pw, homoPeptides);
		} finally {
			pw.close();
		}

		PrintWriter pwDetails = new PrintWriter(new FileWriter(statFile
				+ ".details"));
		try {
			printItemDetails(pwDetails, homoPeptides, writer);
		} finally {
			pwDetails.close();
		}
	}

	private void printStatistic(PrintWriter pw,
			HomologModificationPeptidesList homoPeptides) throws IOException {
		pw.println();
		pw.println("Modified Peptide Count\t" + homoPeptides.getPeptideHitCount());
		pw.println("Unique Modified Peptide Count\t"
				+ homoPeptides.getUniqueModifiedPeptideCount());
		pw.println("Modified Peptide Site Count\t"
				+ homoPeptides.getModificationSiteCount());

		CountMap<Integer> phosphoCount = homoPeptides.getPhosphoCountMap();
		printCountMap(pw, "Unique Modified Peptide Count", phosphoCount);

		printStatByFilter(pw, homoPeptides, "Candidate Modification Site",
				ModificationSiteFilterFactory.getModifiedSiteFilter());

		printStatByFilter(pw, homoPeptides, "Ambigious Modification Site",
				ModificationSiteFilterFactory.getAmbigiousModifiedSiteFilter());

		printStatByFilter(pw, homoPeptides, "Positive Modification Site",
				ModificationSiteFilterFactory.getPositiveModifiedSiteFilter());

		printStatByFilter(pw, homoPeptides, "Multiple State Modification Site",
				ModificationSiteFilterFactory.getMultipleStateModifiedSiteFilter());
	}

	private void printStatByFilter(PrintWriter pw,
			HomologModificationPeptidesList homoPeptides, String title,
			IFilter<Pair<Character, Character>> afilter) {
		CountMap<Character> modificationSiteCount = homoPeptides
				.getSiteMap(afilter);
		printCountMap(pw, title, modificationSiteCount);
	}

	private <T extends Comparable<? super T>> void printCountMap(PrintWriter pw,
			String title, CountMap<T> modificationSiteCount) {
		if (modificationSiteCount.size() > 0) {
			pw.println();
			pw.println(title);
			pw.println("Total\t" + modificationSiteCount.getTotalCount());
			List<T> keys = new ArrayList<T>(modificationSiteCount.keySet());
			Collections.sort(keys);
			for (Comparable key : keys) {
				pw.println(key + "\t" + modificationSiteCount.get(key));
			}
		}
	}

	private void printItemBrief(PrintWriter pw,
			HomologModificationPeptidesList homoPeptides,
			BuildSummaryPeptideHitWriter writer) throws IOException {
		pw.println("Index\tSequence\tModified\tPositive\tAmbigious\t");
		int index = 0;
		for (HomologModificationPeptides homo : homoPeptides) {
			index++;
			pw.println("$" + index + "\t" + homo.getSequence() + "\t"
					+ homo.getSequence().getModifiedCount() + "\t"
					+ homo.getSequence().getTrueModifiedCount() + "\t"
					+ homo.getSequence().getAmbiguousModifiedCount());
			for (DiffStateModificationPeptides diff : homo
					.getModificationPeptidesList()) {
				for (SameModificationPeptides modified : diff
						.getSameModificationPeptidesList()) {
					for (BuildSummaryPeptideHit pep : modified.getPeptideHits()) {
						writer.writePeptideHit(pw, pep);
						pw.println();
					}
				}
			}
		}
	}

	private void printItemDetails(PrintWriter pw,
			HomologModificationPeptidesList homoPeptides,
			BuildSummaryPeptideHitWriter writer) throws IOException {
		pw.println("Sequence\t\t\tModified\tPositive\tAmbigious\t");
		for (HomologModificationPeptides homo : homoPeptides) {
			pw.println(homo.getSequence() + "\t" + "\t" + "\t"
					+ homo.getSequence().getModifiedCount() + "\t"
					+ homo.getSequence().getTrueModifiedCount() + "\t"
					+ homo.getSequence().getAmbiguousModifiedCount());
			for (DiffStateModificationPeptides diff : homo
					.getModificationPeptidesList()) {
				pw.println("\t" + diff.getSequence() + "\t" + "\t"
						+ diff.getSequence().getModifiedCount() + "\t"
						+ diff.getSequence().getTrueModifiedCount() + "\t"
						+ diff.getSequence().getAmbiguousModifiedCount());
				for (SameModificationPeptides modified : diff
						.getSameModificationPeptidesList()) {
					pw.println("\t\t" + modified.getSequence() + "\t"
							+ modified.getSequence().getModifiedCount() + "\t"
							+ modified.getSequence().getTrueModifiedCount() + "\t"
							+ modified.getSequence().getAmbiguousModifiedCount());
					List<BuildSummaryPeptideHit> peptides = new ArrayList<BuildSummaryPeptideHit>(
							modified.getPeptideHits());
					Collections.sort(peptides);
					for (BuildSummaryPeptideHit pep : peptides) {
						pw.print("\t\t\t");
						SequenceModificationSitePair pair = new SequenceModificationSitePair(
								modifiedAminoacids, pep);
						pw.print(pair.getModifiedSequence());
						writer.writePeptideHit(pw, pep);
						pw.println();
					}
				}
			}
		}
	}

	public HomologModificationPeptidesList getInitHomologModificationPeptides(
			List<DiffStateModificationPeptides> modificationPeptides) {
		HashSet<DiffStateModificationPeptides> removed = new HashSet<DiffStateModificationPeptides>();
		HomologModificationPeptidesList result = new HomologModificationPeptidesList();
		for (int i = 0; i < modificationPeptides.size(); i++) {
			final DiffStateModificationPeptides iPeptides = modificationPeptides
					.get(i);
			if (removed.contains(iPeptides)) {
				continue;
			}

			HomologModificationPeptides homo = new HomologModificationPeptides(
					iPeptides);
			result.add(homo);

			for (int j = i + 1; j < modificationPeptides.size(); j++) {
				final DiffStateModificationPeptides jPeptides = modificationPeptides
						.get(j);
				if (iPeptides.getPureSequence().equals(jPeptides.getPureSequence())) {
					homo.add(jPeptides);
					removed.add(jPeptides);
				}
			}
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		// new IdentifiedPeptideModificationStatisticsCalculator("STY").process(
		// "F:\\Science\\Data\\jwh\\phospho\\Stringent_all_RP_SAX_Phospho.noredundant.peptides");
		// new IdentifiedPeptideModificationStatisticsCalculator("STY")
		// .process("Z:\\JWH\\CD\\SAX_0906_Summary\\2.0_2.5_3.3_0.01\\SAX_0906_Total_2.0_2.5_3.3_0.01.peptides");
		new IdentifiedPeptideModificationStatisticsCalculator("STY")
				.process("data/TestPhospho2.peptides");
	}

}
