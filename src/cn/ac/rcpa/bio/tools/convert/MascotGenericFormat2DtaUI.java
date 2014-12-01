package cn.ac.rcpa.bio.tools.convert;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class MascotGenericFormat2DtaUI extends AbstractFileProcessorUI {
	private static String title = "Mascot General Format to DTA";

	public MascotGenericFormat2DtaUI() {
		super(Constants.getSQHTitle(title, MascotGenericFormat2Dta.version),
				new OpenFileArgument("Mascot General Format", "mgf"));
	}

	public static void main(String[] args) {
		new MascotGenericFormat2DtaUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new MascotGenericFormat2Dta();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Format,"PeakList Format" };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			MascotGenericFormat2DtaUI.main(new String[0]);
		}

		public String getVersion() {
			return MascotGenericFormat2Dta.version;
		}
	}

}
