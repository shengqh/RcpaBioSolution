package cn.ac.rcpa.bio.tools.modification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.tools.statistic.PeptideHitMap;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class ModifiedPeptideHitMap extends HashMap<String, PeptideHitMap> {
	public ModifiedPeptideHitMap() {
		super();
	}

	public void addModifiedPeptide(String pureSeq, IIdentifiedPeptideHit hit) {
		final String convertedPureSeq = getConvertedPureSequence(pureSeq);

		if (!this.containsKey(convertedPureSeq)) {
			this.put(convertedPureSeq, new PeptideHitMap());
		}

		this.get(convertedPureSeq).addPeptide(pureSeq, hit);
	}

	private String getConvertedPureSequence(String pureSeq) {
		if (this.keySet().contains(pureSeq)) {
			return pureSeq;
		}

		for (String key : this.keySet()) {
			if (isModificationEquals(key, pureSeq)) {
				return key;
			}
		}

		return pureSeq;
	}

	private boolean isModificationEquals(String key, String pureSeq) {
		if (key.length() != pureSeq.length()) {
			return false;
		}

		for (int i = 0; i < key.length(); i++) {
			if (key.charAt(i) != pureSeq.charAt(i)
					&& (!SequenceUtils.isModifiedChar(key.charAt(i)) || !SequenceUtils
							.isModifiedChar(pureSeq.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	public int getUniqueModifiedPeptideCount() {
		return this.keySet().size();
	}

	public int getPeptideCount() {
		int result = 0;
		for (PeptideHitMap hitMap : values()) {
			result += hitMap.getPeptideCount();
		}
		return result;
	}

	public int getDifferentModificationInSameSitePeptideCount() {
		int result = 0;
		for (PeptideHitMap hitMap : values()) {
			if (hitMap.keySet().size() > 1) {
				result++;
			}
		}
		return result;
	}

	public List<String> getSortedPeptides() {
		List<String> result = new ArrayList<String>(this.keySet());

		final ModifiedPeptideHitMap thisMap = this;

		Collections.sort(result, new Comparator<String>() {
			public int compare(String o1, String o2) {
				int compResult = thisMap.get(o2).size() - thisMap.get(o1).size();
				if (compResult == 0) {
					compResult = thisMap.get(o2).getPeptideCount()
							- thisMap.get(o1).getPeptideCount();
					if (compResult == 0) {
						compResult = o1.compareTo(o2);
					}
				}
				return compResult;
			}
		});

		return result;
	}

}
