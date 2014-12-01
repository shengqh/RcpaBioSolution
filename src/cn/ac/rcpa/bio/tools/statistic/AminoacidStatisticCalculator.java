package cn.ac.rcpa.bio.tools.statistic;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.ac.rcpa.bio.proteomics.classification.ClassificationFactory;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.proteomics.statistics.IStatisticsCalculator;
import cn.ac.rcpa.bio.proteomics.statistics.StatisticsCalculatorFactory;
import cn.ac.rcpa.bio.proteomics.statistics.impl.CompositeStatisticsCalculator;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;

public class AminoacidStatisticCalculator {
	private String aminoacids;

	public AminoacidStatisticCalculator(String aminoacids) {
		this.aminoacids = aminoacids;
	}

	public void process(String resultFile, Set<String> sequences)
			throws Exception {
		ArrayList<IStatisticsCalculator<String>> calcs = new ArrayList<IStatisticsCalculator<String>>();
		for (int i = 0; i < aminoacids.length(); i++) {
			IClassification<String> aminoacidClassification = ClassificationFactory
					.getPeptideSequenceAminoacidClassification(aminoacids.charAt(i));
			calcs.add(StatisticsCalculatorFactory
					.getStatisticsCalculator(aminoacidClassification));
		}
		IStatisticsCalculator<String> calc = new CompositeStatisticsCalculator<String>(
				calcs);

		Set<String> filtered = new HashSet<String>();
		for (String seq : sequences) {
			String pureSeq = PeptideUtils.getPurePeptideSequence(seq);
			if (filtered.contains(pureSeq)) {
				continue;
			}
			/**
			 * should not consider overlap condition! so remove those codes <br>
			 * 
			 * <code>
			 * boolean bContained = false; 
			 * List<String> removed = new ArrayList<String>();
			 * for (String key : filtered) { 
			 * 	 if (pureSeq.contains(key)) {
			 * 	   removed.add(key); 
			 *   } 
			 *   else if (key.contains(pureSeq)) { 
			 *     bContained = true; 
			 *     break; 
			 *   } 
			 * }
			 * 
			 * if (!bContained) { filtered.add(pureSeq); }
			 * 
			 * for (String key : removed) { filtered.remove(key); }
			 * </code>
			 */
		}

		calc.process(filtered);

		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
		try {
			calc.output(pw);
		} finally {
			pw.close();
		}
	}
}
