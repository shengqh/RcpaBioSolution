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
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: RCPA.SIBS.AC.CN
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class PairwiseDifferentModificationPeptideFilterUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Get Peptides with Different Modified Site";

	public PairwiseDifferentModificationPeptideFilterUI() {
		super(Constants.getSQHTitle(title,
				PairwiseDifferentModificationPeptideFilter.version),
				new OpenFileArgument("Peptides", "peptides"), "Modification Aminoacids");
	}

	public static void main(String[] args) {
		new PairwiseDifferentModificationPeptideFilterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new PairwiseDifferentModificationPeptideFilter(getArgument());
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
			PairwiseDifferentModificationPeptideFilterUI.main(new String[0]);
		}

		public String getVersion() {
			return PairwiseDifferentModificationPeptideFilter.version;
		}
	}

}
