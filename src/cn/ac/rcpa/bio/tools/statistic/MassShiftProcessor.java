package cn.ac.rcpa.bio.tools.statistic;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bijnum.BIJStats;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.PrecursorTolerance;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.utils.Pair;
import cn.ac.rcpa.utils.RcpaMathUtils;

public class MassShiftProcessor implements IFileProcessor {
	private double maxPPMTolerance;

	public MassShiftProcessor(double maxPPMTolerance) {
		super();
		this.maxPPMTolerance = maxPPMTolerance;
	}

	public List<String> process(String originFile) throws Exception {
		BuildSummaryResult br = new BuildSummaryResultReader().read(originFile);

		List<BuildSummaryProteinGroup> groups = new ArrayList<BuildSummaryProteinGroup>();

		for (BuildSummaryProteinGroup group : br.getProteinGroups()) {
			if (group.getProtein(0).getUniquePeptides().length >= 2) {
				groups.add(group);
			}
		}

		BuildSummaryResult good = new BuildSummaryResult();
		for (int i = 0; i < groups.size() / 2; i++) {
			good.addProteinGroup(groups.get(i));
		}

		List<BuildSummaryPeptideHit> pephits = good.getPeptideHits();
		List<Double> massShifts = new ArrayList<Double>();

		String resultFile = originFile + ".massshift";
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			pw.println("Filename(Scan)\tMz\tShift");
			for (BuildSummaryPeptideHit pephit : pephits) {
				BuildSummaryPeptide peptide = pephit.getPeptide(0);
				double mzShift = peptide.getDiffToExperimentMass()
						/ peptide.getCharge();
				double ppmShift = PrecursorTolerance.mz2ppm(pephit.getPeptide(0)
						.getObservedMz(), mzShift);
				massShifts.add(ppmShift);
				pw.println(pephit.getPeakListInfo().getLongFilename() + "\t"
						+ peptide.getObservedMz() + "\t" + ppmShift);
			}

			double[] massShiftArray = RcpaMathUtils.toDoubleArray(massShifts);
			double avg = BIJStats.avg(massShiftArray);
			double stdev = BIJStats.stdev(massShiftArray);
			pw.println();
			pw.println("Avg    =" + avg);
			pw.println("Stdev  =" + stdev);
			pw.println("Stdev*5=" + stdev * 5);

			Map<Pair<Double, Double>, Integer> countmap = new LinkedHashMap<Pair<Double, Double>, Integer>();

			int maxSize = 20;
			double factor = maxPPMTolerance / maxSize;
			countmap.put(
					new Pair<Double, Double>(-Double.MAX_VALUE, -maxPPMTolerance), 0);
			for (int i = -maxSize; i < maxSize; i++) {
				double from = i * factor;
				double to = (i + 1) * factor;
				countmap.put(new Pair<Double, Double>(from, to), 0);
			}
			countmap.put(new Pair<Double, Double>(maxPPMTolerance, Double.MAX_VALUE),
					0);

			for (double ms : massShifts) {
				for (Pair<Double, Double> pair : countmap.keySet()) {
					if (ms >= pair.fst && ms < pair.snd) {
						countmap.put(pair, countmap.get(pair) + 1);
					}
				}
			}

			pw.println();

			for (Pair<Double, Double> pair : countmap.keySet()) {
				pw.println(pair.fst + "~" + pair.snd + "\t" + countmap.get(pair));
			}

		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}
}
