package cn.ac.rcpa.bio.tools.report;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.detectability.DetectabilityEntry;
import cn.ac.rcpa.bio.proteomics.detectability.DetectabilityImageBuilder;
import cn.ac.rcpa.bio.proteomics.detectability.IProteinProbabilityCalculator;
import cn.ac.rcpa.bio.proteomics.detectability.ProteinDetectabilityEntry;
import cn.ac.rcpa.bio.proteomics.detectability.ProteinProbabilityCalculatorMDepth;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.utils.Pair;
import cn.ac.rcpa.utils.ShellUtils;

public class SequestDetectabilitySummaryBuilder implements IFileProcessor {

	public static String version = "1.0.0";

	DecimalFormat df4 = new DecimalFormat("0.0000");

	DecimalFormat dfe = new DecimalFormat("0.##E0");

	private double fdr;

	private String detectabilityProgram;

	public SequestDetectabilitySummaryBuilder(String detectabilityProgram,
			double fdr) {
		super();
		this.detectabilityProgram = detectabilityProgram;
		this.fdr = fdr;
	}

	public List<String> process(String proteinFilename) throws Exception {
		BuildSummaryResult sr = new BuildSummaryResultReader()
				.read(proteinFilename);

		if (sr.getProteins().size() == 0) {
			throw new IllegalArgumentException(
					"There is no protein identified by sequest");
		}

		Map<String, BuildSummaryProtein> name2mpMap = new HashMap<String, BuildSummaryProtein>();
		Map<BuildSummaryProtein, String> mp2nameMap = new HashMap<BuildSummaryProtein, String>();
		List<BuildSummaryProtein> proteins = new ArrayList<BuildSummaryProtein>();
		for (int i = 0; i < sr.getProteinGroupCount(); i++) {
			proteins.add(sr.getProteinGroup(i).getProtein(0));
		}
		for (int i = 0; i < proteins.size(); i++) {
			BuildSummaryProtein mp = proteins.get(i);
			String proteinName = "protein"
					+ StringUtils.leftPad(Integer.toString(i), 8, '0');
			name2mpMap.put(proteinName, mp);
			mp2nameMap.put(mp, proteinName);
		}

		String resultFastaFile = proteinFilename + ".fasta";

		String targetDetectabilityDir = resultFastaFile + ".detectability";

		String proteinFile = targetDetectabilityDir + "\\proteins.fasta";
		new File(targetDetectabilityDir).mkdir();
		PrintWriter pwDetectabilityFasta = new PrintWriter(proteinFile);
		try {
			for (int i = 0; i < proteins.size(); i++) {
				BuildSummaryProtein mp = proteins.get(i);
				String proteinName = mp2nameMap.get(mp);
				pwDetectabilityFasta.println(">" + proteinName);
				pwDetectabilityFasta.println(mp.getSequence());
			}
		} finally {
			pwDetectabilityFasta.close();
		}

		ShellUtils.execute(
				new String[] { detectabilityProgram, "-F", proteinFile }, new File(
						targetDetectabilityDir), true);
		List<DetectabilityEntry> deList = DetectabilityEntry
				.readDetectabilityEntryList(targetDetectabilityDir);

		Map<String, ProteinDetectabilityEntry> proteinPeptideMap = new LinkedHashMap<String, ProteinDetectabilityEntry>();
		for (DetectabilityEntry de : deList) {
			if (!proteinPeptideMap.containsKey(de.getProtein())) {
				ProteinDetectabilityEntry pde = new ProteinDetectabilityEntry();
				String realName = name2mpMap.get(de.getProtein()).getProteinName();
				pde.setName(realName);
				proteinPeptideMap.put(de.getProtein(), pde);
			}
			Map<String, DetectabilityEntry> peptideMap = proteinPeptideMap.get(
					de.getProtein()).getPeptideMap();
			peptideMap.put(de.getPeptide(), de);
		}

		for (BuildSummaryProtein mp : proteins) {
			String mapName = mp2nameMap.get(mp);
			Map<String, DetectabilityEntry> peptideDetectabilityMap = proteinPeptideMap
					.get(mapName).getPeptideMap();

			for (BuildSummaryPeptide mpep : mp.getPeptides()) {
				String pureSeq = PeptideUtils
						.getPurePeptideSequence(mpep.getSequence());
				if (peptideDetectabilityMap.containsKey(pureSeq)) {
					DetectabilityEntry de = peptideDetectabilityMap.get(pureSeq);
					de.getExperimentals().add(mpep.getPeakListInfo().getExperiment());
					if (de.getScore() == DetectabilityEntry.DEFAULT_SCORE) {
						de.setScore(this.fdr);
					} else {
						de.setScore(this.fdr);
						// de.setScore(de.getScore() * this.fdr);
					}
				}
			}
		}

		List<ProteinDetectabilityEntry> proteinProbabilityList = new ArrayList<ProteinDetectabilityEntry>(
				proteinPeptideMap.values());

		IProteinProbabilityCalculator calc = new ProteinProbabilityCalculatorMDepth();
		for (ProteinDetectabilityEntry pde : proteinProbabilityList) {
			pde.setProbability(calc.getProbability(pde));
		}
		// sort by probability descending
		Collections.sort(proteinProbabilityList,
				new Comparator<ProteinDetectabilityEntry>() {
					public int compare(ProteinDetectabilityEntry o1,
							ProteinDetectabilityEntry o2) {
						return Double.compare(o2.getProbability(), o1.getProbability());
					}
				});

		Map<String, List<ProteinDetectabilityEntry>> proteinMap = new HashMap<String, List<ProteinDetectabilityEntry>>();
		for (ProteinDetectabilityEntry pde : proteinProbabilityList) {
			String type = "";
			if (pde.getName().startsWith("REVERSED_")) {
				type = "REV_";
			} else {
				type = "IPI_";
			}

			if (pde.getUniquePeptideCount() > 1) {
				type = type + "2";
			} else {
				type = type + "1";
			}

			if (!proteinMap.containsKey(type)) {
				proteinMap.put(type, new ArrayList<ProteinDetectabilityEntry>());
			}
			proteinMap.get(type).add(pde);
		}

		String resultDetectabilityFile = proteinFilename + ".detectability";
		PrintWriter pw = new PrintWriter(resultDetectabilityFile);
		try {
			pw.println("Protein\tGroup\tProbability\tUniPepCount");
			for (ProteinDetectabilityEntry protein : proteinProbabilityList) {
				int group = 0;
				if (!protein.getName().startsWith("REVERSED_")) {
					if (protein.getUniquePeptideCount() > 1) {
						group = 2;
					} else {
						group = 1;
					}
				}
				pw
						.println(protein.getName() + "\t" + group + "\t"
								+ protein.getProbability() + "\t"
								+ protein.getUniquePeptideCount());
			}
			pw.println();

			List<String> types = new ArrayList<String>(proteinMap.keySet());
			Collections.sort(types);
			for (String type : types) {
				pw.print("\t" + type + "\t" + type + "Count");
			}
			pw.println("\tFDR_1");

			DecimalFormat df2 = new DecimalFormat("0.00");
			double[] ranges = new double[] { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7,
					0.8, 0.9, 1, 0.81, 0.82, 0.83, 0.84, 0.85, 0.86, 0.87, 0.88, 0.89 };
			for (double proteinProbability : ranges) {
				pw.print(df2.format(proteinProbability));

				Map<String, Pair<Integer, Double>> countMap = new HashMap<String, Pair<Integer, Double>>();

				for (String type : types) {
					List<ProteinDetectabilityEntry> pdes = proteinMap.get(type);
					int count = 0;
					for (ProteinDetectabilityEntry pde : pdes) {
						if (pde.getProbability() >= proteinProbability) {
							count++;
						}
					}
					countMap.put(type, new Pair<Integer, Double>(count, (double) count
							/ (double) pdes.size()));
				}

				for (String type : types) {
					Pair<Integer, Double> p = countMap.get(type);
					pw.print("\t" + df2.format(p.snd) + "\t" + p.fst);
				}
				double fdr_1 = 0.0;
				if (countMap.containsKey("REV_1")) {
					fdr_1 = 2
							* countMap.get("REV_1").fst
							/ (double) (countMap.get("REV_1").fst + countMap.get("IPI_1").fst);
				}
				pw.println("\t" + df2.format(fdr_1));
			}

			Map<String, Map<Integer, Integer>> distributionMap = new HashMap<String, Map<Integer, Integer>>();
			for (String type : types) {
				List<ProteinDetectabilityEntry> pdes = proteinMap.get(type);
				Map<Integer, Integer> countMap = new LinkedHashMap<Integer, Integer>();
				distributionMap.put(type, countMap);
				for (int i = 0; i <= 10; i++) {
					countMap.put(i, 0);
				}
				for (ProteinDetectabilityEntry pde : pdes) {
					for (DetectabilityEntry de : pde.getPeptideMap().values()) {
						if (de.isDetected()) {
							int range = (int) (de.getDetectability() * 10);
							countMap.put(range, countMap.get(range) + 1);
						}
					}
				}
			}

			pw.println();
			for (String type : types) {
				pw.print("\t" + type);
			}
			pw.println();
			for (int i = 0; i <= 10; i++) {
				pw.print(i);
				for (String type : types) {
					pw.print("\t" + distributionMap.get(type).get(i));
				}
				pw.println();
			}
		} finally {
			pw.close();
		}

		String resultDetectabilityDetailFile = proteinFilename
				+ ".detectability.details";
		PrintWriter pwDetails = new PrintWriter(resultDetectabilityDetailFile);
		try {
			pwDetails.println("Protein\tPeptide\tDetectability\tDetected\tPValue");
			for (ProteinDetectabilityEntry protein : proteinProbabilityList) {
				List<DetectabilityEntry> peptides = new ArrayList<DetectabilityEntry>(
						protein.getPeptideMap().values());
				Collections.sort(peptides, new Comparator<DetectabilityEntry>() {
					public int compare(DetectabilityEntry o1, DetectabilityEntry o2) {
						return Double.compare(o2.getDetectability(), o1.getDetectability());
					}
				});

				for (DetectabilityEntry de : peptides) {
					pwDetails.print(protein.getName() + "\t" + de.getPeptide() + "\t"
							+ de.getDetectability());
					if (de.isDetected()) {
						pwDetails.println("\t1\t" + dfe.format(de.getScore()));
					} else {
						pwDetails.println("\t0\t-");
					}
				}
			}
		} finally {
			pwDetails.close();
		}

		String resultImageFile = resultDetectabilityFile + ".png";
		DetectabilityImageBuilder
				.drawImage(resultImageFile, proteinProbabilityList);

		return Arrays.asList(new String[] { resultDetectabilityFile,
				resultDetectabilityDetailFile, resultImageFile });
	}
}
