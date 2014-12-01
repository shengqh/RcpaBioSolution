package cn.ac.rcpa.bio.tools.other;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class MergePeptideBuilderUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static String title = "Merge Peptide Builder";

	public MergePeptideBuilderUI() {
		super(Constants.getSQHTitle(title, MergePeptideBuilder.version),
				new OpenFileArgument("First Peptides", "peptides"),
				new OpenFileArgument("Second Peptides", "peptides"));
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new MergePeptideBuilder(getArgument());
	}

	public static void main(String[] args) {
		new MergePeptideBuilderUI().showSelf();
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
			MergePeptideBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return MergePeptideBuilder.version;
		}
	}
}
