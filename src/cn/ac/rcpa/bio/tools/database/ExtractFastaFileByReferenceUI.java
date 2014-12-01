package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ExtractFastaFileByReferenceUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static String title = "Extract Fasta File By Protein Reference";

	public ExtractFastaFileByReferenceUI() {
		super(Constants.getSQHTitle(title, ExtractFastaFileByReference.version),
				new OpenFileArgument("Protein Reference", "references"),
				new OpenFileArgument("Database", "fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ExtractFastaFileByReference(getArgument());
	}

	public static void main(String[] args) {
		new ExtractFastaFileByReferenceUI().showSelf();
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
			ExtractFastaFileByReferenceUI.main(new String[0]);
		}

		public String getVersion() {
			return ExtractFastaFileByReference.version;
		}
	}

}
