package cn.ac.rcpa.bio.tools.statistic;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class PeptideFDRCalculatorUI extends AbstractFileProcessorUI {
	private static final String title = "Peptide False Discovery Rate Calculator";

	private static final String version = "1.0.0";

	private static String[] formulas = new String[] {
			"Num(Decoy) * 2 / [Num(Decoy) + Num(Target)]", "Num(Decoy) / Num(Target)" };

	private JRcpaComboBox<String> calculator;

	private JRcpaTextField txtDecoyDbPattern = new JRcpaTextField("decoyPattern",
			"Decoy Database Pattern", "^REVERSE", true);

	public PeptideFDRCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"Identified Peptide", "peptides"));
		this.calculator = new JRcpaComboBox<String>("calculator", "FDR Formula",
				formulas, formulas[0]);
		this.addComponent(calculator);
		this.addComponent(txtDecoyDbPattern);
	}

	public static void main(String[] args) {
		new PeptideFDRCalculatorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		if (this.calculator.getSelectedItem().equals(formulas[0])) {
			return new PeptideFDRCalculator(new PeptideFDRGygiCalculator(
					txtDecoyDbPattern.getText()));
		} else {
			return new PeptideFDRCalculator(new PeptideFDRSqhCalculator(
					txtDecoyDbPattern.getText()));
		}
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
