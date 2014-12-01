package cn.ac.rcpa.bio.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.biojava.bio.proteomics.Protease;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.aminoacid.Aminoacids;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.PrecursorTolerance;
import cn.ac.rcpa.bio.utils.ProteaseRenderer;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class TheoreticalDigestStatisticCalculator implements IFileProcessor {
	public static String version = "1.0.1";

	private Protease protease;

	private int maxMissedCleavages;

	private ISequenceValidator validator;

	private double minMz;

	private double maxMz;

	private ProteaseDigestor digestor;

	private double[] ppmToleranceList;

	private class DigestedPeptide {
		private String sequence;

		private int charge;

		private double mz;

		private double mzTolerance;

		public DigestedPeptide(String sequence, int charge, double mz) {
			this.sequence = sequence;
			this.charge = charge;
			this.mz = mz;
		}

		public int getCharge() {
			return charge;
		}

		public double getMz() {
			return mz;
		}

		public double getMzTolerance() {
			return mzTolerance;
		}

		public String getSequence() {
			return sequence;
		}

		public void setMzTolerance(double mzTolerance) {
			this.mzTolerance = mzTolerance;
		}

		public boolean isMzEquals(DigestedPeptide another) {
			if (this.charge != another.charge) {
				return false;
			}

			return Math.abs(this.mz - another.mz) <= this.mzTolerance;
		}
	}

	public TheoreticalDigestStatisticCalculator(Protease protease,
			int maxMissedCleavages, double minMz, double maxMz,
			double[] ppmToleranceList) {
		this.protease = protease;
		this.maxMissedCleavages = maxMissedCleavages;
		this.minMz = minMz;
		this.maxMz = maxMz;
		this.validator = SequenceValidatorFactory.getMWValidator(minMz - 1.0,
				maxMz * 3 - 2.0, true);
		this.digestor = new ProteaseDigestor(protease, maxMissedCleavages,
				validator);
		this.ppmToleranceList = ppmToleranceList;
	}

	public List<String> process(String databaseFile) throws Exception {
		Set<String> totalPeptides = new HashSet<String>();
		SequenceIterator seqi = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(databaseFile)));
		int icount = 0;

		HashMap<String, List<DigestedPeptide>> proteins = new HashMap<String, List<DigestedPeptide>>();
		while (seqi.hasNext()) {
			icount++;
			if (icount % 1000 == 0) {
				System.out.println("Digesting " + icount);
			}
			Sequence seq = seqi.nextSequence();
			Set<String> peptides = new HashSet<String>(digestor.digest(seq));

			List<DigestedPeptide> dpList = new ArrayList<DigestedPeptide>();
			proteins.put(seq.getName(), dpList);
			for (String peptide : peptides) {
				double mass = Aminoacids.getStableInstance().getMonoResiduesMass(
						peptide);
				for (int charge = 1; charge <= 3; charge++) {
					double mz = mass / charge;
					if (mz >= minMz && mz <= maxMz) {
						dpList.add(new DigestedPeptide(peptide, charge, mz));
						break;
					}
				}
			}
			totalPeptides.addAll(peptides);
		}
		seqi = null;
		System.out.println("\nDigested " + icount + " sequences and generated "
				+ totalPeptides.size() + " peptides!");

		ArrayList<String> result = new ArrayList<String>();
		for (double ppmTolerance : ppmToleranceList) {
			final String resultFile = databaseFile + "." + ppmTolerance + ".stat";
			result.add(resultFile);
			PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
			try {
				pw.println("Version=" + version);
				pw.println("Protease=" + ProteaseRenderer.getProteaseCaption(protease));
				pw.println("MinMz=" + minMz);
				pw.println("MaxMz=" + maxMz);
				pw.println("MaxMissedCleavages=" + maxMissedCleavages);
				pw.println("ppmTolerance=" + ppmTolerance);
				pw.println();

				List<String> proteinNames = new ArrayList<String>(proteins.keySet());
				for (int i = 0; i < 100; i++) {
					int index = (int) (Math.random() * proteins.size());
					if (index == proteins.size()) {
						index = proteins.size() - 1;
					}
					String randomProtein = proteinNames.get(index);
					List<DigestedPeptide> proteinPeptides = proteins.get(randomProtein);

					for (DigestedPeptide peptide : proteinPeptides) {
						peptide.setMzTolerance(PrecursorTolerance.ppm2mz(peptide.mz,
								ppmTolerance));
					}

					HashMap<Integer, Integer> countMap = new HashMap<Integer, Integer>();
					for (int proIndex = 0; proIndex < proteinNames.size(); proIndex++) {
						if (proIndex == index) {
							continue;
						}

						int matchedCount = 0;
						List<DigestedPeptide> curProPeptides = proteins.get(proteinNames
								.get(proIndex));

						for (DigestedPeptide peptide : proteinPeptides) {
							for (DigestedPeptide curProPep : curProPeptides) {
								if (peptide.isMzEquals(curProPep)) {
									matchedCount++;
								}
							}
						}

						if (matchedCount > 0) {
							if (!countMap.containsKey(matchedCount)) {
								countMap.put(matchedCount, 1);
							} else {
								countMap.put(matchedCount, countMap.get(matchedCount) + 1);
							}
						}
					}

					List<Integer> counts = new ArrayList<Integer>(countMap.keySet());
					Collections.sort(counts);
					pw.println(randomProtein + "\t" + proteinPeptides.size());
					for (Integer count : counts) {
						pw.println(count + "\t" + countMap.get(count));
					}
				}

				pw.println();
			} finally {
				pw.close();
			}
		}
		return result;
	}
}
