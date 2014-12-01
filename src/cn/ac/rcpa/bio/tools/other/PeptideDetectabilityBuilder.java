package cn.ac.rcpa.bio.tools.other;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.detectability.DetectabilityEntry;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.ShellUtils;

public class PeptideDetectabilityBuilder implements IFileProcessor {
	public static String version = "1.0.1";
	private String detectabilityProgram;

	public PeptideDetectabilityBuilder(String detectabilityProgram) {
		super();
		this.detectabilityProgram = detectabilityProgram;
	}

	public List<String> process(String fastaFilename) throws Exception {
		List<Sequence> proteins = SequenceUtils.readFastaProteins(fastaFilename);

		if (proteins.size() == 0) {
			throw new IllegalArgumentException(
					"There is no protein sequence in file " + fastaFilename);
		}

		Map<String, String> name2mpMap = new HashMap<String, String>();
		Map<String, String> mp2nameMap = new HashMap<String, String>();
		for (int i = 0; i < proteins.size(); i++) {
			Sequence mp = proteins.get(i);
			String proteinName = "protein"
					+ StringUtils.leftPad(Integer.toString(i), 8, '0');
			name2mpMap.put(proteinName, mp.getName());
			mp2nameMap.put(mp.getName(), proteinName);
		}

		String targetDetectabilityDir = fastaFilename + ".detectability";

		String proteinFile = targetDetectabilityDir + "\\proteins.fasta";
		new File(targetDetectabilityDir).mkdir();
		PrintWriter pwDetectabilityFasta = new PrintWriter(proteinFile);
		try {
			for (int i = 0; i < proteins.size(); i++) {
				Sequence mp = proteins.get(i);
				String proteinName = mp2nameMap.get(mp.getName());
				pwDetectabilityFasta.println(">" + proteinName);
				pwDetectabilityFasta.println(mp.seqString());
			}
		} finally {
			pwDetectabilityFasta.close();
		}

		ShellUtils.execute(
				new String[] { detectabilityProgram, "-F", proteinFile }, new File(
						targetDetectabilityDir),true);
		List<DetectabilityEntry> deList = DetectabilityEntry
				.readDetectabilityEntryList(targetDetectabilityDir);

		for (DetectabilityEntry de : deList) {
			String realProteinName = name2mpMap.get(de.getProtein());
			de.setProtein(realProteinName);
		}

		String resultFile = fastaFilename + ".pepdet";
		DetectabilityEntry.writeToFile(resultFile, deList);

		return Arrays.asList(new String[] { resultFile });
	}
}
