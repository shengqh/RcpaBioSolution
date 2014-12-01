package cn.ac.rcpa.bio.tools.statistic;

public class ProteinFalseDiscoveryRateItem {
	private String name;

	private int decoy;

	private int target;

	private double fdr;

	public int getDecoy() {
		return decoy;
	}

	public void setDecoy(int decoy) {
		this.decoy = decoy;
	}

	public double getFdr() {
		return fdr;
	}

	public void setFdr(double fdr) {
		this.fdr = fdr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}
}
