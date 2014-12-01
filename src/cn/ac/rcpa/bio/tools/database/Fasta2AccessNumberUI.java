package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class Fasta2AccessNumberUI extends AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Extract Protein Access Number From Fasta File";

	public Fasta2AccessNumberUI() {
		super(Constants.getSQHTitle(title, Fasta2AccessNumber.version),
				new OpenFileArgument("Fasta", "fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new Fasta2AccessNumber(getDatabaseType());
	}

	public static void main(String[] args) {
		new Fasta2AccessNumberUI().showSelf();
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
			Fasta2AccessNumberUI.main(new String[0]);
		}

		public String getVersion() {
			return Fasta2AccessNumber.version;
		}
	}

}
