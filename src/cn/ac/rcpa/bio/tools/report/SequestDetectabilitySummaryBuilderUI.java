package cn.ac.rcpa.bio.tools.report;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaDoubleField;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class SequestDetectabilitySummaryBuilderUI extends
		AbstractFileProcessorUI {

	final static String title = "Sequest Detectability Summary Builder";

	private JRcpaDoubleField txtPvalue = new JRcpaDoubleField(
			"FalseDiscoverRate", "False Discover Rate", 0.01, true);

	private JRcpaFileField txtDetectabilityProgram = new JRcpaFileField(
			"PeptideDetectabilityProgram", new OpenFileArgument(
					"Peptide Detectability Program", "exe"), true);

	public SequestDetectabilitySummaryBuilderUI() {
		super(Constants.getSQHTitle(title,
				SequestDetectabilitySummaryBuilder.version), new OpenFileArgument(
				"BuildSummary Protein", "noredundant"));
		this.addComponent(txtPvalue);
		this.addComponent(txtDetectabilityProgram);
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new SequestDetectabilitySummaryBuilder(txtDetectabilityProgram
				.getFilename(), txtPvalue.getValue());
	}

	public static void main(String[] argv) {
		new SequestDetectabilitySummaryBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] {CommandType.Report};
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			SequestDetectabilitySummaryBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return SequestDetectabilitySummaryBuilder.version;
		}
	}

}
