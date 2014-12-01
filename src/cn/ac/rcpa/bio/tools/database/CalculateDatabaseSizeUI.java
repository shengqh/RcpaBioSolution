package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class CalculateDatabaseSizeUI extends AbstractFileProcessorUI {
	private final static String title = "Calculate Database Size";

	public CalculateDatabaseSizeUI() {
		super(Constants.getSQHTitle(title, CalculateDatabaseSize.version),
				new OpenFileArgument("Database", "fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new CalculateDatabaseSize();
	}

	public static void main(String[] args) {
		new CalculateDatabaseSizeUI().showSelf();
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
			CalculateDatabaseSizeUI.main(new String[0]);
		}

		public String getVersion() {
			return CalculateDatabaseSize.version;
		}
	}

}
