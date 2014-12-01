package cn.ac.rcpa.bio.tools.filter;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.SequenceValidateException;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public abstract class AbstractProteinFastaFilter implements IFileProcessor {
	private String extension;

	public AbstractProteinFastaFilter(String extension) {
		this.extension = extension;
	}

	public List<String> process(String originFile) throws Exception {
		SequenceIterator seqi = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(originFile)));
		String resultFile = originFile + "." + extension;
		FileOutputStream fos = new FileOutputStream(resultFile);
		try {
			while (seqi.hasNext()) {
				Sequence seq = seqi.nextSequence();
				if (isValid(seq)) {
					SequenceUtils.writeFasta(fos, seq);
				}
			}
		} finally {
			fos.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

	protected abstract boolean isValid(Sequence seq)
			throws SequenceValidateException;
}
