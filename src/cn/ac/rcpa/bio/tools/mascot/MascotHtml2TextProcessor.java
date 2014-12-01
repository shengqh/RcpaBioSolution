package cn.ac.rcpa.bio.tools.mascot;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotResult;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotResultHtmlParser;
import cn.ac.rcpa.bio.proteomics.results.mascot.MascotResultTextWriter;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class MascotHtml2TextProcessor implements IFileProcessor {

	public static String version = "1.0.1";

	public List<String> process(String originFile) throws Exception {
		MascotResult mr = new MascotResultHtmlParser(true).parseFile(new File(
				originFile));
		
		String resultFile = RcpaFileUtils.changeExtension(originFile, ".txt");
		
		new MascotResultTextWriter().write(resultFile, mr);

		return Arrays.asList(new String[] { resultFile });
	}

}
