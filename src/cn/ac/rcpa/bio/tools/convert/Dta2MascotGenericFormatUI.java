package cn.ac.rcpa.bio.tools.convert;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.DirectoryArgument;

public class Dta2MascotGenericFormatUI extends AbstractFileProcessorUI {
	private static String title = "Dta to Mascot General Format";

	public Dta2MascotGenericFormatUI() {
		super(Constants.getSQHTitle(title, Dta2MascotGenericFormat.version),
				new DirectoryArgument("Dta"));
	}

	public static void main(String[] args) {
		new Dta2MascotGenericFormatUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new Dta2MascotGenericFormat();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Format, "PeakList Format" };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			Dta2MascotGenericFormatUI.main(new String[0]);
		}

		public String getVersion() {
			return Dta2MascotGenericFormat.version;
		}
	}
}
