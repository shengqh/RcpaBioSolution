package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ReversedDatabaseOnlyBuilderUI extends AbstractFileProcessorUI {
	private static String title = "Reversed Only Database Builder";

	public ReversedDatabaseOnlyBuilderUI() {
		super(Constants.getSQHTitle(title, ReversedDatabaseOnlyBuilder.version),
				new OpenFileArgument("Fasta", "fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ReversedDatabaseOnlyBuilder();
	}

	public static void main(String[] args) {
		new ReversedDatabaseOnlyBuilderUI().showSelf();
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
			ReversedDatabaseOnlyBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return ReversedDatabaseOnlyBuilder.version;
		}
	}

}
