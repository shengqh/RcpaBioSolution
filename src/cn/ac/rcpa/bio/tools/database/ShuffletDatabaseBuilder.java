package cn.ac.rcpa.bio.tools.database;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.ShellUtils;

public class ShuffletDatabaseBuilder implements IFileProcessor {
	private String shuffletProgram;

	private int wordLength;

	private String originDatabase;

	public ShuffletDatabaseBuilder(int wordLength, String originDatabase) {
		this.shuffletProgram = "extends/shufflet.exe";
		this.wordLength = wordLength;
		this.originDatabase = originDatabase;
	}

	public ShuffletDatabaseBuilder(String shuffletProgram, int wordLength,
			String originDatabase) {
		this.shuffletProgram = shuffletProgram;
		this.wordLength = wordLength;
		this.originDatabase = originDatabase;
	}

	public List<String> process(String targetDatabase) throws Exception {
		List<Sequence> seqs = SequenceUtils.readFastaProteins(originDatabase);

		String tmpFile = originDatabase + ".tmp";
		PrintWriter pw = new PrintWriter(tmpFile);
		try {
			int count = 1;
			pw.println(">tmp" + count);
			int length = 0;
			for (Sequence seq : seqs) {
				length += seq.seqString().length();
				if (length > 100000) {
					count++;
					pw.println(">tmp" + count);
					length = seq.seqString().length();
				}
				pw.println(seq.seqString());
			}
		} finally {
			pw.close();
		}

		String[] commands = new String[] { shuffletProgram, "1",
				Integer.toString(wordLength), tmpFile, targetDatabase };
		if (ShellUtils.execute(commands, new File(tmpFile).getParentFile(), true)) {
			List<Sequence> shuffletSeqs = SequenceUtils
					.readFastaProteins(targetDatabase);

			StringBuilder sb = new StringBuilder();
			for (Sequence seq : shuffletSeqs) {
				sb.append(seq.seqString());
			}

			String totalSequence = sb.toString();
			List<Sequence> targetSeqs = new ArrayList<Sequence>();

			int startPos = 0;
			int count = 1;
			for (Sequence seq : seqs) {
				int endPos = startPos + seq.seqString().length();
				String sequence = totalSequence.substring(startPos, endPos);
				String name = "SHUFFLET_"
						+ StringUtils.leftPad(Integer.toString(count), 8, '0');

				targetSeqs.add(ProteinTools.createProteinSequence(sequence, name));
				startPos = endPos;
				count++;
			}

			SequenceUtils.writeFasta(new File(targetDatabase), targetSeqs);
		}
		new File(tmpFile).delete();

		return Arrays.asList(new String[] { targetDatabase });
	}

	public static void main(String[] args) throws Exception {
		new ShuffletDatabaseBuilder(1, "D:\\Database\\19mix.fasta")
				.process("D:\\Database\\19mix_shufflet_11.fasta");
	}

}
