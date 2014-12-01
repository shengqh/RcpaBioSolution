package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

/**
 * <p>
 * Title: RCPA Package
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: RCPA.SIBS.AC.CN
 * </p>
 * 
 * @author Sheng QuanHu
 * @version 1.0
 */

public class IdentifiedResultModificationFilterUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "IdentifiedResult Modification Filter";

	public IdentifiedResultModificationFilterUI() {
		super(Constants.getSQHTitle(title,
				IdentifiedResultModificationFilter.version), new OpenFileArgument(
				"Noredundant", "noredundant"), "Modification Aminoacids");
	}

	public static void main(String[] args) {
		new IdentifiedResultModificationFilterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedResultModificationFilter(getArgument());
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
			IdentifiedResultModificationFilterUI.main(new String[0]);
		}

		public String getVersion() {
			return IdentifiedResultModificationFilter.version;
		}
	}

}
