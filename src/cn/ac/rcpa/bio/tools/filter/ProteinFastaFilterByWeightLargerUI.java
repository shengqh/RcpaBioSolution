package cn.ac.rcpa.bio.tools.filter;

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

public class ProteinFastaFilterByWeightLargerUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Get Proteins With Larger Weight From Fasta File";

	public ProteinFastaFilterByWeightLargerUI() {
		super(Constants
				.getSQHTitle(title, ProteinFastaFilterByWeightLarger.version),
				new OpenFileArgument("Fasta", "fasta"), "Min Protein Weight Da");
	}

	public static void main(String[] args) {
		new ProteinFastaFilterByWeightLargerUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ProteinFastaFilterByWeightLarger(Integer.parseInt(getArgument()));
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[]{CommandType.Database};
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			ProteinFastaFilterByWeightLargerUI.main(new String[0]);
		}

		public String getVersion() {
			return ProteinFastaFilterByWeightLarger.version;
		}
	}
}
