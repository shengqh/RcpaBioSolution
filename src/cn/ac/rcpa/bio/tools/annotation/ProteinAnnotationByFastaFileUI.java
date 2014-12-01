package cn.ac.rcpa.bio.tools.annotation;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.annotation.AnnotationProcessorFactory;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ProteinAnnotationByFastaFileUI extends AbstractFileProcessorUI {
	private static String title = "Protein MW/PI/Gravy Annotation By Fasta File";

	private static String version = "1.0.2";

	public ProteinAnnotationByFastaFileUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument("Fasta",
				"fasta"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return AnnotationProcessorFactory.getAnnotationProcessor();
	}

	public static void main(String[] args) {
		new ProteinAnnotationByFastaFileUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			ProteinAnnotationByFastaFileUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
