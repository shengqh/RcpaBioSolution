package cn.ac.rcpa.bio.tools.mascot;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class MascotHtml2TextProcessorUI extends AbstractFileProcessorUI {

	final static String title = "Parse Mascot Html Result To Text";

	public MascotHtml2TextProcessorUI() {
		super(Constants.getSQHTitle(title, MascotHtml2TextProcessor.version),
				new OpenFileArgument("Mascot Html Result",
						new String[] { "html", "htm" }));
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new MascotHtml2TextProcessor();
	}

	public static void main(String[] argv) {
		new MascotHtml2TextProcessorUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Other };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			MascotHtml2TextProcessorUI.main(new String[0]);
		}

		public String getVersion() {
			return MascotHtml2TextProcessor.version;
		}
	}

}
