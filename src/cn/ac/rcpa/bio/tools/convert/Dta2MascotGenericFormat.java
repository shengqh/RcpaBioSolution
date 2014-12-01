package cn.ac.rcpa.bio.tools.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.mascot.MascotGenericFormatWriter;
import cn.ac.rcpa.bio.proteomics.sequest.DtaDirectoryIterator;

public class Dta2MascotGenericFormat implements IFileProcessor {

	public static String version = "1.0.1";

	public List<String> process(String dtaDirectory) throws Exception {
		DtaDirectoryIterator ddi = new DtaDirectoryIterator(dtaDirectory);
		MascotGenericFormatWriter mgfw = new MascotGenericFormatWriter();

		File dir = new File(dtaDirectory);
		File resultFile = new File(dir, dir.getName() + ".mgf");
		PrintStream ps = new PrintStream(new FileOutputStream(resultFile));
		try {
			while (ddi.hasNext()) {
				PeakList<Peak> pl = ddi.next();
				mgfw.write(ps, pl);
			}
		} finally {
			ps.close();
		}

		return Arrays.asList(new String[] { resultFile.getAbsolutePath() });
	}

	public static void main(String[] args) throws Exception {
		new Dta2MascotGenericFormat()
				.process("F:\\sqh\\Program\\java\\workspace\\Collabration\\data\\dta");
	}

}
