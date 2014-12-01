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
 * @author Sheng QuanHu
 * @version 1.0.1
 */

public class PairwiseModificationPeptideFilterUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Get Peptides with Modified and Unmodified State";

	public PairwiseModificationPeptideFilterUI() {
		super(Constants.getSQHTitle(title,
				PairwiseModificationPeptideFilter.version), new OpenFileArgument(
				"Peptides", "peptides"), "Modification Aminoacids");
	}

	public static void main(String[] args) {
		new PairwiseModificationPeptideFilterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new PairwiseModificationPeptideFilter(getArgument());
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
			PairwiseModificationPeptideFilterUI.main(new String[0]);
		}

		public String getVersion() {
			return PairwiseModificationPeptideFilter.version;
		}
	}

}
