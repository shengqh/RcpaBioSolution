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

public class ReversedDatabaseOnlyBuilder implements IFileProcessor {
	public static String version = "1.0.1";

	public ReversedDatabaseOnlyBuilder() {
	}

	public List<String> process(String dbName) throws Exception {
		final String resultFile = RcpaFileUtils.changeExtension(dbName,
				"REVERSED_ONLY.fasta");
		SequenceIterator seqI = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(dbName)));
		FileOutputStream fs = new FileOutputStream(resultFile);
		int iCount = 1;

		ReversedDatabaseBuilder builder = new ReversedDatabaseBuilder(false);
		while (seqI.hasNext()) {
			Sequence seq = seqI.nextSequence();
			Sequence reversedSeq = builder.getReversedSeq(seq, iCount);
			SequenceUtils.writeFasta(fs, reversedSeq);
			iCount++;
		}

		fs.close();
		return Arrays.asList(new String[] { resultFile });
	}
}
