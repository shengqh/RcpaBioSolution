package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IPIAccessNumber2SwissProtUI extends AbstractFileProcessorUI {
	private static String title = "Transfer IPI Access Number To SwissProt";

	public IPIAccessNumber2SwissProtUI() {
		super(Constants.getSQHTitle(title, IPIAccessNumber2SwissProt.version),
				new OpenFileArgument("IPI Access Number", "ipiNumber"));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IPIAccessNumber2SwissProt();
	}

	public static void main(String[] args) {
		new IPIAccessNumber2SwissProtUI().showSelf();
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
			IPIAccessNumber2SwissProtUI.main(new String[0]);
		}

		public String getVersion() {
			return IPIAccessNumber2SwissProt.version;
		}
	}

}
