package cn.ac.rcpa.bio.tools.alignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IIdentifiedProtein;
import cn.ac.rcpa.bio.proteomics.IIdentifiedProteinGroup;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultFileType;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupFilterByProteinFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinNameFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.Pair;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.ShellUtils;

/**
 * <p>
 * Title: RCPA Package
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: RCPA.SIBS.AC.CN
 * </p>
 * 
 * @author Sheng Quan-Hu
 * @version 1.0
 */
public class PairwiseAlignment {
	private static PairwiseAlignment instance;

	public static PairwiseAlignment getInstance() {
		if (instance == null) {
			instance = new PairwiseAlignment();
		}
		return instance;
	}

	private PairwiseAlignment() {
		File clustalw = new File("extends\\clustalw.exe");
		if (!clustalw.exists()) {
			throw new IllegalStateException("Program " + clustalw.getAbsolutePath()
					+ " is not exists!");
		}
	}

	public Pattern distancePattern = Pattern.compile(".+\\:(.+)\\);$");

	public boolean clustalw(String inputFile) {
		String[] command = { "extends\\clustalw.exe", "\"" + inputFile + "\"" };
		return ShellUtils.execute(command, false);
	}

	public void alignment(File fastaFile, SequenceDatabaseType dbType)
			throws FileNotFoundException, BioException, NoSuchElementException,
			IOException {
		File dir = new File(fastaFile.getParentFile(), "alignment");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		IAccessNumberParser parser = AccessNumberParserFactory.getParser(dbType);

		List<Sequence> allSeq = SequenceUtils.readFastaProteins(fastaFile);

		for (int i = 0; i < allSeq.size(); i++) {
			final String namei = parser.getValue(allSeq.get(i).getName());
			for (int j = i + 1; j < allSeq.size(); j++) {
				final String namej = parser.getValue(allSeq.get(j).getName());
				File alignFastaFile = new File(dir, namei + "_" + namej + ".fasta");
				PrintWriter pw = new PrintWriter(new FileWriter(alignFastaFile));
				pw.println(">" + namei);
				pw.println(allSeq.get(i).seqString());
				pw.println(">" + namej);
				pw.println(allSeq.get(j).seqString());
				pw.close();

				clustalw(alignFastaFile.getAbsolutePath());
			}
		}
	}

