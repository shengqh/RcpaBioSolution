package cn.ac.rcpa.bio.tools.distribution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class DistributionComparisonResultReader {
	public static Map<String, Map<String, Integer>> readMap(String filename)
			throws Exception {
		Map<String, Map<String, Integer>> result = new LinkedHashMap<String, Map<String, Integer>>();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = br.readLine();
			String[] parts = line.split("\t");
			for (int i = 1; i < parts.length; i++) {
				result.put(parts[i], new LinkedHashMap<String, Integer>());
			}

			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}

				String[] values = line.split("\t");
				for (int i = 1; i < values.length; i++) {
					result.get(values[0]).put(parts[i], Integer.parseInt(values[i]));
				}
			}
		} finally {
			br.close();
		}

		return result;
	}
}
