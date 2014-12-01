package cn.ac.rcpa.bio.tools.distribution;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProteinDistributionItem {
	private String[] names;

	private String[] descriptions;

	private Map<String, String> items = new LinkedHashMap<String, String>();

	public String[] getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions.split(" ! ");
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	public Map<String, String> getItems() {
		return items;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names.split(" ! ");
	}

	public void setNames(String[] names) {
		this.names = names;
	}
}
