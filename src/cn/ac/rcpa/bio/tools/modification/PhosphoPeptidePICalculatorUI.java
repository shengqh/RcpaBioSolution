package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.modification.PhosphoPeptidePICalculator;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class PhosphoPeptidePICalculatorUI extends AbstractFileProcessorUI {
	public static String title = "Phospho peptide PI calculator";
	public static String version = "1.0.2";

	public PhosphoPeptidePICalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"Phospho Peptide Sequence", "txt"));
	}

	public static void main(String[] args) {
		new PhosphoPeptidePICalculatorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new PhosphoPeptidePICalculator();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Modification };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			PhosphoPeptidePICalculatorUI.main(new String[0]);
		}

		public String getVersion() {
			return PhosphoPeptidePICalculatorUI.version;
		}
	}

}
