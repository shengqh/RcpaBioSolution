package cn.ac.rcpa.bio.tools.go;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.DirectoryArgument;

public class GOAnnotationSpecialCategoryIdentifierUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private final static String title = "GO Annotation - Find Over Represented Category";

	private final static String version = "1.0.0";

	public GOAnnotationSpecialCategoryIdentifierUI() {
		super(Constants.getSQHTitle(title, version), new DirectoryArgument(
				"GOAnnotation"), new DirectoryArgument("Database GOAnnotation"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new GOAnnotationSpecialCategoryIdentifier(getArgument());
	}

	public static void main(String[] args) {
		new GOAnnotationSpecialCategoryIdentifierUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation, "GO Annotation Analysis" };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
