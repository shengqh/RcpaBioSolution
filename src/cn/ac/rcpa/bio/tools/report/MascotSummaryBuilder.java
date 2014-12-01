package cn.ac.rcpa.bio.tools.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.detectability.DetectabilityEntry;
import cn.ac.rcpa.bio.proteomics.detectability.DetectabilityImageBuilder;
import cn.ac.rcpa.bio.proteomics.detectability.ProteinDetectabilityEntry;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotPeptide;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotPeptidePValueFilter;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotProtein;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotResult;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotResultHtmlParser;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotResultTextWriter;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.ShellUtils;

public class MascotSummaryBuilder implements IFileProcessor {

	public static String version = "1.0.1";

	DecimalFormat df4 = new DecimalFormat("0.0000");

	DecimalFormat dfe = new DecimalFormat("0.##E0");

	private String detectabilityProgram;

	// private String detectabilityDir = "F:\\sqh\\ScienceTools\\Detectability";
	// private String[] detectabilityPrograms = new String[] {
	// "Detectability.exe",
	// "stand.bin" };
	//
	private MascotResultHtmlParser parser;

	private String database;

	public MascotSummaryBuilder(String detectabilityProgram, double pValue,
			String database, SequenceDatabaseType dbType) {
		super();
		this.detectabilityProgram = detectabilityProgram;
		MascotPeptidePValueFilter pvalueFilter = new MascotPeptidePValueFilter(
				pValue);
		this.parser = new MascotResultHtmlParser(pvalueFilter, false);
		this.database = database;
	}

