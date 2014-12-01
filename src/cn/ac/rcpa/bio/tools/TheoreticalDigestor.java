package cn.ac.rcpa.bio.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.biojava.bio.proteomics.Protease;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.annotation.StatisticRanges;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.classification.ClassificationFactory;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.proteomics.statistics.IStatisticsCalculator;
import cn.ac.rcpa.bio.proteomics.statistics.StatisticsCalculatorFactory;
import cn.ac.rcpa.bio.utils.ProteaseRenderer;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class TheoreticalDigestor implements IFileProcessor {
	public static String version = "1.0.5";

	private Protease protease;

	private int maxMissedCleavages;

	private ISequenceValidator validator;

	private ProteaseDigestor digestor;

	private boolean precursorMonoisotopic;

	private String aminoacids;

	private boolean writeSequence;

	public TheoreticalDigestor(Protease protease, int maxMissedCleavages,
			ISequenceValidator validator, boolean precursorMonoisotopic,
			String aminoacids, boolean writeSequence) {
		this.protease = protease;
		this.maxMissedCleavages = maxMissedCleavages;
		this.validator = validator;
		this.digestor = new ProteaseDigestor(protease, maxMissedCleavages,
				validator);
		this.precursorMonoisotopic = precursorMonoisotopic;
		this.aminoacids = aminoacids;
		this.writeSequence = writeSequence;
	}

	protected IStatisticsCalculator<String> getStatisticsCalculator()
			throws FileNotFoundException, IOException {
		// initialize classification
		IClassification<String> mwClassification = ClassificationFactory
				.getPeptideSequenceMWClassification(StatisticRanges
						.getPeptideMWRange(), precursorMonoisotopic);
		IClassification<String> piClassification = ClassificationFactory
				.getPeptideSequencePIClassification(StatisticRanges
						.getPIRange());
		IClassification<String> missedCleavagesClassification = ClassificationFactory
				.getPeptideSequenceMissedCleavagesClassification(protease);

		Collection<IClassification<String>> compositeClassifications = ClassificationFactory
				.getPeptideSequenceNetSolutionChargeClassification();
		compositeClassifications.add(ClassificationFactory
				.getPeptideSequenceGRAVYClassification(StatisticRanges
						.getGRAVYRange()));

		// initialize calculator
		IStatisticsCalculator<String> mwpiCalc = StatisticsCalculatorFactory
				.getStatisticsCalculator(piClassification, mwClassification,
						StatisticsCalculatorFactory.OUTPUT_BOTH);
		IStatisticsCalculator<String> missedCleavagesPICalc = StatisticsCalculatorFactory
				.getStatisticsCalculator(missedCleavagesClassification,
						piClassification,
						StatisticsCalculatorFactory.OUTPUT_FIRST);
		IStatisticsCalculator<String> compositeCalc = StatisticsCalculatorFactory
				.getStatisticsCalculator(compositeClassifications);

		ArrayList<IStatisticsCalculator<String>> calcs = new ArrayList<IStatisticsCalculator<String>>();
		calcs.add(mwpiCalc);
		calcs.add(missedCleavagesPICalc);
		calcs.add(compositeCalc);

		if (aminoacids.length() != 0) {
			for (int i = 0; i < aminoacids.length(); i++) {
				IClassification<String> aminoacidClassification = ClassificationFactory
						.getPeptideSequenceAminoacidClassification(aminoacids
								.charAt(i));
				calcs.add(StatisticsCalculatorFactory
						.getStatisticsCalculator(aminoacidClassification));
			}
		}

		// get composite calculator
		return StatisticsCalculatorFactory
				.getCompositeStatisticsCalculator(calcs);
	}

	public List<String> process(String databaseFile) throws Exception {
		final String peptideFile = databaseFile + ".peptides";
		final String resultFile = peptideFile + ".stat";
		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
		try {
			pw.println("Version=" + version);
			pw.println("Protease="
					+ ProteaseRenderer.getProteaseCaption(protease));
			pw.println(validator.getType());
			pw.println("MaxMissedCleavages=" + maxMissedCleavages);
			pw.println();

			Set<String> totalPeptides = new HashSet<String>();
			SequenceIterator seqi = SequenceUtils
					.readFastaProtein(new BufferedReader(new FileReader(
							databaseFile)));
			int icount = 0;
			while (seqi.hasNext()) {
				icount++;
				if (icount % 1000 == 0) {
					System.out.println("Digesting " + icount);
				}
				Sequence seq = seqi.nextSequence();
				totalPeptides.addAll(digestor.digest(seq));
			}
			seqi = null;
			System.out.println("\nDigested " + icount
					+ " sequences and generated " + totalPeptides.size()
					+ " peptides!");

			IStatisticsCalculator<String> allCalc = getStatisticsCalculator();
			allCalc.process(totalPeptides);

			allCalc.output(pw);

			if (writeSequence) {
				PrintWriter pwSeq = new PrintWriter(new FileWriter(peptideFile));
				try {
					for (String pep : totalPeptides) {
						pwSeq.println(pep);
					}
				} finally {
					pwSeq.close();
				}
			}
			totalPeptides.clear();
		} finally {
			pw.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}
}
