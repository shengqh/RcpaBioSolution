package cn.ac.rcpa.bio.tools.database;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class ExtractFastaFileByAccessNumberPattern implements IFileProcessor {
	public static String version = "1.0.0";

	private String resultFilename;

	private Pattern accessNumberPattern;

	public ExtractFastaFileByAccessNumberPattern(String accessNumberPattern,
			String resultFilename) {
		super();
		this.accessNumberPattern = Pattern.compile(accessNumberPattern);
		this.resultFilename = resultFilename;
	}

	public List<String> process(String originFile) throws Exception {
		FileOutputStream fos = new FileOutputStream(resultFilename);
		SequenceIterator seqi = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(originFile)));
		try {
			while (seqi.hasNext()) {
				Sequence seq = seqi.nextSequence();
				// System.out.println(seq.getName());
				if (accessNumberPattern.matcher(seq.getName()).find()) {
					System.out.println(seq.getName());
					SequenceUtils.writeFasta(fos, seq);
				}
			}
		} finally {
			seqi = null;
			fos.close();
		}

		return Arrays.asList(new String[] { resultFilename });
	}

}
