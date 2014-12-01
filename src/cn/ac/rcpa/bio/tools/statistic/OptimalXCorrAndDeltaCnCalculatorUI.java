package cn.ac.rcpa.bio.tools.statistic;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class OptimalXCorrAndDeltaCnCalculatorUI extends AbstractFileProcessorUI {
	private static final String title = "IdentifiedPeptide Correct Percent Calculator (Reversed Database Search Needed)";

	private static final String version = "1.0.1";

	private JRcpaTextField patternField = new JRcpaTextField("pattern",
			"Decoy Database Pattern", "^REVERSE", true);

	public OptimalXCorrAndDeltaCnCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"BuildSummary Peptides", "peptides"));
		addComponent(patternField);
	}

	public static void main(String[] args) {
		new OptimalXCorrAndDeltaCnCalculatorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new OptimalXCorrAndDeltaCnCalculator(new double[] { 0.95, 0.99 },
				patternField.getText());
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.MachineLearning };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
