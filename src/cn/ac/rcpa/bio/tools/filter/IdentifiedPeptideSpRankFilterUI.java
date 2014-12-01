package cn.ac.rcpa.bio.tools.filter;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedPeptideSpRankFilterUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Filter Peptides by SpRank";

	public IdentifiedPeptideSpRankFilterUI() {
		super(Constants.getSQHTitle(title, IdentifiedPeptideSpRankFilter.version),
				new OpenFileArgument("Peptides", "peptides"), "Max SpRank");
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedPeptideSpRankFilter(Integer.parseInt(getArgument()));
	}

	public static void main(String[] args) {
		new IdentifiedPeptideSpRankFilterUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[]{CommandType.Other};
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			IdentifiedPeptideSpRankFilterUI.main(new String[0]);
		}

		public String getVersion() {
			return IdentifiedPeptideSpRankFilter.version;
		}
	}

}
