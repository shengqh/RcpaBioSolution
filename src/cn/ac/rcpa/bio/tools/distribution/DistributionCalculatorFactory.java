package cn.ac.rcpa.bio.tools.distribution;

import cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType;

public class DistributionCalculatorFactory {
	private DistributionCalculatorFactory() {
	}

	public static AbstractDistributionCalculator getProteinDistributionCalculator(
			ClassificationType cType, boolean exportIndividual) {
		if (cType == ClassificationType.PI) {
			return new ProteinPIDistributionCalculator(exportIndividual);
		} else if (cType == ClassificationType.MW) {
			return new ProteinMWDistributionCalculator(exportIndividual);
		}

		return new ProteinOtherDistributionCalculator(exportIndividual);
	}

	public static AbstractDistributionCalculator getPeptideDistributionCalculator(
			ClassificationType cType, boolean exportIndividual,
			boolean modifiedPeptideOnly, String modifiedAminoacid) {
		IValueCalculator calc;
		if (cType == ClassificationType.MW) {
			calc = new ValueMWCalculator();
		} else {
			calc = new ValuePICalculator();
		}

		if (modifiedPeptideOnly) {
			return new ModifiedPeptideDistributionCalculator(calc, exportIndividual,
					modifiedAminoacid);
		} else {
			return new PeptideDistributionCalculator(calc, exportIndividual);
		}
	}
}
