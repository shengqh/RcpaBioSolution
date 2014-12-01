package cn.ac.rcpa.bio.tools;

import cn.ac.rcpa.bio.tools.impl.SequenceLengthValidator;
import cn.ac.rcpa.bio.tools.impl.SequenceMWValidator;

public class SequenceValidatorFactory {
	private SequenceValidatorFactory() {
	}

	public static ISequenceValidator getLengthValidator(int minLength,
			int maxLength) {
		return new SequenceLengthValidator(minLength, maxLength);
	}

	public static ISequenceValidator getMWValidator(double minMW, double maxMW,
			boolean isMonoisotopic) {
		return new SequenceMWValidator(minMW, maxMW, isMonoisotopic);
	}

}
