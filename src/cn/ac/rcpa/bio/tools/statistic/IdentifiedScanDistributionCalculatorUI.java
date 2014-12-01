package cn.ac.rcpa.bio.tools.statistic;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedScanDistributionCalculatorUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static String title = "Identified Scan Distribution Calculator";

	private static final String version = "1.0.1";

	public IdentifiedScanDistributionCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"BuildSummary Peptides", "peptides"), new OpenFileArgument(
				"MS Scan Index", "msLevel"));
	}

	public static void main(String[] args) {
		new IdentifiedScanDistributionCalculatorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedScanDistributionCalculator(getArgument());
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
