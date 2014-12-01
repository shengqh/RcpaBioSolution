package cn.ac.rcpa.bio.tools.annotation;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class DBAnnotationFromFastaFileUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Get Annotation From Database By Protein Fasta File";

	public DBAnnotationFromFastaFileUI() {
		super(Constants.getSQHTitle(title, DBAnnotationFromFastaFile.version),
				new OpenFileArgument("Fasta", "fasta"));
	}

	public static void main(String[] args) {
		new DBAnnotationFromFastaFileUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new DBAnnotationFromFastaFile(getDatabaseType());
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation };
		}

		public String getCaption() {
			return "Database Annotation By Fasta File";
		}

		public void run() {
			DBAnnotationFromFastaFileUI.main(new String[0]);
		}

		public String getVersion() {
			return DBAnnotationFromFastaFile.version;
		}
	}

}
