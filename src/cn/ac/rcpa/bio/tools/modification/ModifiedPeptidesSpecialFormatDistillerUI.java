package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

/**
 * 这个程序将带有Ambigious信息的肽段作为Sequence一列， 同时把原来第一位的正常Peptide移到FollowCandidate中保存。
 * 
 * @author sheng
 * 
 */

public class ModifiedPeptidesSpecialFormatDistillerUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Modified Peptides Special Format Distiller";

	public ModifiedPeptidesSpecialFormatDistillerUI() {
		super(Constants.getSQHTitle(title,
				ModifiedPeptidesSpecialFormatDistiller.version), new OpenFileArgument(
				"BuildSummary PeptideHits", "peptides"), "Modified Aminoacid");
	}

	public static void main(String[] args) {
		new ModifiedPeptidesSpecialFormatDistillerUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ModifiedPeptidesSpecialFormatDistiller(getArgument());
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
			ModifiedPeptidesSpecialFormatDistillerUI.main(new String[0]);
		}

		public String getVersion() {
			return ModifiedPeptidesSpecialFormatDistiller.version;
		}
	}

}
