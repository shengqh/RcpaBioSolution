package cn.ac.rcpa.bio.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.biojava.bio.proteomics.Protease;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.MassCalculator;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.chem.Atom;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.Pair;

public class TruncatedDigestBuilder implements IFileProcessor {
	public static String version = "1.0.1";

	private MassCalculator mc;

	private IFilter<String> validator;

	private ProteaseDigestor digestor;

	private IAccessNumberParser acParser;

	public TruncatedDigestBuilder(SequenceDatabaseType dbType, MassCalculator mc,
			Protease protease, int maxMissedCleavages, IFilter<String> validator) {
		this.mc = mc;
		this.validator = validator;
		this.digestor = new ProteaseDigestor(protease, maxMissedCleavages,
				validator);
		this.acParser = AccessNumberParserFactory.getParser(dbType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.rcpa.bio.processor.IFileProcessor#process(java.lang.String)
	 */
	public List<String> process(String originFile) throws Exception {
		final String peptideFile = originFile + ".truncated_digest";
		PrintWriter pw = new PrintWriter(new FileWriter(peptideFile));
		try {
			SequenceIterator seqi = SequenceUtils.readFastaProtein(new BufferedReader(
					new FileReader(originFile)));

			DecimalFormat df = new DecimalFormat("0.0000");

			int icount = 0;
			while (seqi.hasNext()) {
				icount++;
				if (icount % 1000 == 0) {
					System.out.println("Digesting " + icount);
				}
				Sequence seq = seqi.nextSequence();
				String ac = acParser.getValue(seq.getName());

				HashSet<String> peptides = new HashSet<String>(digestor.digest(seq));
				List<Pair<String, Double>> pepMass = new ArrayList<Pair<String, Double>>();
				for (String pep : peptides) {
					pepMass.add(new Pair<String, Double>(pep, mc.getMass(pep)
							+ Atom.H.getMono_isotopic().getMass()));
				}

				Collections.sort(pepMass, new Comparator<Pair<String, Double>>() {
					public int compare(Pair<String, Double> arg0,
							Pair<String, Double> arg1) {
						return arg0.snd.compareTo(arg1.snd);
					}
				});

				for (Pair<String, Double> pm : pepMass) {
					pw.println(ac + "\t" + pm.fst + "\t" + pm.fst + "\t"
							+ df.format(pm.snd));
					for (int i = 1; i < pm.fst.length(); i++) {
						String truncatedPep = pm.fst.substring(i);
						if (validator.accept(truncatedPep)) {
							pw.println(ac
									+ "\t"
									+ pm.fst
									+ "\t"
									+ truncatedPep
									+ "\t"
									+ df.format(mc.getMass(truncatedPep)
											+ Atom.H.getMono_isotopic().getMass()));
						}
					}

					for (int i = pm.fst.length() - 2; i >= 1; i--) {
						String truncatedPep = pm.fst.substring(0, i);
						if (validator.accept(truncatedPep)) {
							pw.println(ac
									+ "\t"
									+ pm.fst
									+ "\t"
									+ truncatedPep
									+ "\t"
									+ df.format(mc.getMass(truncatedPep)
											+ Atom.H.getMono_isotopic().getMass()));
						}
					}
				}
			}
			seqi = null;
			System.out.println("\nDigested " + icount + " sequences!");
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { peptideFile });
	}

}
