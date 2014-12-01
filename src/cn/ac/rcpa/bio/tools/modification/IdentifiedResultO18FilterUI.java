package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedResultO18FilterUI extends AbstractFileProcessorUI {
	private static String title = "IdentifiedResult O18 Modification Filter";

	public IdentifiedResultO18FilterUI() {
		super(Constants.getSQHTitle(title, IdentifiedResultO18Filter.version),
				new OpenFileArgument("Noredundant", "noredundant"));
	}

	public static void main(String[] args) {
		new IdentifiedResultO18FilterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return IdentifiedResultO18Filter.getInstance();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Modification };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			IdentifiedResultO18FilterUI.main(new String[0]);
		}

		public String getVersion() {
			return IdentifiedResultO18Filter.version;
		}
	}

}
