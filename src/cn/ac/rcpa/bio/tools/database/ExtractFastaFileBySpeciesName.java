package cn.ac.rcpa.bio.tools.database;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojava.bio.seq.io.StreamWriter;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.seq.FastaFormat;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ExtractFastaFileBySpeciesName implements IFileProcessor {
	public static final String version = "1.0.0";

	private String speciesName;

	public ExtractFastaFileBySpeciesName(String speciesName) {
		speciesName = speciesName.trim();
		if (speciesName.startsWith("[")) {
			this.speciesName = speciesName;
		} else {
			this.speciesName = "[" + speciesName;
		}
	}

	public List<String> process(String databaseFile) throws Exception {
		final String resultFile = RcpaFileUtils.changeExtension(databaseFile,
				speciesName.substring(1) + ".fasta");

		FileOutputStream os = new FileOutputStream(resultFile);
		int count = 0;
		try {
			SequenceIterator seqi = SequenceUtils
					.readFastaProtein(new BufferedReader(new FileReader(
							databaseFile)));
			while (seqi.hasNext()) {
				Sequence seq = seqi.nextSequence();
				count++;
				if (count % 10000 == 0) {
					System.out.println(count);
				}

				String despLine = SequenceUtils.getProteinDescriptionLine(seq);
				if (despLine.contains(speciesName)) {
					String[] descriptions = despLine.split("");
					for (String desp : descriptions) {
						if (desp.contains(speciesName)) {
							System.out.println(desp);
							Sequence newSeq = ProteinTools
									.createProteinSequence(seq.seqString(),
											desp);
							SequenceUtils.writeFasta(os, newSeq);
							break;
						}
					}
				}
			}
		} finally {
			os.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}

	public static void main(String[] args) throws Exception {
		new ExtractFastaFileBySpeciesName("Capra hircus").process("X:\\nr");
//		new ExtractFastaFileBySpeciesName("Neurospora crassa").process("X:\\nr");
		
		}
}
