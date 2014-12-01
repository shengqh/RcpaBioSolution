package cn.ac.rcpa.bio.tools.statistic;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.ac.rcpa.bio.annotation.StatisticRanges;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.Pair;

public class OptimalXCorrAndDeltaCnCalculator implements IFileProcessor {
	private static double[] xcorrSet;

	private static double[] deltacnSet;

	private static double[] getXCorrSet() {
		if (xcorrSet == null) {
			xcorrSet = StatisticRanges.getXCorrRange();
		}
		return xcorrSet;
	}

	private static double[] getDeltaCnSet() {
		if (deltacnSet == null) {
			deltacnSet = StatisticRanges.getDeltaCnRange();
		}
		return deltacnSet;
	}

	private double[] minCorrectPercents;

	private IPeptideFDRCalculator calculator;

	public OptimalXCorrAndDeltaCnCalculator(double[] minCorrectPercents,
			String reversedDbPattern) {
		this.minCorrectPercents = minCorrectPercents;
		this.calculator = new PeptideFDRGygiCalculator(reversedDbPattern);
	}

	public OptimalXCorrAndDeltaCnCalculator(double[] minCorrectPercents,
			IFilter<IIdentifiedPeptideHit> filter) {
		this.minCorrectPercents = minCorrectPercents;
		this.calculator = new PeptideFDRGygiCalculator(filter);
	}

	public OptimalXCorrAndDeltaCnCalculator(double[] minCorrectPercents) {
		this.minCorrectPercents = minCorrectPercents;
		this.calculator = new PeptideFDRGygiCalculator("^REVERSE");
	}

	/**
	 * A pair used to store correct percentage and corresponding identified count
	 */
	public class CorrectPercentCountValue extends
			Pair<List<BuildSummaryPeptideHit>, Double> {
		public CorrectPercentCountValue(List<BuildSummaryPeptideHit> pephits,
				Double correctPercent) {
			super(pephits, correctPercent);
		}
	}

	/**
	 * A map used to deltaCn fit for the threshold and its'corresponding result
	 */
	public class DeltaCnMap extends
			LinkedHashMap<Double, CorrectPercentCountValue> {
	}

	/**
	 * A map used to store xcorr and its'corresponding DeltaCnMap
	 */
	public class XCorrMap extends LinkedHashMap<Double, DeltaCnMap> {
	}

	/**
	 * A map used to store charge and its'corresponding XCorrMap
	 */
	public class ChargeMap extends LinkedHashMap<Integer, XCorrMap> {
	}

	public ChargeMap getChargeMap(List<BuildSummaryPeptideHit> peptides) {
		ChargeMap chargeMap = new ChargeMap();
		for (int charge = 1; charge <= 3; charge++) {
			List<BuildSummaryPeptideHit> chargePeptides = filterPeptideByCharge(
					peptides, charge);

			XCorrMap xcorrMap = new XCorrMap();
			chargeMap.put(charge, xcorrMap);

			for (Double xcorr : getXCorrSet()) {
				List<BuildSummaryPeptideHit> xcorrPeptides = filterPeptideByXcorr(
						chargePeptides, xcorr);
				if (xcorrPeptides.size() == 0) {
					break;
				}

				DeltaCnMap deltacnMap = new DeltaCnMap();
				xcorrMap.put(xcorr, deltacnMap);

				for (Double deltacn : getDeltaCnSet()) {
					List<BuildSummaryPeptideHit> deltacnPeptides = filterPeptideByDeltacn(
							xcorrPeptides, deltacn);

					double correctPercent = calculator.calculate(deltacnPeptides);

					deltacnMap.put(deltacn, new CorrectPercentCountValue(deltacnPeptides,
							correctPercent));
				}
			}
		}
		return chargeMap;
	}

