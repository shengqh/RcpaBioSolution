package cn.ac.rcpa.bio.tools.convert;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ExtractPeptideHitFromProteinUI extends AbstractFileProcessorUI {
	private static String title = "Extract Peptide File From Protein File";

	public ExtractPeptideHitFromProteinUI() {
		super(Constants.getSQHTitle(title, ExtractPeptideHitFromProtein.version),
				new OpenFileArgument("Noredundant", "noredundant"));
	}

	public static void main(String[] args) {
		new ExtractPeptideHitFromProteinUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return ExtractPeptideHitFromProtein.getInstance();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Format };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			ExtractPeptideHitFromProteinUI.main(new String[0]);
		}

		public String getVersion() {
			return ExtractPeptideHitFromProtein.version;
		}
	}

}
