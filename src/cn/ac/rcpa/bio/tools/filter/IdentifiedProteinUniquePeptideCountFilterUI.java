package cn.ac.rcpa.bio.tools.filter;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedProteinUniquePeptideCountFilterUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Extract Protein Access Number By Unique Peptide Count";

	public IdentifiedProteinUniquePeptideCountFilterUI() {
		super(Constants.getSQHTitle(title,
				IdentifiedProteinUniquePeptideCountFilter.version),
				new OpenFileArgument("Proteins", new String[] { "proteins",
						"noredundant" }));
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedProteinUniquePeptideCountFilter(getDatabaseType(), 2);
	}

	public static void main(String[] args) {
		new IdentifiedProteinUniquePeptideCountFilterUI().showSelf();
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
			IdentifiedProteinUniquePeptideCountFilterUI.main(new String[0]);
		}

		public String getVersion() {
			return IdentifiedProteinUniquePeptideCountFilter.version;
		}
	}

}