	private void printResult(double minCorrectPercent, ChargeMap chargeMap,
			PrintWriter pw) {
		DecimalFormat df = new DecimalFormat("##.##");
		for (Integer charge : chargeMap.keySet()) {
			pw.println("Charge " + charge);
			XCorrMap correctMap = chargeMap.get(charge);
			for (Double deltacn : getDeltaCnSet()) {
				pw.print("\t" + df.format(deltacn));
			}
			pw.println();

			for (Double xcorr : correctMap.keySet()) {
				pw.print(df.format(xcorr));
				for (Pair<List<BuildSummaryPeptideHit>, Double> correct : correctMap
						.get(xcorr).values()) {
					pw.print("\t" + df.format(correct.snd * 100));
				}
				pw.println();
			}

			pw.println();
		}

		pw.println("\t\tCharge 1\t\t\tCharge 2\t\t\tCharge 3");
		pw
				.println("XCorr\tDeltaCn\tCount\tPercent\tDeltaCn\tCount\tPercent\tDeltaCn\tCount\tPercent\tTotalCount");
		for (Double xcorr : getXCorrSet()) {
			pw.print(df.format(xcorr));
			int totalCount = 0;
			for (int charge : chargeMap.keySet()) {
				XCorrMap xcorrMap = chargeMap.get(charge);
				boolean bfound = false;
				for (Double deltacn : getDeltaCnSet()) {
					DeltaCnMap deltacnMap = xcorrMap.get(xcorr);
					if (null == deltacnMap) {
						break;
					}

					Pair<List<BuildSummaryPeptideHit>, Double> correct = deltacnMap
							.get(deltacn);
					if (correct.snd >= minCorrectPercent) {
						pw.print("\t" + df.format(deltacn) + "\t" + correct.fst.size()
								+ "\t" + df.format(correct.snd));
						totalCount += correct.fst.size();
						bfound = true;
						break;
					}
				}
				if (!bfound) {
					pw.print("\t\t\t");
				}
			}
			pw.println("\t" + totalCount);
		}
		pw.println();

		pw.println("DeltaCn\t\tCharge 1\t\t\tCharge 2\t\t\tCharge 3");
		pw
				.println("\tXCorr\tCount\tPercent\tXCorr\tCount\tPercent\tXCorr\tCount\tPercent");
		for (Double deltacn : getDeltaCnSet()) {
			pw.print(df.format(deltacn));
			int totalCount = 0;
			for (int charge : chargeMap.keySet()) {
				XCorrMap xcorrMap = chargeMap.get(charge);
				boolean bfound = false;
				for (Double xcorr : getXCorrSet()) {
					DeltaCnMap deltacnMap = xcorrMap.get(xcorr);
					if (null == deltacnMap) {
						break;
					}

					Pair<List<BuildSummaryPeptideHit>, Double> correct = deltacnMap
							.get(deltacn);
					if (correct.snd >= minCorrectPercent) {
						pw.print("\t" + df.format(xcorr) + "\t" + correct.fst.size() + "\t"
								+ df.format(correct.snd));
						totalCount += correct.fst.size();
						bfound = true;
						break;
					}
				}
				if (!bfound) {
					pw.print("\t\t\t");
				}
			}
			pw.println("\t" + totalCount);
		}

		pw.println();
	}

	private static <E extends BuildSummaryPeptideHit> List<E> filterPeptideByCharge(
			List<E> peptides, int charge) {
		List<E> result = new ArrayList<E>();

		for (E peptide : peptides) {
			if (peptide.getPeakListInfo().getCharge() == charge) {
				result.add(peptide);
			}
		}

		return result;
	}

	private static List<BuildSummaryPeptideHit> filterPeptideByDeltacn(
			List<BuildSummaryPeptideHit> peptides, double deltacn) {
		List<BuildSummaryPeptideHit> result = new ArrayList<BuildSummaryPeptideHit>();

		for (BuildSummaryPeptideHit peptide : peptides) {
			BuildSummaryPeptide pep = peptide.getPeptide(0);
			if (pep.getDeltacn() >= deltacn) {
				result.add(peptide);
			}
		}

		return result;
	}

	private static List<BuildSummaryPeptideHit> filterPeptideByXcorr(
			List<BuildSummaryPeptideHit> peptides, double xcorr) {
		List<BuildSummaryPeptideHit> result = new ArrayList<BuildSummaryPeptideHit>();

		for (BuildSummaryPeptideHit peptide : peptides) {
			BuildSummaryPeptide pep = (BuildSummaryPeptide) peptide.getPeptide(0);
			if (pep.getXcorr() >= xcorr) {
				result.add(peptide);
			}
		}

		return result;
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> peptides = IdentifiedResultIOFactory
				.readBuildSummaryPeptideHit(originFile);
		ChargeMap chargeMap = getChargeMap(peptides);
		ArrayList<String> result = new ArrayList<String>();
		for (double minCorrectPercent : minCorrectPercents) {
			String resultFile = originFile + "." + minCorrectPercent + ".calc";
			PrintWriter pw = new PrintWriter(resultFile);
			result.add(resultFile);
			try {
				printResult(minCorrectPercent, chargeMap, pw);
			} finally {
				pw.close();
			}
		}

		return result;
	}
}
