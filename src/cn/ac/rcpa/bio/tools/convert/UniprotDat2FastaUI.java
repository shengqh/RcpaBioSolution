package cn.ac.rcpa.bio.tools.convert;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class UniprotDat2FastaUI extends AbstractFileProcessorUI {
	private static String title = "Fasta To Alignment Compatible Format";

	public UniprotDat2FastaUI() {
		super(Constants.getSQHTitle(title, UniprotDat2Fasta.version),
				new OpenFileArgument("Fasta", "fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new UniprotDat2Fasta();
	}

	public static void main(String[] args) {
		new UniprotDat2FastaUI().showSelf();
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
			UniprotDat2FastaUI.main(new String[0]);
		}

		public String getVersion() {
			return UniprotDat2Fasta.version;
		}
	}

}
