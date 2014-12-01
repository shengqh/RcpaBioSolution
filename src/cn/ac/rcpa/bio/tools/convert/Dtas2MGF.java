package cn.ac.rcpa.bio.tools.convert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.mascot.MascotGenericFormatWriter;
import cn.ac.rcpa.bio.proteomics.sequest.DtasIterator;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class Dtas2MGF implements IFileProcessor {

	public static String version = "1.0.0";

	public Dtas2MGF() {
		super();
	}

	public List<String> process(String originFile) throws Exception {
		DtasIterator iter = new DtasIterator(new BufferedReader(new FileReader(
				originFile)));
		MascotGenericFormatWriter writer = new MascotGenericFormatWriter();
		String resultFile = RcpaFileUtils.changeExtension(originFile, ".mgf");
		PrintStream ps = new PrintStream(resultFile);
		try {
			while (iter.hasNext()) {
				PeakList<Peak> pkl = iter.next();
				writer.write(ps, pkl);
			}
		} finally {
			ps.close();
		}

		return Arrays.asList(new String[]{resultFile});
	}

}
