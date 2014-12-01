package cn.ac.rcpa.bio.tools.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;

public class CalculateDatabaseSize implements IFileProcessor {

	public static String version = "1.0.0";

	public List<String> process(String fastaFile) throws Exception {
		int result = 0;
		BufferedReader br = new BufferedReader(new FileReader(fastaFile));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(">")) {
					result++;
				}
			}
		} finally {
			br.close();
		}

		return Arrays.asList(new String[] { "There are " + result
				+ " sequences in database " + new File(fastaFile).getName() });
	}

}