	public List<String> process(String mascotResultDir) throws Exception {
		File[] mascotResultFiles = new File(mascotResultDir)
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						String upper = name.toUpperCase();
						return upper.endsWith(".HTM") || upper.endsWith(".HTML");
					}
				});

		if (mascotResultFiles.length == 0) {
			throw new IllegalArgumentException(
					"There is no mascot result file (.htm) in directory "
							+ mascotResultDir);
		}

		MascotResult mr = new MascotResult();
		for (File mascotResultFile : mascotResultFiles) {
			System.out.print("Paring " + mascotResultFile.getName() + " ... ");
			MascotResult curr = parser.parseFile(mascotResultFile);
			System.out.println("finished.");

			System.runFinalization();
			System.gc();

			boolean bFound = false;
			for (MascotProteinGroup mp : curr) {
				for (MascotProteinGroup old : mr) {
					if (mp.get(0).getProteinName().equals(old.get(0).getProteinName())) {
						for (MascotProtein oldMp : old) {
							oldMp.getPeptides().addAll(mp.get(0).getPeptides());
						}
						bFound = true;
						break;
					}
				}
				if (!bFound) {
					mr.add(mp);
				}
			}
		}

		if (mr.size() == 0) {
			throw new IllegalArgumentException(
					"There is no protein identified by mascot");
		}

		System.out.println("Reading protein sequences from " + database + " ...");
		SequenceIterator seqi = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(database)));
		while (seqi.hasNext()) {
			Sequence seq = seqi.nextSequence();
			for (MascotProteinGroup mpg : mr) {
				for (MascotProtein mp : mpg) {
					if (seq.getName().contains(mp.getProteinName())) {
						mp.setProteinName(seq.getName());
						mp.setReference(SequenceUtils.getProteinReference(seq));
						mp.setSequence(seq.seqString());
						break;
					}
				}
			}
		}
		System.out.println("Reading protein sequences from " + database
				+ " finished.");

		String resultFile = printProteinsFile(mascotResultDir, mr);
		String resultFastaFile = printProteinsFastaFile(mascotResultDir, mr);

		Map<String, MascotProtein> name2mpMap = new HashMap<String, MascotProtein>();
		Map<MascotProtein, String> mp2nameMap = new HashMap<MascotProtein, String>();
		for (int i = 0; i < mr.size(); i++) {
			MascotProteinGroup mpg = mr.get(i);
			for (int j = 0; j < mpg.size(); j++) {
				MascotProtein mp = mpg.get(j);
				String proteinName = "protein_" + i + "_" + j;
				name2mpMap.put(proteinName, mp);
				mp2nameMap.put(mp, proteinName);
			}
		}

		String targetDetectabilityDir = resultFastaFile + ".detectability";
		// String proteinFile = targetDetectabilityDir
		// + "\\purvy.txt";
		String proteinFile = targetDetectabilityDir + "\\proteins.fasta";
		new File(targetDetectabilityDir).mkdir();
		PrintWriter pwDetectabilityFasta = new PrintWriter(proteinFile);
		try {
			for (int i = 0; i < mr.size(); i++) {
				MascotProteinGroup mpg = mr.get(i);
				for (int j = 0; j < mpg.size(); j++) {
					MascotProtein mp = mpg.get(j);
					String proteinName = mp2nameMap.get(mp);
					pwDetectabilityFasta.println(">" + proteinName);
					pwDetectabilityFasta.println(mp.getSequence());
				}
			}
		} finally {
			pwDetectabilityFasta.close();
		}

		// call detectability program
		// for (String file : detectabilityPrograms) {
		// FileUtils.copyFileToDirectory(new File(detectabilityDir + "\\" + file),
		// new File(targetDetectabilityDir));
		// }
		// ShellUtils.execute(
		// targetDetectabilityDir + "\\" + detectabilityPrograms[0], new File(
		// targetDetectabilityDir));
		ShellUtils.execute(
				new String[] { detectabilityProgram, "-F", proteinFile }, new File(
						targetDetectabilityDir),true);
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

		for (MascotProteinGroup mpg : mr) {
			for (MascotProtein mp : mpg) {
				String mapName = mp2nameMap.get(mp);
				Map<String, DetectabilityEntry> peptideDetectabilityMap = proteinPeptideMap
						.get(mapName).getPeptideMap();

				for (MascotPeptide mpep : mp.getPeptides()) {
					String pureSeq = PeptideUtils.getPurePeptideSequence(mpep
							.getSequence());
					if (peptideDetectabilityMap.containsKey(pureSeq)) {
						DetectabilityEntry de = peptideDetectabilityMap.get(pureSeq);
						de.getExperimentals().add(mpep.getPeakListInfo().getExperiment());
						if (DetectabilityEntry.DEFAULT_SCORE == de.getScore()
								|| de.getScore() > mpep.getPValue()) {
							de.setScore(mpep.getPValue());
						}
					}
				}
			}
		}

		List<ProteinDetectabilityEntry> proteinProbabilityList = new ArrayList<ProteinDetectabilityEntry>(
				proteinPeptideMap.values());
		for (ProteinDetectabilityEntry pde : proteinProbabilityList) {
			pde.setProbability(getProbability(pde.getPeptideMap().values()));
		}

		// sort by probability descending
		Collections.sort(proteinProbabilityList,
				new Comparator<ProteinDetectabilityEntry>() {
					public int compare(ProteinDetectabilityEntry o1,
							ProteinDetectabilityEntry o2) {
						return Double.compare(o2.getProbability(), o1.getProbability());
					}
				});

		String resultDetectabilityFile = resultFile + ".detectability";
		PrintWriter pw = new PrintWriter(resultDetectabilityFile);
		try {
			pw.println("Protein\tProbability\tUniPepCount");
			for (ProteinDetectabilityEntry protein : proteinProbabilityList) {
				pw.println(protein.getName() + "\t" + protein.getProbability() + "\t"
						+ protein.getUniquePeptideCount());
			}

			pw.println();

			pw.println("Protein\tPeptide\tDetectability\tDetected\tPValue");
			for (ProteinDetectabilityEntry protein : proteinProbabilityList) {
				List<DetectabilityEntry> peptides = new ArrayList<DetectabilityEntry>(
						protein.getPeptideMap().values());
				Collections.sort(peptides, new Comparator<DetectabilityEntry>() {
					public int compare(DetectabilityEntry o1, DetectabilityEntry o2) {
						return Double.compare(o2.getDetectability(), o1.getDetectability());
					}
				});

				for (DetectabilityEntry de : peptides) {
					pw.print(protein.getName() + "\t" + de.getPeptide() + "\t"
							+ de.getDetectability());
					if (de.isDetected()) {
						pw.println("\t1\t" + dfe.format(de.getScore()));
					} else {
						pw.println("\t0\t-");
					}
				}
			}
		} finally {
			pw.close();
		}

		String resultImageFile = resultDetectabilityFile + ".png";
		DetectabilityImageBuilder
				.drawImage(resultImageFile, proteinProbabilityList);

		return Arrays.asList(new String[] { resultFile, resultFastaFile,
				resultDetectabilityFile, resultImageFile });
	}

	private double getProbability(Collection<DetectabilityEntry> des) {
		double minDetectability = 1.1;
		for (DetectabilityEntry de : des) {
			if (de.isDetected() && de.getDetectability() < minDetectability) {
				minDetectability = de.getDetectability();
			}
		}

		List<DetectabilityEntry> candidateDes = new ArrayList<DetectabilityEntry>();
		for (DetectabilityEntry de : des) {
			if (de.getDetectability() >= minDetectability) {
				candidateDes.add(de);
			}
		}

		if (0 == candidateDes.size()) {
			return 0;
		}

		double ppro = 0.5;
		double ppostpro_exist = 1.0;
		for (DetectabilityEntry de : candidateDes) {
			// ppostpro_exist *= 0.5;
			if (de.isDetected()) {
				ppostpro_exist *= de.getDetectability();
			} else {
				ppostpro_exist *= (1 - de.getDetectability());
			}
		}

		double ppostpro_notexist = 1.0;
		for (DetectabilityEntry de : des) {
			if (de.isDetected()) {
				ppostpro_notexist *= de.getScore();
			}
		}

		double result = ppro * ppostpro_exist
				/ (ppro * ppostpro_exist + (1 - ppro) * ppostpro_notexist);

		return result;
	}

	private String printProteinsFastaFile(String originDir, MascotResult mr)
			throws FileNotFoundException {
		// write protein fasta file
		String resultFastaFile = originDir + ".proteins.fasta";
		PrintWriter pwFasta = new PrintWriter(resultFastaFile);
		try {
			for (MascotProteinGroup mpg : mr) {
				for (MascotProtein mp : mpg) {
					pwFasta.println(">" + mp.getProteinName() + " " + mp.getReference());
					pwFasta.println(mp.getSequence());
				}
			}
		} finally {
			pwFasta.close();
		}
		return resultFastaFile;
	}

	private String printProteinsFile(String originDir, MascotResult mr)
			throws Exception {
		String resultFile = originDir + ".proteins";

		new MascotResultTextWriter().write(resultFile, mr);

		return resultFile;
	}
}
