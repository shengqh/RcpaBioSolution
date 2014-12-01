package cn.ac.rcpa.bio.tools.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;

public class PeptideHitMap extends HashMap<String, List<IIdentifiedPeptideHit>> {
	public PeptideHitMap() {
		super();
	}

	public void addPeptide(String sequence, IIdentifiedPeptideHit hit) {
		if (!this.containsKey(sequence)) {
			this.put(sequence, new ArrayList<IIdentifiedPeptideHit>());
		}
		this.get(sequence).add(hit);
	}

	public int getPeptideCount() {
		int result = 0;
		for (List<IIdentifiedPeptideHit> hits : values()) {
			result += hits.size();
		}
		return result;
	}

	@Override
	public Set<String> keySet() {
		List<String> resultList = new ArrayList<String>(super.keySet());

		final PeptideHitMap thisMap = this;

		Collections.sort(resultList, new Comparator<String>() {
			public int compare(String o1, String o2) {
				int result = thisMap.get(o2).size() - thisMap.get(o1).size();
				if (result == 0) {
					result = o1.compareTo(o2);
				}
				return result;
			}
		});

		return new LinkedHashSet<String>(resultList);
	}
}
