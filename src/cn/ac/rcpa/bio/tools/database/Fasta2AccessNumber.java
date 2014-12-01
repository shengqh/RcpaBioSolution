package cn.ac.rcpa.bio.tools.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class Fasta2AccessNumber implements IFileProcessor {
	public static final String version = "1.0.1";

	private IAccessNumberParser acParser;

	private SequenceDatabaseType dbType;

	public Fasta2AccessNumber(SequenceDatabaseType dbType) {
		this.dbType = dbType;
		this.acParser = AccessNumberParserFactory.getParser(dbType);
	}

	public List<String> process(String originFile) throws Exception {
		final SequenceIterator seqI = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(originFile)));
		String suffix;
		if (dbType == SequenceDatabaseType.IPI) {
			suffix = ".ipiNumber";
		} else if (dbType == SequenceDatabaseType.SWISSPROT) {
			suffix = ".spNumber";
		} else if (dbType == SequenceDatabaseType.NR) {
			suffix = ".nrNumber";
		} else {
			suffix = ".acNumber";
		}

		final String resultFile = RcpaFileUtils.changeExtension(originFile, suffix);
		final PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
		try {
			while (seqI.hasNext()) {
				final Sequence seq = seqI.nextSequence();
				pw.println(acParser.getValue(seq.getName()));
			}
		} finally {
			pw.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}
}
