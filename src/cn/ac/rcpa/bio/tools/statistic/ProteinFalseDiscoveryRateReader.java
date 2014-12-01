package cn.ac.rcpa.bio.tools.statistic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProteinFalseDiscoveryRateReader {
	public static Map<String, ProteinFalseDiscoveryRateItem> readMap(
			String filename) throws Exception {
		Map<String, ProteinFalseDiscoveryRateItem> result = new LinkedHashMap<String, ProteinFalseDiscoveryRateItem>();

		List<ProteinFalseDiscoveryRateItem> itemList = readList(filename);
		for (ProteinFalseDiscoveryRateItem item : itemList) {
			result.put(item.getName(), item);
		}

		return result;
	}

	public static List<ProteinFalseDiscoveryRateItem> readList(String filename)
			throws Exception {
		List<ProteinFalseDiscoveryRateItem> result = new ArrayList<ProteinFalseDiscoveryRateItem>();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = "";
			br.readLine();
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}

				String[] parts = line.split("\t");
				ProteinFalseDiscoveryRateItem item = new ProteinFalseDiscoveryRateItem();
				item.setName(parts[0]);
				item.setDecoy(Integer.parseInt(parts[1]));
				item.setTarget(Integer.parseInt(parts[2]));
				item.setFdr(Double.parseDouble(parts[3].substring(0,
						parts[3].length() - 1)));

				result.add(item);
			}
		} finally {
			br.close();
		}

		return result;
	}
}
