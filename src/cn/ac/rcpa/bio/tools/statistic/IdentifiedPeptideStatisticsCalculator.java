package cn.ac.rcpa.bio.tools.statistic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.proteomics.statistics.IStatisticsCalculator;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;

public class IdentifiedPeptideStatisticsCalculator implements IFileProcessor {
	private IStatisticsCalculator<IIdentifiedPeptide> calc;

	public IdentifiedPeptideStatisticsCalculator(
			IStatisticsCalculator<IIdentifiedPeptide> calc) {
		super();
		this.calc = calc;
	}

	public List<String> process(String peptideFilename) throws Exception {
		List<BuildSummaryPeptideHit> peptides;
		try {
			peptides = new BuildSummaryPeptideHitReader().read(peptideFilename);
		} catch (RcpaParseException ex) {
			peptides = new BuildSummaryResultReader().read(peptideFilename)
					.getPeptideHits();
		}
		return doCalculate(peptideFilename, peptides);
	}

	private List<String> doCalculate(String peptideFilename,
			List<BuildSummaryPeptideHit> peptides) throws IOException {
		final List<BuildSummaryPeptide> totalPeptides = PeptideUtils
				.getUnduplicatedPeptides(peptides);
		calc.clear();
		calc.process(totalPeptides);
		final String totalResultFile = peptideFilename + ".total.stat";
		saveStatisticResult(calc, totalResultFile);

		calc.clear();
		final List<BuildSummaryPeptide> uniquePeptides = PeptideUtils
				.getUniquePeptides(peptides);
		calc.process(uniquePeptides);
		final String uniqueResultFile = peptideFilename + ".unique.stat";
		saveStatisticResult(calc, uniqueResultFile);

		return Arrays.asList(new String[] { totalResultFile, uniqueResultFile });
	}

	private static void saveStatisticResult(
			IStatisticsCalculator<IIdentifiedPeptide> calc, String filename)
			throws IOException, IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(filename));
		try {
			calc.output(pw);
		} finally {
			pw.close();
		}
	}
}
