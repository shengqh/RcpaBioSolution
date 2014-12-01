package cn.ac.rcpa.bio.tools.modification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.modification.DiffStateModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.HomologModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.HomologModificationPeptidesList;
import cn.ac.rcpa.bio.proteomics.modification.SameModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.SequenceModificationSitePair;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;

public abstract class AbstractIdentifiedPeptideModificationStatisticsCalculator
		implements IFileProcessor {
	protected String modifiedAminoacids;

	public AbstractIdentifiedPeptideModificationStatisticsCalculator(
			String modifiedAminoacids) {
		super();
		this.modifiedAminoacids = modifiedAminoacids;
	}

	/**
	 * 合并有overlap的DiffStateModificationPeptides，主要合并有两种：<br>
	 * 1、序列完全一致，但是修饰位点不一样。例如：<br>
	 * <code>
	 * PeptideHit1: AAAABBBB*CCCCDDDD 
	 * PeptideHit2: AA*AABBBBCCCCDDDD
	 * </code>
	 * 2、序列有overlap<br>
	 * <code>
	 * PeptideHit1: EAA*AABBBBCCCCDDDD 
	 * PeptideHit2:  AA*AABBBBCCCCDDDDF
	 * </code>
	 * 
	 * @param modificationPeptides
	 * @return
	 */
	protected HomologModificationPeptidesList getHomologModificationPeptides(
			List<DiffStateModificationPeptides> modificationPeptides) {
		HashMap<DiffStateModificationPeptides, HomologModificationPeptides> removed = new HashMap<DiffStateModificationPeptides, HomologModificationPeptides>();
		HomologModificationPeptidesList result = new HomologModificationPeptidesList();
		for (int i = 0; i < modificationPeptides.size(); i++) {
			final DiffStateModificationPeptides iPeptides = modificationPeptides
					.get(i);
			if (removed.containsKey(iPeptides)) {
				continue;
			}

			HomologModificationPeptides homo = new HomologModificationPeptides(
					iPeptides);
			result.add(homo);

			for (int j = i + 1; j < modificationPeptides.size(); j++) {
				final DiffStateModificationPeptides jPeptides = modificationPeptides
						.get(j);
				if (iPeptides.getPureSequence().indexOf(jPeptides.getPureSequence()) != -1) {
					if (removed.containsKey(jPeptides)) {
						HomologModificationPeptides anotherParent = removed.get(jPeptides);
						HashSet<String> curProteinNames = new HashSet<String>(homo
								.getModificationPeptidesList().get(0)
								.getSameModificationPeptidesList().get(0).getPeptideHits().get(
										0).getPeptide(0).getProteinNames());

						HashSet<String> otherProteinNames = new HashSet<String>(
								anotherParent.getModificationPeptidesList().get(0)
										.getSameModificationPeptidesList().get(0).getPeptideHits()
										.get(0).getPeptide(0).getProteinNames());

						if (curProteinNames.equals(otherProteinNames)) {
							if (anotherParent.getSequence().mergeOverlap(homo.getSequence(),
									jPeptides.getPureSequence())) {
								removed.put(iPeptides, anotherParent);
								result.remove(homo);
								anotherParent.add(iPeptides);
								System.out.println("Oh, merge succeed : the peptide "
										+ jPeptides.getPureSequence() + " is included in "
										+ homo.getSequence().getModifiedSequence() + " and "
										+ anotherParent.getSequence().getModifiedSequence());
								break;
							}

							System.out.println("Sorry, merge failed : the peptide "
									+ jPeptides.getPureSequence() + " is included in "
									+ homo.getSequence().getModifiedSequence() + " and "
									+ anotherParent.getSequence().getModifiedSequence());
						}
					}
					homo.add(jPeptides);
					removed.put(jPeptides, homo);
				}
			}
		}

		return result;
	}

	/**
	 * 合并SameModificationPeptides，转换为DiffStateModificationPeptides，合并需要满足下面的任何一条：<br>
	 * 1、TransMatchSequence一样（忽略那些相同位点上的不同标记，例如'*'表示MS2，'@'表示MS3)。例如：<br>
	 * <code>
	 * PeptideHit1: AA@AABBBBCCCCDDDD 
	 * PeptideHit2: AA*AABBBBCCCCDDDD
	 * </code>
	 * 2、序列完全一样并且modification site有包含关系的。这里的包含关系是指位点个数不同的包含关系。例如：<br>
	 * <code>
	 * PeptideHit1: AA*AABB*BBCCCCDDDD 
	 * PeptideHit2: AA*AABBBBCCCCDDDD
	 * </code>
	 * peptidehit1所组成的Group就包含了peptidehit2所组成的group
	 * 
	 * @param transedPeptides
	 * @return
	 */
	protected List<DiffStateModificationPeptides> mergeSameModifiedSequenceButDifferentState(
			List<SameModificationPeptides> transedPeptides) {
		List<DiffStateModificationPeptides> result = new ArrayList<DiffStateModificationPeptides>();

		HashSet<SameModificationPeptides> removed = new HashSet<SameModificationPeptides>();
		for (int i = 0; i < transedPeptides.size(); i++) {
			final SameModificationPeptides iPeptides = transedPeptides.get(i);
			if (removed.contains(iPeptides)) {
				continue;
			}

			DiffStateModificationPeptides pep = new DiffStateModificationPeptides(
					iPeptides);
			result.add(pep);

			final String iPureSeq = iPeptides.getPureSequence();
			for (int j = i + 1; j < transedPeptides.size(); j++) {
				final SameModificationPeptides jPeptides = transedPeptides.get(j);
				final String jPureSeq = jPeptides.getPureSequence();
				if (iPureSeq.equals(jPureSeq)) {
					pep.add(jPeptides);
					removed.add(jPeptides);
					continue;
				}
			}
		}

		return result;
	}

	/**
	 * 将PeptideHitList转换为SameModificationPeptides。 <br>
	 * 1、由于Ambigious的复杂性，有时候会出现两个PeptideHit没有一种修饰形式是一样的，但是
	 * 综合考虑两个PeptideHit的修饰位点后，两者是一样的，合并这种List。<br>
	 * <code>
	 * PeptideHit1: AA*AABB*BBCCCCDDDD | AAAABBBBCC*CCDD*DD ->AApAABBpBBCCpCCDDpDD
	 * PeptideHit2: AA*AABBBBCC*CCDDDD | AAAABB*BBCCCCDD*DD ->AApAABBpBBCCpCCDDpDD
	 * </code>
	 * 
	 * 2、如果一个peptidehit的修饰比另一个可靠，也就是说，ambigious位点更少，合并到保留ambigious位点更少的那一个。<br>
	 * <code>
	 * PeptideHit1: AA*AABB*BBCCCCDDDD ->AA*AABB*BBCCCCDDDD 
	 * PeptideHit2: AA*AABBBBCC*CCDDDD | AAAABB*BBCCCCDD*DD ->AApAABBpBBCCpCCDDpDD
	 * </code>
	 * 
	 * @param uniquePeptidesList
	 * @return
	 */
	protected List<SameModificationPeptides> getModificationPeptidesList(
			List<List<BuildSummaryPeptideHit>> uniquePeptidesList) {
		List<SameModificationPeptides> result = new ArrayList<SameModificationPeptides>();
		for (List<BuildSummaryPeptideHit> pephits : uniquePeptidesList) {
			result.add(new SameModificationPeptides(modifiedAminoacids, pephits));
		}

		for (int i = 0; i < result.size(); i++) {
			String phosPeptide = result.get(i).getSequence().toString();
			for (int j = result.size() - 1; j > i; j--) {
				if (phosPeptide.equals(result.get(j).getSequence().toString())) {
					result.get(i).addPeptideHits(result.get(j).getPeptideHits());
					result.remove(j);
					continue;
				}

				SequenceModificationSitePair iPair = result.get(i).getSequence();
				SequenceModificationSitePair jPair = result.get(j).getSequence();
				if (iPair.getPureSequence().equals(jPair.getPureSequence())
						&& iPair.getModifiedCount() == jPair.getModifiedCount()) {
					if (jPair.getAmbiguousModifiedCount() > iPair
							.getAmbiguousModifiedCount()
							&& jPair.containAllAmbiguousSite(iPair)) {
						result.get(i).addPeptideHits(result.get(j).getPeptideHits());
						result.remove(j);
						continue;
					}

					if (iPair.getAmbiguousModifiedCount() > jPair
							.getAmbiguousModifiedCount()
							&& iPair.containAllAmbiguousSite(jPair)
							&& 0 != jPair.getAmbiguousModifiedCount()) {
						result.get(j).addPeptideHits(result.get(i).getPeptideHits());
						result.set(i, result.get(j));
						result.remove(j);
						continue;
					}
				}
			}
		}

		return result;
	}

	/**
	 * 如果一个List中的所有PeptideHit被包含在另外一个List中，这个List被移除。
	 * 
	 * @param pephits
	 * @return
	 */
	protected List<List<BuildSummaryPeptideHit>> getUniquePeptidesSet(
			Map<String, List<BuildSummaryPeptideHit>> pepSetMap) {
		LinkedHashSet<List<BuildSummaryPeptideHit>> redundantSet = new LinkedHashSet<List<BuildSummaryPeptideHit>>(
				pepSetMap.values());

		ArrayList<List<BuildSummaryPeptideHit>> result = new ArrayList<List<BuildSummaryPeptideHit>>(
				redundantSet);

		// sort by list size descending
		Collections.sort(result, new Comparator<List<BuildSummaryPeptideHit>>() {
			public int compare(List<BuildSummaryPeptideHit> arg0,
					List<BuildSummaryPeptideHit> arg1) {
				return -(arg0.size() - arg1.size());
			}
		});

		// remove child set
		for (int i = result.size() - 1; i >= 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				if (result.get(j).containsAll(result.get(i))) {
					result.remove(i);
					break;
				}
			}
		}
		
		return result;
	}

	/**
	 * 一个List中的PeptideHit有相同的属性： <br>
	 * 1、纯序列一样<br>
	 * 2、修饰位点个数一样<br>
	 * 3、修饰方式相同<br>
	 * 但是结果中会有冗余，一个有多种修饰可能的PeptideHit会在多个List中出现。Map的Key就是可能的修饰序列。
	 * 
	 * @param List
	 *          <BuildSummaryPeptideHit>
	 * @return Map<String, List<BuildSummaryPeptideHit>>
	 */
	protected Map<String, List<BuildSummaryPeptideHit>> getPepSetMap(
			List<BuildSummaryPeptideHit> pephits) {
		Map<String, List<BuildSummaryPeptideHit>> result = new LinkedHashMap<String, List<BuildSummaryPeptideHit>>();

		for (BuildSummaryPeptideHit pephit : pephits) {
			List<String> sequences = getModifiedSequenceList(pephit);

			for (String sequence : sequences) {
				if (!result.containsKey(sequence)) {
					List<BuildSummaryPeptideHit> peptideList = new ArrayList<BuildSummaryPeptideHit>();
					result.put(sequence, peptideList);
				}

				result.get(sequence).add(pephit);
			}
		}
		return result;
	}

	/**
	 * Get all possible modified peptide sequences
	 * 
	 * @param pephit
	 * @return
	 */
	protected List<String> getModifiedSequenceList(BuildSummaryPeptideHit pephit) {
		List<String> result = new ArrayList<String>();
		List<String> sequences = pephit.getPeptideSequences();
		for (String seq : sequences) {
			SequenceModificationSitePair ambi = new SequenceModificationSitePair(
					modifiedAminoacids, seq, '*');
			result.add(ambi.toString());
		}
		return result;
	}

	/**
	 * Sort list of SameModificationPeptides by the pure sequence length of the
	 * SameModificationPeptides
	 * 
	 * @param modificationPeptides
	 */
	public void sortByLengthDesc(
			List<SameModificationPeptides> modificationPeptides) {
		Collections.sort(modificationPeptides,
				new Comparator<SameModificationPeptides>() {
					public int compare(SameModificationPeptides o1,
							SameModificationPeptides o2) {
						return o2.getPureSequence().length()
								- o1.getPureSequence().length();
					}
				});
	}

	protected void keepMultipleStateOnly(
			List<DiffStateModificationPeptides> modificationPeptides) {
		for (int i = modificationPeptides.size() - 1; i >= 0; i--) {
			if (1 == modificationPeptides.get(i).getSameModificationPeptidesList()
					.size()) {
				modificationPeptides.remove(i);
			}
		}
	}
}
