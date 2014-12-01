package cn.ac.rcpa.bio.tools.impl;

import cn.ac.rcpa.bio.tools.ISequenceValidator;
import cn.ac.rcpa.bio.utils.MassCalculator;

public class SequenceMWValidator implements ISequenceValidator {
	private double minMW;

	private double maxMW;

	private MassCalculator massCalculator;

	public SequenceMWValidator(double minMW, double maxMW,
			boolean isMonoisotopic) {
		this.minMW = minMW;
		this.maxMW = maxMW;
		this.massCalculator = new MassCalculator(isMonoisotopic);
	}

	public SequenceMWValidator(double minMW, double maxMW, MassCalculator mc) {
		this.minMW = minMW;
		this.maxMW = maxMW;
		this.massCalculator = mc;
	}

	public boolean accept(String seq) {
		try {
			double peptideMW = massCalculator.getMass(seq);
			return peptideMW >= minMW && peptideMW <= maxMW;
		} catch (Exception ex) {
			return false;
		}
	}

	public String getType() {
		return "MassWeightRange=" + minMW + "--" + maxMW;
	}
}
