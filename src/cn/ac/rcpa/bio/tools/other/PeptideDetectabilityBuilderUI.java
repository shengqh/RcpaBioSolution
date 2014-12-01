package cn.ac.rcpa.bio.tools.other;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class PeptideDetectabilityBuilderUI extends AbstractFileProcessorUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -776292041609041923L;

	final static String title = "Peptide Detectability Builder";

	private JRcpaFileField txtDetectabilityProgram = new JRcpaFileField(
			"PeptideDetectabilityProgram", new OpenFileArgument(
					"Peptide Detectability Program", "exe"), true);

	public PeptideDetectabilityBuilderUI() {
		super(Constants.getSQHTitle(title, PeptideDetectabilityBuilder.version),
				new OpenFileArgument("Protein Sequence", "fasta"));
		this.addComponent(txtDetectabilityProgram);
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new PeptideDetectabilityBuilder(txtDetectabilityProgram
				.getFilename());
	}

	public static void main(String[] argv) {
		new PeptideDetectabilityBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Other };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			PeptideDetectabilityBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return PeptideDetectabilityBuilder.version;
		}
	}

}
