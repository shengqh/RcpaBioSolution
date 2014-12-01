package cn.ac.rcpa.bio.tools.distribution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ProteinDistributionResultReader {
	public static List<ProteinDistributionItem> read(String filename)
			throws Exception {
		List<ProteinDistributionItem> result = new ArrayList<ProteinDistributionItem>();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = br.readLine();
			String[] parts = line.split("\t");

			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}

				String[] values = line.split("\t");
				ProteinDistributionItem item = new ProteinDistributionItem();
				item.setNames(values[0]);
				item.setDescriptions(values[1]);
				for (int i = 2; i < values.length; i++) {
					item.getItems().put(parts[i], values[i]);
				}

				result.add(item);
			}
		} finally {
			br.close();
		}

		return result;
	}
}
