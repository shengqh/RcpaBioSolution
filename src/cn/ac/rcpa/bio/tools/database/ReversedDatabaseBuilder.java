package cn.ac.rcpa.bio.tools.database;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojava.bio.symbol.IllegalSymbolException;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ReversedDatabaseBuilder implements IFileProcessor {
	public static String version = "1.0.3";

	private boolean oldFormat = false;

	public ReversedDatabaseBuilder(boolean oldFormat) {
		super();
		this.oldFormat = oldFormat;
	}

	public Sequence getReversedSeq(Sequence seq, int iCount)
			throws IllegalSymbolException {
		return SequenceUtils.getReversedSeq(seq, iCount, oldFormat ? 0 : 8);
	}

	public List<String> process(String dbName) throws Exception {
		SequenceIterator seqI = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(dbName)));
		final String resultFile = RcpaFileUtils.changeExtension(dbName,
				"REVERSED.fasta");
		FileOutputStream fs = new FileOutputStream(resultFile);
		int iCount = 1;
		while (seqI.hasNext()) {
			Sequence seq = seqI.nextSequence();
			SequenceUtils.writeFasta(fs, seq);
			Sequence reversedSeq = getReversedSeq(seq, iCount);
			SequenceUtils.writeFasta(fs, reversedSeq);
			iCount++;
		}

		fs.close();
		return Arrays.asList(new String[] { resultFile });
	}
}
