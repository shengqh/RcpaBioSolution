package cn.ac.rcpa.bio.tools.other;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class SubstractPeptideBuilderUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static String title = "Substract Peptide Builder";

	public SubstractPeptideBuilderUI() {
		super(Constants.getSQHTitle(title, SubstractPeptideBuilder.version),
				new OpenFileArgument("Main Peptides", "peptides"),
				new OpenFileArgument("Substract Peptides", "peptides"));
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new SubstractPeptideBuilder(getArgument());
	}

	public static void main(String[] args) {
		new SubstractPeptideBuilderUI().showSelf();
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
			SubstractPeptideBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return SubstractPeptideBuilder.version;
		}
	}

}
