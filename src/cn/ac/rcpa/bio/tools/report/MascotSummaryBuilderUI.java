package cn.ac.rcpa.bio.tools.report;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaDoubleField;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.utils.DirectoryArgument;
import cn.ac.rcpa.utils.OpenFileArgument;

public class MascotSummaryBuilderUI extends
		AbstractFileProcessorWithFileArgumentUI {

	final static String title = "Mascot Summary Builder";

	private JRcpaDoubleField txtPvalue = new JRcpaDoubleField("PValue",
			"Max PValue", 0.01, true);

	private JRcpaFileField txtDetectabilityProgram = new JRcpaFileField(
			"PeptideDetectabilityProgram", new OpenFileArgument(
					"Peptide Detectability Program", "exe"), true);

	public MascotSummaryBuilderUI() {
		super(Constants.getSQHTitle(title, MascotSummaryBuilder.version),
				new DirectoryArgument("Mascot Result"), new OpenFileArgument(
						"Database", "fasta"));
		this.addComponent(txtPvalue);
		this.addComponent(txtDetectabilityProgram);
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new MascotSummaryBuilder(txtDetectabilityProgram.getFilename(),
				txtPvalue.getValue(), getArgument(), SequenceDatabaseType.SWISSPROT);
	}

	public static void main(String[] argv) {
		new MascotSummaryBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Report };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			MascotSummaryBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return MascotSummaryBuilder.version;
		}
	}

}
