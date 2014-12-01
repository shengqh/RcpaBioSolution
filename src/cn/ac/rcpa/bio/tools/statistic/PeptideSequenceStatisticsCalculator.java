package cn.ac.rcpa.bio.tools.statistic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.proteomics.Protease;

import cn.ac.rcpa.bio.annotation.StatisticRanges;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.classification.ClassificationFactory;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.proteomics.statistics.IStatisticsCalculator;
import cn.ac.rcpa.bio.proteomics.statistics.StatisticsCalculatorFactory;
import cn.ac.rcpa.bio.proteomics.statistics.impl.CompositeStatisticsCalculator;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class PeptideSequenceStatisticsCalculator implements IFileProcessor {
	private Protease protease;

	private boolean precursorMonoisotopic;

	public PeptideSequenceStatisticsCalculator(Protease protease,
			boolean precursorMonoisotopic) {
		this.protease = protease;
		this.precursorMonoisotopic = precursorMonoisotopic;
	}

	private String doCalculate(String peptideFilename, List<String> peptides)
			throws IOException {
		final IStatisticsCalculator<String> calc = getStatisticCalculator();

		calc.clear();
		calc.process(peptides);
		final String resultFile = peptideFilename + ".stat";
		saveStatisticResult(calc, resultFile);

		return resultFile;
	}

	private static void saveStatisticResult(IStatisticsCalculator<String> calc,
			String filename) throws IOException, IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(filename));
		try {
			calc.output(pw);
		} finally {
			pw.close();
		}
	}

	private IStatisticsCalculator<String> getStatisticCalculator() {
		IClassification<String> mwClassification = ClassificationFactory
				.getPeptideSequenceMWClassification(
						StatisticRanges.getPeptideMWRange(), precursorMonoisotopic);
		IClassification<String> piClassification = ClassificationFactory
				.getPeptideSequencePIClassification(StatisticRanges.getPIRange());

		IClassification<String> missCleavagedClassification = ClassificationFactory
				.getPeptideSequenceMissedCleavagesClassification(protease);

		IClassification<String> gravyClassification = ClassificationFactory
				.getPeptideSequenceGRAVYClassification(StatisticRanges.getGRAVYRange());

		IClassification<String> lengthClassification = ClassificationFactory
				.getPeptideSequenceLengthClassification();

		ArrayList<IStatisticsCalculator<String>> calcs = new ArrayList<IStatisticsCalculator<String>>();

		calcs.add(StatisticsCalculatorFactory.getStatisticsCalculator(
				piClassification, mwClassification,
				StatisticsCalculatorFactory.OUTPUT_BOTH));

		calcs.add(StatisticsCalculatorFactory.getStatisticsCalculator(
				missCleavagedClassification, piClassification,
				StatisticsCalculatorFactory.OUTPUT_FIRST));

		calcs.add(StatisticsCalculatorFactory.getStatisticsCalculator(
				missCleavagedClassification, mwClassification,
				StatisticsCalculatorFactory.OUTPUT_NONE));

		calcs.add(StatisticsCalculatorFactory
				.getStatisticsCalculator(gravyClassification));

		calcs.add(StatisticsCalculatorFactory
				.getStatisticsCalculator(lengthClassification));

		IStatisticsCalculator<String> calc = new CompositeStatisticsCalculator<String>(
				calcs);

		return calc;
	}

	public List<String> process(String originFile) throws Exception {
		List<String> seqs = Arrays.asList(RcpaFileUtils.readFile(originFile));
		return Arrays.asList(new String[] { doCalculate(originFile, seqs) });
	}
}
