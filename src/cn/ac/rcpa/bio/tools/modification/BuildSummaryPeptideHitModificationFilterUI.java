package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class BuildSummaryPeptideHitModificationFilterUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "BuildSummary Peptide Modification Filter";

	public BuildSummaryPeptideHitModificationFilterUI() {
		super(Constants.getSQHTitle(title,
				BuildSummaryPeptideHitModificationFilter.version),
				new OpenFileArgument("Peptides", "peptides"), "Modification Aminoacids");
	}

	public static void main(String[] args) {
		new BuildSummaryPeptideHitModificationFilterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new BuildSummaryPeptideHitModificationFilter(getArgument());
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
			BuildSummaryPeptideHitModificationFilterUI.main(new String[0]);
		}

		public String getVersion() {
			return BuildSummaryPeptideHitModificationFilter.version;
		}
	}

}
