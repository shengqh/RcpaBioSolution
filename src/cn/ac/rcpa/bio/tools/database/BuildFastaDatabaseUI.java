package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.SaveFileArgument;

public class BuildFastaDatabaseUI extends AbstractFileProcessorWithArgumentUI {
	private final static String title = "Build Fasta Database by Taxonomy";

	public BuildFastaDatabaseUI() {
		super(Constants.getSQHTitle(title, BuildFastaDatabase.version),
				new SaveFileArgument("Database", "fasta"), "Taxonomy IDs");
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new BuildFastaDatabase(getArgument());
	}

	public static void main(String[] args) {
		new BuildFastaDatabaseUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Database };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			BuildFastaDatabaseUI.main(new String[0]);
		}

		public String getVersion() {
			return BuildFastaDatabase.version;
		}
	}

}
