package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ExtractFastaFileByAccessNumberUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static final String title = "Extract Fasta File By Protein Access Number";

	public ExtractFastaFileByAccessNumberUI() {
		super(Constants.getSQHTitle(title, ExtractFastaFileByAccessNumber.version),
				new OpenFileArgument("Protein Access Number", "proteinid"),
				new OpenFileArgument("Database", "fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ExtractFastaFileByAccessNumber(getArgument(), false);
	}

	public static void main(String[] args) {
		new ExtractFastaFileByAccessNumberUI().showSelf();
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
			ExtractFastaFileByAccessNumberUI.main(new String[0]);
		}

		public String getVersion() {
			return ExtractFastaFileByAccessNumber.version;
		}
	}
}
