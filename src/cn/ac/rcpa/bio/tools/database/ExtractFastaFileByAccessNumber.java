package cn.ac.rcpa.bio.tools.database;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.AccessNumberUtils;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ExtractFastaFileByAccessNumber implements IFileProcessor {
	public static final String version = "1.0.2";

	private List<Sequence> sequences;

	private boolean bSkipFirstLine;

	public ExtractFastaFileByAccessNumber(String databaseFile,
			boolean bSkipFirstLine) {
		try {
			sequences = SequenceUtils.readFastaProteins(new File(databaseFile));
		} catch (Exception ex) {
			throw new IllegalStateException(ex.getMessage());
		}
		this.bSkipFirstLine = bSkipFirstLine;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println(ExtractFastaFileByAccessNumber.class.getName()
					+ " databaseFile acNumberFile");
			return;
		}

		final List<String> resultFiles = new ExtractFastaFileByAccessNumber(
				args[0], false).process(args[1]);
		System.out.println("Result saved to " + resultFiles);
	}

	public List<String> process(String tokenFile) throws Exception {
		String[] proteinIds = AccessNumberUtils.loadFromFile(tokenFile,
				bSkipFirstLine);
		System.out.println(proteinIds.length + " proteins in list");

		List<String> missedProteinIds = new ArrayList<String>();

		final File resultFastaFile = new File(tokenFile + ".fasta");
		FileOutputStream os = new FileOutputStream(resultFastaFile);
		try {
			for (String proteinId : proteinIds) {
				boolean bFound = false;
				for (Sequence seq : sequences) {
					if (seq.getName().indexOf(proteinId) != -1) {
						SequenceUtils.writeFasta(os, seq);
						bFound = true;
						break;
					}
				}

				if (!bFound) {
					missedProteinIds.add(proteinId);
				}
			}
		} finally {
			os.close();
		}
		final List<String> result = new ArrayList<String>();

		result.add(resultFastaFile.getAbsolutePath());

		File missedFile = new File(tokenFile + ".miss");
		if (missedProteinIds.size() > 0) {
			RcpaFileUtils.writeFile(missedFile.getAbsolutePath(), missedProteinIds
					.toArray(new String[0]));
			result.add(missedFile.getAbsolutePath());
		} else {
			if (missedFile.exists()) {
				missedFile.delete();
			}
		}

		return result;
	}
}
