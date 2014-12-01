package cn.ac.rcpa.bio.tools.convert;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class FastaToAligmentCompatibleConverterUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Fasta To Alignment Compatible Format";

	public FastaToAligmentCompatibleConverterUI() {
		super(Constants.getSQHTitle(title,
				FastaToAligmentCompatibleConverter.version), new OpenFileArgument(
				"Fasta", "fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new FastaToAligmentCompatibleConverter(getDatabaseType());
	}

	public static void main(String[] args) {
		new FastaToAligmentCompatibleConverterUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Format };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			FastaToAligmentCompatibleConverterUI.main(new String[0]);
		}

		public String getVersion() {
			return FastaToAligmentCompatibleConverter.version;
		}
	}

}
