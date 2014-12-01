package cn.ac.rcpa.bio.tools.statistic;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedPeptideAminoacidStatisticCalculatorUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Identified Peptide Aminoacid Statistic Calculator";

	private static String version = "1.1.0";

	public IdentifiedPeptideAminoacidStatisticCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"Peptides", "peptides"), "Aminoacids");
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedPeptideAminoacidStatisticCalculator(getArgument());
	}

	public static void main(String[] args) {
		new IdentifiedPeptideAminoacidStatisticCalculatorUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Statistic };
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
