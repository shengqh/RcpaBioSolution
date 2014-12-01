package cn.ac.rcpa.bio.tools.statistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bijnum.BIJStats;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IonType;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.image.peptide.MatchedPeak;
import cn.ac.rcpa.bio.proteomics.image.peptide.SequestPeptideResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.sequest.DtasFileIterator;
import cn.ac.rcpa.bio.proteomics.sequest.OutsFileIterator;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.utils.Pair;
import cn.ac.rcpa.utils.RcpaCollectionUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class IonTypeFrequencyCalculator implements IFileProcessor {
	private String dir;

	private IonType[] ionTypesCharge12;

	private IonType[] ionTypes;

	private double mzTolerance;

	private double intensityTolerance;

	private Map<String, Pair<File, File>> experimentalMap;

	public IonTypeFrequencyCalculator(String dir, IonType[] ionTypes,
			double mzTolerance, double intensityTolerance) {
		super();
		this.dir = dir;
		this.experimentalMap = initDtaOutFiles(dir);
		this.mzTolerance = mzTolerance;
		this.intensityTolerance = intensityTolerance;

		List<IonType> itype12 = new ArrayList<IonType>();
		for (IonType iType : ionTypes) {
			if (iType != IonType.B2 && iType != IonType.Y2) {
				itype12.add(iType);
			}
		}
		this.ionTypesCharge12 = itype12.toArray(new IonType[0]);
		this.ionTypes = ionTypes;

		for (String exp : experimentalMap.keySet()) {
			Pair<File, File> files = experimentalMap.get(exp);
			System.out.println(exp + "\t" + files.fst.getName() + "\t"
					+ files.snd.getName());
		}
	}

	protected Map<String, Pair<File, File>> initDtaOutFiles(String dir) {
		File rootDir = new File(dir);
		if (!rootDir.exists()) {
			throw new IllegalArgumentException("Directory not exist : " + dir);
		}
		Map<String, Pair<File, File>> result = new HashMap<String, Pair<File, File>>();
		initDtaOutFiles(rootDir, result);
		return result;
	}

	private void initDtaOutFiles(File dir, Map<String, Pair<File, File>> result) {
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".dtas");
			}
		});

		for (File dtasFile : files) {
			File outsFile = new File(RcpaFileUtils.changeExtension(dtasFile
					.getAbsolutePath(), ".outs"));
			if (!outsFile.exists()) {
				throw new IllegalArgumentException(
						"Cannot find corresponding outs file of "
								+ dtasFile.getAbsolutePath());
			}

			String experimental = RcpaFileUtils.changeExtension(dtasFile
					.getName(), "");
			result.put(experimental, new Pair<File, File>(dtasFile, outsFile));
		}

		File[] subDirs = RcpaFileUtils.getSubDirectories(dir);
		for (File subDir : subDirs) {
			initDtaOutFiles(subDir, result);
		}
	}

	private class InfoItem {
		private BuildSummaryPeptideHit pephit;

		private String[] dtaContent;

		private String[] outContent;

		public String[] getDtaContent() {
			return dtaContent;
		}

		public void setDtaContent(String[] dtaContent) {
			this.dtaContent = dtaContent;
		}

		public String[] getOutContent() {
			return outContent;
		}

		public void setOutContent(String[] outContent) {
			this.outContent = outContent;
		}

		public BuildSummaryPeptideHit getPephit() {
			return pephit;
		}

		public void setPephit(BuildSummaryPeptideHit pephit) {
			this.pephit = pephit;
		}
	}

	public static class IonTypeFrequency {
		private int matchCount;

		private int totalCount;

		private List<Double> intensity = new ArrayList<Double>();

		private List<Double> relativeIntensity = new ArrayList<Double>();

		public IonTypeFrequency() {
			matchCount = 0;
			totalCount = 0;
		}

		public int getMatchCount() {
			return matchCount;
		}

		public void setMatchCount(int matchCount) {
			this.matchCount = matchCount;
		}

		public List<Double> getIntensity() {
			return intensity;
		}

		public Pair<Double, Double> getIntensityMeanAndStdev() {
			if (intensity.size() == 0) {
				return new Pair<Double, Double>(0.0, 0.0);
			} else {
				final double[] corrs = RcpaCollectionUtils
						.toDoubleArray(intensity);
				final double avg = BIJStats.avg(corrs);
				final double stdev = BIJStats.stdev(corrs);

				return new Pair<Double, Double>(avg, stdev);
			}
		}

		public List<Double> getRelativeIntensity() {
			return relativeIntensity;
		}

		public Pair<Double, Double> getRelativeIntensityMeanAndStdev() {
			if (relativeIntensity.size() == 0) {
				return new Pair<Double, Double>(0.0, 0.0);
			} else {
				final double[] corrs = RcpaCollectionUtils
						.toDoubleArray(relativeIntensity);
				final double avg = BIJStats.avg(corrs);
				final double stdev = BIJStats.stdev(corrs);

				return new Pair<Double, Double>(avg, stdev);
			}
		}

		public int getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}

		public double getMatchPercent() {
			if (totalCount == 0) {
				return 0.0;
			}

			return (double) matchCount / (double) totalCount;
		}
	}

	private class ResultItem {
		private int charge;

		private int spectraCount = 0;

		private int averagePeakCount = 0;

		private int averageSelectedPeakCount = 0;

		private Map<IonType, IonTypeFrequency> ionTypeFrequency;

		public int getAveragePeakCount() {
			return averagePeakCount;
		}

		public void setAveragePeakCount(int averagePeakCount) {
			this.averagePeakCount = averagePeakCount;
		}

		public int getAverageSelectedPeakCount() {
			return averageSelectedPeakCount;
		}

		public void setAverageSelectedPeakCount(int averageSelectedPeakCount) {
			this.averageSelectedPeakCount = averageSelectedPeakCount;
		}

		public int getCharge() {
			return charge;
		}

		public void setCharge(int charge) {
			this.charge = charge;
		}

		public Map<IonType, IonTypeFrequency> getIonTypeFrequency() {
			return ionTypeFrequency;
		}

		public void setIonTypeFrequency(
				Map<IonType, IonTypeFrequency> ionTypeFrequency) {
			this.ionTypeFrequency = ionTypeFrequency;
		}

		public int getSpectraCount() {
			return spectraCount;
		}

		public void setSpectraCount(int spectraCount) {
			this.spectraCount = spectraCount;
		}
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader()
				.read(originFile);
		Map<String, List<BuildSummaryPeptideHit>> pepMap = getExperimentalPeptideMap(pephits);

		Map<Integer, ResultItem> chargeIonTypeFrequencyMap = new LinkedHashMap<Integer, ResultItem>();
		for (int charge = 1; charge <= 3; charge++) {
			ResultItem item = new ResultItem();
			chargeIonTypeFrequencyMap.put(charge, item);

			item.setCharge(charge);

			Map<IonType, IonTypeFrequency> ionTypeFrequency = new LinkedHashMap<IonType, IonTypeFrequency>();
			IonType[] curIonTypes = getCurrentIonTypes(charge);
			for (IonType iType : curIonTypes) {
				ionTypeFrequency.put(iType, new IonTypeFrequency());
			}
			item.setIonTypeFrequency(ionTypeFrequency);
		}

		for (String exp : pepMap.keySet()) {
			if (!experimentalMap.containsKey(exp)) {
				throw new Exception("Cannot find experimental " + exp
						+ " in directory " + dir);
			}

			System.out.println(exp);

			Map<String, InfoItem> filenamePeptideMap = getFileNamePeptideMap(pepMap
					.get(exp));
			OutsFileIterator outReader = new OutsFileIterator(
					new BufferedReader(new FileReader(
							experimentalMap.get(exp).snd)));
			DtasFileIterator dtaReader = new DtasFileIterator(
					new BufferedReader(new FileReader(
							experimentalMap.get(exp).fst)));
			while (outReader.hasNext()) {
				Pair<String, String[]> outContent = outReader.next();
				String dtaFilename = RcpaFileUtils.changeExtension(
						outContent.fst, ".dta");

				InfoItem outItem = filenamePeptideMap.get(dtaFilename);
				if (outItem != null) {
					outItem.setOutContent(outContent.snd);
					if (outItem.dtaContent != null) {
						calculate(chargeIonTypeFrequencyMap, dtaFilename,
								outItem);
						filenamePeptideMap.remove(dtaFilename);
					}
				}

				Pair<String, String[]> dtaContent = dtaReader.next();
				InfoItem dtaItem = filenamePeptideMap.get(dtaContent.fst);
				if (dtaItem != null) {
					dtaItem.setDtaContent(dtaContent.snd);
					if (dtaItem.outContent != null) {
						calculate(chargeIonTypeFrequencyMap, dtaContent.fst,
								dtaItem);
						filenamePeptideMap.remove(dtaContent.fst);
					}
				}
			}

			for (InfoItem item : filenamePeptideMap.values()) {
				if (null == item.getDtaContent()
						|| null == item.getOutContent()) {
					throw new Exception("Cannot find dta/out content of "
							+ item.getPephit().getPeakListInfo()
									.getLongFilename());
				}
			}
		}

		DecimalFormat df2 = new DecimalFormat("0.00");
		DecimalFormat df8 = new DecimalFormat("0.########");
		String resultFile = originFile + "_" + df8.format(intensityTolerance)
				+ ".frequency";
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			pw.println("Directory=" + dir);
			pw.println();
			for (int charge = 1; charge <= 3; charge++) {
				ResultItem resultItem = chargeIonTypeFrequencyMap.get(charge);
				if (resultItem.getSpectraCount() == 0) {
					continue;
				}

				resultItem.setAveragePeakCount(resultItem.getAveragePeakCount()
						/ resultItem.getSpectraCount());
				resultItem.setAverageSelectedPeakCount(resultItem
						.getAverageSelectedPeakCount()
						/ resultItem.getSpectraCount());

				pw.println("Charge=" + charge);
				pw.println("SpectraCount=" + resultItem.getSpectraCount());
				pw.println("AveragePeakCount="
						+ resultItem.getAveragePeakCount());
				pw.println("AverageSelectedPeakCount="
						+ resultItem.getAverageSelectedPeakCount());
				pw.println("AverageSelectedPeakPercentage="
						+ df2.format(((double) resultItem
								.getAverageSelectedPeakCount() * 100)
								/ resultItem.getAveragePeakCount()) + "%");

				pw
						.println("IonType\tMatchedCount\tTheoreticalCount\tPercent\tMeanOfIntensity\tStdevOfIntensity\tMeanOfRelativeIntensity\tStdevOfRelativeIntensity");
				IonType[] curIonTypes = getCurrentIonTypes(charge);
				for (IonType iType : curIonTypes) {
					pw.print(iType);
					Map<IonType, IonTypeFrequency> ionTypeFrequency = resultItem
							.getIonTypeFrequency();
					IonTypeFrequency frequency = ionTypeFrequency.get(iType);
					pw.print("\t" + frequency.getMatchCount());
					pw.print("\t" + frequency.getTotalCount());
					pw.print("\t"
							+ df2.format(frequency.getMatchPercent() * 100)
							+ "%");

					Pair<Double, Double> intensityMeanAndStdev = frequency
							.getIntensityMeanAndStdev();
					pw.print("\t" + df2.format(intensityMeanAndStdev.fst)
							+ "\t" + df2.format(intensityMeanAndStdev.snd));

					Pair<Double, Double> relativeIntensityMeanAndStdev = frequency
							.getRelativeIntensityMeanAndStdev();
					pw.println("\t"
							+ df2.format(relativeIntensityMeanAndStdev.fst)
							+ "%" + "\t"
							+ df2.format(relativeIntensityMeanAndStdev.snd)
							+ "%");
				}
				pw.println();
			}
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

	private IonType[] getCurrentIonTypes(int charge) {
		IonType[] curIonTypes = charge < 3 ? ionTypesCharge12 : ionTypes;
		return curIonTypes;
	}

	private void calculate(Map<Integer, ResultItem> chargeIonTypeFrequencyMap,
			String dtaFilename, InfoItem item) throws Exception {
		BuildSummaryPeptideHit phit = item.pephit;

		ResultItem resultItem;

		if (phit.getPeakListInfo().getCharge() >= 3) {
			resultItem = chargeIonTypeFrequencyMap.get(3);
		} else {
			resultItem = chargeIonTypeFrequencyMap.get(phit.getPeakListInfo()
					.getCharge());
		}

		resultItem.setSpectraCount(resultItem.getSpectraCount() + 1);

		Map<IonType, IonTypeFrequency> ionTypeFrequency = resultItem
				.getIonTypeFrequency();

		IonType[] curIonTypes = getCurrentIonTypes(phit.getPeakListInfo()
				.getCharge());

		SequestPeptideResult spr = new SequestPeptideResult(phit.getPeptide(0)
				.getSequence(), curIonTypes);

		String outFilename = RcpaFileUtils.changeExtension(dtaFilename, "out");

		spr.parseDtaFile(dtaFilename, item.getDtaContent());
		spr.parseOutFile(item.getOutContent(), outFilename);

		PeakList<MatchedPeak> experimentalPeaks = spr.getExperimentalPeakList();
		resultItem.setAveragePeakCount(resultItem.getAveragePeakCount()
				+ experimentalPeaks.getPeaks().size());

		removeIntensityLessThanTolerance(experimentalPeaks);
		resultItem.setAverageSelectedPeakCount(resultItem
				.getAverageSelectedPeakCount()
				+ experimentalPeaks.getPeaks().size());

		Map<IonType, List<MatchedPeak>> ionTypePeaks = spr.getTheoreticalIonSeries();
		double theoreticalPrecursorMz = spr.getPrecursorMass();
		if (Math.abs(theoreticalPrecursorMz - experimentalPeaks.getPrecursor()) > 3.1) {
			throw new Exception(dtaFilename + " : throretical PrecursorMass "
					+ theoreticalPrecursorMz
					+ " is not equals experimental PrecursorMass "
					+ experimentalPeaks.getPrecursor());
		}

		for (IonType iType : ionTypePeaks.keySet()) {
			List<MatchedPeak> theoreticalPeaks = ionTypePeaks.get(iType);
			IonTypeFrequency frequency = ionTypeFrequency.get(iType);
			double totalIntensity = experimentalPeaks.getTotalIntensity();

			int matchedCount = 0;
			for (MatchedPeak peak : theoreticalPeaks) {
				for (MatchedPeak thePeak : experimentalPeaks.getPeaks()) {
					if (Math.abs(peak.getMz() - thePeak.getMz()) <= mzTolerance) {
						matchedCount++;
						frequency.getIntensity().add(thePeak.getIntensity());
						double relativeIntensity = thePeak.getIntensity()
								/ totalIntensity * 100;
						frequency.getRelativeIntensity().add(relativeIntensity);
						break;
					}
				}
			}

			frequency.setMatchCount(frequency.getMatchCount() + matchedCount);
			frequency.setTotalCount(frequency.getTotalCount()
					+ theoreticalPeaks.size());
		}
	}

	private void removeIntensityLessThanTolerance(
			PeakList<MatchedPeak> experimentalPeaks) {
		double totalIntensity = experimentalPeaks.getTotalIntensity();
		double minIntensity = totalIntensity * intensityTolerance;
		for (int i = experimentalPeaks.getPeaks().size() - 1; i >= 0; i--) {
			if (experimentalPeaks.getPeaks().get(i).getIntensity() < minIntensity) {
				experimentalPeaks.getPeaks().remove(i);
			}
		}
	}

	private Map<String, InfoItem> getFileNamePeptideMap(
			List<BuildSummaryPeptideHit> peptides) {
		Map<String, InfoItem> result = new HashMap<String, InfoItem>();
		for (BuildSummaryPeptideHit phit : peptides) {
			String pureSeq = PeptideUtils.getPurePeptideSequence(phit
					.getPeptide(0).getSequence());
			if (pureSeq.indexOf('X') >= 0 || pureSeq.indexOf('B') >= 0
					|| pureSeq.indexOf('Z') >= 0) {
				continue;
			}
			InfoItem item = new InfoItem();
			item.pephit = phit;
			result.put(phit.getPeakListInfo().getLongFilename() + "dta", item);
		}
		return result;
	}

	private Map<String, List<BuildSummaryPeptideHit>> getExperimentalPeptideMap(
			List<BuildSummaryPeptideHit> pephits) {
		Map<String, List<BuildSummaryPeptideHit>> result = new HashMap<String, List<BuildSummaryPeptideHit>>();
		for (BuildSummaryPeptideHit phit : pephits) {
			String exp = phit.getPeakListInfo().getExperiment();
			if (!result.containsKey(exp)) {
				result.put(exp, new ArrayList<BuildSummaryPeptideHit>());
			}
			result.get(exp).add(phit);
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		IonType[] ionTypes = new IonType[] { IonType.B, IonType.Y, IonType.A,
				IonType.X, IonType.C, IonType.Z, IonType.B2, IonType.Y2,
				IonType.B_H2O, IonType.Y_H2O, IonType.B_NH3, IonType.Y_NH3,
				IonType.B_H2O_NH3, IonType.Y_H2O_NH3 };

		double mzTolerance = 0.5;

		// double[] intensityTolerances = new double[] { 0.01, 0.002, 0.001,
		// 0.0001
		// };
		double[] intensityTolerances = new double[] { 0.01, 0.001 };
		for (double intensityTolerance : intensityTolerances) {
			individual(ionTypes, mzTolerance, intensityTolerance);
			// jackchen(ionTypes, mzTolerance, intensityTolerance);
			// hppp(ionTypes, mzTolerance, intensityTolerance);
		}
	}

	public static void hppp(IonType[] ionTypes, double mzTolerance,
			double intensityTolerance) throws Exception {
		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\hppp\\sequest\\LCQ_Reversed", ionTypes,
				mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\hppp\\summary_0.01\\lcq_0.01\\lcq_0.01.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\hppp\\sequest\\LTQ_Reversed", ionTypes,
				mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\hppp\\summary_0.01\\ltq_0.01\\ltq_0.01.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\hppp\\sequest\\Orbitrap_Reversed", ionTypes,
				mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\hppp\\summary_0.01\\orbitrap_0.01_05ppm\\orbitrap_0.01_05ppm.peptides");
	}

	public static void individual(IonType[] ionTypes, double mzTolerance,
			double intensityTolerance) throws Exception {
		File rootDir = new File(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Individual");
		File[] subDirs = rootDir.listFiles(new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}
		});

		for (File subDir : subDirs) {
			File[] paramFile = subDir.listFiles(new FilenameFilter() {
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".param");
				}
			});

			if (paramFile.length == 0) {
				continue;
			}

			File[] peptidesFile = subDir.listFiles(new FilenameFilter() {
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".peptides");
				}
			});

			if (peptidesFile.length == 0) {
				continue;
			}

			String dataDir = "";
			BufferedReader br = new BufferedReader(new FileReader(paramFile[0]
					.getAbsolutePath()));
			try {
				String line;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("<Directory>")) {
						dataDir = line.substring(11, line.length() - 12);
						break;
					}
				}
			} finally {
				br.close();
			}
			new IonTypeFrequencyCalculator(dataDir, ionTypes, mzTolerance,
					intensityTolerance).process(peptidesFile[0]
					.getAbsolutePath());
		}
	}

	public static void jackchen(IonType[] ionTypes, double mzTolerance,
			double intensityTolerance) throws Exception {
		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\INCAPS\\Pilot Study 11_13_06\\LTQ_micro\\A\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\INCAPS_111306_LTQ_micro_A\\INCAPS_111306_LTQ_micro_A.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\INCAPS\\Pilot Study 11_13_06\\LTQ_micro\\B\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\INCAPS_111306_LTQ_micro_B\\INCAPS_111306_LTQ_micro_B.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\INCAPS\\Pilot Study 11_13_06\\LTQ_nano\\A\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\INCAPS_111306_LTQ_nano_A\\INCAPS_111306_LTQ_nano_A.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\INCAPS\\Pilot Study 11_13_06\\LTQ_nano\\B\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\INCAPS_111306_LTQ_nano_B\\INCAPS_111306_LTQ_nano_B.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\INCAPS\\Pilot Study 11_20_06\\Micro_LTQ\\A\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\INCAPS_112006_LTQ_micro_A\\INCAPS_112006_LTQ_micro_A.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\INCAPS\\Pilot Study 11_20_06\\Micro_LTQ\\B\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\INCAPS_112006_LTQ_micro_B\\INCAPS_112006_LTQ_micro_B.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\PURDUE\\Nov-11-13\\Sample_1A1\\LTQ\\Raw_Data\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\PURDUE_111306_LTQ_micro_A\\PURDUE_111306_LTQ_micro_A.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\PURDUE\\Nov-11-13\\Sample_1B1\\LTQ\\Raw_Data\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\PURDUE_111306_LTQ_micro_B\\PURDUE_111306_LTQ_micro_B.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\PURDUE\\Nov-11-20\\Sample_1A2\\LTQ\\Raw_Data\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\PURDUE_112006_LTQ_micro_A\\PURDUE_112006_LTQ_micro_A.peptides");

		new IonTypeFrequencyCalculator(
				"F:\\sqh\\Project\\jakechen\\atp_regnier\\PURDUE\\Nov-11-20\\Sample_1B2\\LTQ\\Raw_Data\\tryptic",
				ionTypes, mzTolerance, intensityTolerance)
				.process("F:\\sqh\\Project\\jakechen\\atp_regnier\\summary\\Replication\\PURDUE_112006_LTQ_micro_B\\PURDUE_112006_LTQ_micro_B.peptides");
	}
}