	public double getEvolutionDistance(File clustalwDndFile)
			throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(clustalwDndFile));
		String line = br.readLine();
		br.close();
		Matcher matcher = distancePattern.matcher(line);
		matcher.find();
		return Double.valueOf(matcher.group(1));
	}

	public double getEvolutionDistance(String name1, String seq1, String name2,
			String seq2, File resultDirectory) throws FileNotFoundException,
			IOException {
		return getEvolutionDistance(name1, seq1, name2, seq2, resultDirectory,
				false);
	}

	public double getEvolutionDistance(String name1, String seq1, String name2,
			String seq2, File resultDirectory, boolean calcAgain)
			throws FileNotFoundException, IOException {
		File fastaFile = new File(resultDirectory, (name1 + "_" + name2 + ".fasta")
				.toLowerCase());

		File dndFile = new File(RcpaFileUtils.changeExtension(fastaFile
				.getAbsolutePath(), "dnd"));

		if (calcAgain || !dndFile.exists()) {
			PrintWriter pw = new PrintWriter(new FileWriter(fastaFile));
			pw.println(">" + name1);
			pw.println(seq1);
			pw.println(">" + name2);
			pw.println(seq2);
			pw.close();

			if (!clustalw(fastaFile.getAbsolutePath())) {
				throw new IllegalStateException("Run clustalw program failed");
			}
		}
		return getEvolutionDistance(dndFile);
	}

	class IntListPair {
		public Integer fst;

		public List snd;

		public IntListPair(Integer fst, List snd) {
			this.fst = fst;
			this.snd = snd;
		};
	};

	private List<Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>>> getEvolutionDistances(
			File identifiedResultFile, IdentifiedResultFileType fileType,
			File targetFastaFile, SequenceDatabaseType dbType)
			throws RcpaParseException, IOException, NoSuchElementException,
			BioException, FileNotFoundException {
		BuildSummaryResult ir = IdentifiedResultIOFactory
				.readBuildSummaryResult(identifiedResultFile.getCanonicalPath());

		IdentifiedProteinNameFilter proteinFilter = new IdentifiedProteinNameFilter(
				"REVERSED_");
		IdentifiedProteinGroupFilterByProteinFilter groupFilter = new IdentifiedProteinGroupFilterByProteinFilter(
				proteinFilter, true);
		for (int i = ir.getProteinGroupCount() - 1; i >= 0; i--) {
			if (groupFilter.accept(ir.getProteinGroup(i))) {
				ir.removeProteinGroup(i);
				continue;
			}
		}

		ir.rebuildGroupIndex();

		List<Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>>> result = new ArrayList<Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>>>();

		IAccessNumberParser parser = AccessNumberParserFactory.getParser(dbType);

		List<Sequence> tarSeqs = SequenceUtils.readFastaProteins(targetFastaFile);
		Set<String> tarAccessNumbers = new HashSet<String>();
		for (Sequence seq : tarSeqs) {
			tarAccessNumbers.add(parser.getValue(seq.getName()));
		}

		File alignmentDir = new File(identifiedResultFile.getParentFile(),
				"alignment");
		if (!alignmentDir.exists()) {
			alignmentDir.mkdirs();
		}

		for (int i = 0; i < ir.getProteinGroupCount(); i++) {
			IIdentifiedProteinGroup ipg = ir.getProteinGroup(i);

			boolean bSkip = false;
			for (int j = 0; j < ipg.getProteinCount(); j++) {
				String srcAccessNumber = parser.getValue(ipg.getProtein(j)
						.getProteinName());
				if (tarAccessNumbers.contains(srcAccessNumber)) {
					// List<Pair<IIdentifiedProtein, Map<String, Double>>> groupList = new
					// ArrayList<Pair<IIdentifiedProtein, Map<String, Double>>>();
					//
					// Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>>
					// groupPair = new Pair<Integer, List<Pair<IIdentifiedProtein,
					// Map<String, Double>>>>(
					// i, groupList);
					//
					// result.add(groupPair);
					//
					// result
					// .add(new Pair<Integer, List<Pair<IIdentifiedProtein, Map<String,
					// Double>>>>(
					// i, null));
					bSkip = true;
					break;
				}
			}
			// if (bSkip) {
			// continue;
			// }

			List<Pair<IIdentifiedProtein, Map<String, Double>>> groupList = new ArrayList<Pair<IIdentifiedProtein, Map<String, Double>>>();

			Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>> groupPair = new Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>>(
					i, groupList);

			result.add(groupPair);

			for (int j = 0; j < ipg.getProteinCount(); j++) {
				IIdentifiedProtein protein = ipg.getProtein(j);
				if (bSkip) {
					groupList.add(new Pair<IIdentifiedProtein, Map<String, Double>>(
							protein, null));
					continue;
				}

				Map<String, Double> alignMap = new HashMap<String, Double>();

				Pair<IIdentifiedProtein, Map<String, Double>> alignPair = new Pair<IIdentifiedProtein, Map<String, Double>>(
						protein, alignMap);

				groupList.add(alignPair);

				final String srcAccessNumber = parser
						.getValue(protein.getProteinName());

				for (Sequence seq : tarSeqs) {
					final String tarAccessNumber = parser.getValue(seq.getName());
					double distance = 0.0;
					if (!srcAccessNumber.equals(tarAccessNumber)) {
						distance = getEvolutionDistance(srcAccessNumber, protein
								.getSequence(), tarAccessNumber, seq.seqString(), alignmentDir);
					}

					final String proteinInfo = SequenceUtils
							.getProteinReference((String) seq.getAnnotation().getProperty(
									"description"));
					if (alignMap.containsKey(proteinInfo)) {
						double oldValue = alignMap.get(proteinInfo);
						if (oldValue < distance) {
							continue;
						}
					}
					alignMap.put(proteinInfo, distance);
				}
			}
		}

		return result;
	}

	private Pair<String, Double> getMinDistancePair(Map<String, Double> alignMap) {
		Pair<String, Double> result = new Pair<String, Double>("Undefined", 10000.0);
		for (String ac : alignMap.keySet()) {
			if (result.snd > alignMap.get(ac)) {
				result = new Pair<String, Double>(ac, alignMap.get(ac));
			}
		}
		return result;
	}

	public void calculateEvolutionDistances(File identifiedResultFile,
			IdentifiedResultFileType fileType, File targetFastaFile,
			SequenceDatabaseType dbType) throws NoSuchElementException, BioException,
			FileNotFoundException, IOException, RcpaParseException {
		File resultFile = new File(RcpaFileUtils.changeExtension(
				identifiedResultFile.getAbsolutePath(), "distances"));
		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));

		final List<Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>>> distanceList = getEvolutionDistances(
				identifiedResultFile, fileType, targetFastaFile, dbType);

		Pair<Integer, Integer> targetProtein = new Pair<Integer, Integer>(0, 0);
		Pair<Integer, Integer> distanceLower = new Pair<Integer, Integer>(0, 0);
		Pair<Integer, Integer> distanceHigher = new Pair<Integer, Integer>(0, 0);

		IAccessNumberParser parser = AccessNumberParserFactory.getParser(dbType);

		DecimalFormat df = new DecimalFormat("0.00");

		List<String> tarAccessNumbers = getTargetAccessNumbers(distanceList);

		pw.print("\t\t");
		pw.print("\tHomoProtein");
		pw.print("\tDistance");
		for (int k = 0; k < tarAccessNumbers.size(); k++) {
			pw.print("\t" + tarAccessNumbers.get(k));
		}
		pw.println();

		for (int i = 0; i < distanceList.size(); i++) {
			final Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>> pair = distanceList
					.get(i);
			final int groupIndex = pair.fst + 1;
			final List<Pair<IIdentifiedProtein, Map<String, Double>>> proteinList = pair.snd;

			if (proteinList == null) {
				continue;
			}

			boolean isTargetProtein = false;
			boolean isDistanceLower = false;

			for (int j = 0; j < proteinList.size(); j++) {
				final IIdentifiedProtein protein = proteinList.get(j).fst;

				final String srcAccessNumber;
				try {
					srcAccessNumber = parser.getValue(protein.getProteinName());
				} catch (Exception ex) {
					continue;
				}

				final Map<String, Double> alignMap = proteinList.get(j).snd;

				pw.print("$" + groupIndex + "-" + (j + 1));
				pw.print("\t" + srcAccessNumber + "\t"
						+ SequenceUtils.getProteinReference(protein.getReference()));
				if (null == alignMap) {
					isTargetProtein = true;
					targetProtein = new Pair<Integer, Integer>(targetProtein.fst,
							targetProtein.snd + 1);
					pw.println();
					continue;
				}

				Pair<String, Double> minPair = getMinDistancePair(alignMap);
				if (minPair.snd <= 0.1) {
					distanceLower = new Pair<Integer, Integer>(distanceLower.fst,
							distanceLower.snd + 1);
					isDistanceLower = true;
					pw.print("\t" + minPair.fst + "\t" + df.format(minPair.snd));
				} else {
					distanceHigher = new Pair<Integer, Integer>(distanceHigher.fst,
							distanceHigher.snd + 1);
					pw.print("\t-\t-");
				}

				for (String ac : tarAccessNumbers) {
					pw.print("\t" + df.format(alignMap.get(ac)));
				}
				pw.println();
			}

			if (isTargetProtein) {
				targetProtein = new Pair<Integer, Integer>(targetProtein.fst + 1,
						targetProtein.snd);
			} else if (isDistanceLower) {
				distanceLower = new Pair<Integer, Integer>(distanceLower.fst + 1,
						distanceLower.snd);
			} else {
				distanceHigher = new Pair<Integer, Integer>(distanceHigher.fst + 1,
						distanceHigher.snd);
			}
		}

		final int totalGroupCount = targetProtein.fst + distanceLower.fst
				+ distanceHigher.fst;
		final int totalProteinCount = targetProtein.snd + distanceLower.snd
				+ distanceHigher.snd;

		pw.println();
		pw.println("TotalGroup\t" + totalGroupCount);
		pw.println("TargetGroup\t" + targetProtein.fst);
		pw.println("LowerGroup\t" + distanceLower.fst);
		pw.println("HighGroup\t" + distanceHigher.fst);
		pw.println("TotalProtein\t" + totalProteinCount);
		pw.println("TargetProtein\t" + targetProtein.snd);
		pw.println("LowerProtein\t" + distanceLower.snd);
		pw.println("HighProtein\t" + distanceHigher.snd);
		pw.close();
	}

	private List<String> getTargetAccessNumbers(
			final List<Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>>> distanceList) {
		List<String> result = null;
		for (int i = 0; i < distanceList.size(); i++) {
			final Pair<Integer, List<Pair<IIdentifiedProtein, Map<String, Double>>>> pair = distanceList
					.get(i);
			final List<Pair<IIdentifiedProtein, Map<String, Double>>> proteinList = pair.snd;

			for (int j = 0; j < proteinList.size(); j++) {
				final Map<String, Double> alignMap = proteinList.get(j).snd;
				if (null == alignMap) {
					continue;
				}

				result = new ArrayList<String>(alignMap.keySet());
				Collections.sort(result);
				break;
			}

			if (null != result) {
				break;
			}
		}
		return result;
	}
}
