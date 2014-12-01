package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.OpenFileArgument;

public class BuildSummaryPeptideHitAmbigiousDeltaCnFilterUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Filter Ambigious Modification Sites By DeltaCn";

	public BuildSummaryPeptideHitAmbigiousDeltaCnFilterUI() {
		super(Constants.getSQHTitle(title,
				BuildSummaryPeptideHitAmbigiousDeltaCnFilter.version),
				new OpenFileArgument("Peptides", "peptides"),
				"Max Ambigious Modification DeltaCn");
	}

	@Override
	protected IFileProcessor getProcessor() {
		double deltacn;
		try {
			deltacn = Double.parseDouble(getArgument());
		} catch (NumberFormatException ex) {
			throw new IllegalStateException(
					"Error : Input Max Ambigious Modification DeltaCn (double value) First!");
		}
		return new BuildSummaryPeptideHitAmbigiousDeltaCnFilter(deltacn);
	}

	public static void main(String[] args) {
		new BuildSummaryPeptideHitAmbigiousDeltaCnFilterUI().showSelf();
	}

}
