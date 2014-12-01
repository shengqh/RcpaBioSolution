package cn.ac.rcpa.bio.tools.database;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ExtractSwissProtFastaFileBySpecies implements IFileProcessor {
	public static final String version = "1.0.1";

	private String speciesName;

	public ExtractSwissProtFastaFileBySpecies(String speciesName) {
		this.speciesName = speciesName;
	}

	public List<String> process(String databaseFile) throws Exception {
		final String resultFastaFile = RcpaFileUtils.changeExtension(databaseFile,
				speciesName + ".fasta");
		FileOutputStream os = new FileOutputStream(resultFastaFile);
		try {
			SequenceIterator seqi = SequenceUtils
					.readFastaProtein(new BufferedReader(new FileReader(databaseFile)));
			while (seqi.hasNext()) {
				Sequence seq = seqi.nextSequence();
				if (seq.getName().endsWith(speciesName)) {
					SequenceUtils.writeFasta(os, seq);
				}
			}
		} finally {
			os.close();
		}

		return Arrays.asList(new String[] { resultFastaFile });
	}

	public static void main(String[] args) throws Exception {
		new ExtractSwissProtFastaFileBySpecies("YEAST")
				.process("C:\\sqh\\database\\uniprot_sprot.fasta");
	}
}
