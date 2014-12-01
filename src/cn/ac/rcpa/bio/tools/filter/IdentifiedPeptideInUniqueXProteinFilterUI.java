package cn.ac.rcpa.bio.tools.filter;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedPeptideInUniqueXProteinFilterUI extends
		AbstractFileProcessorWithArgumentUI {
	private static String title = "Get Identified Peptide in UniqueX Protein";

	public IdentifiedPeptideInUniqueXProteinFilterUI() {
		super(Constants.getSQHTitle(title,
				IdentifiedPeptideInUniqueXProteinFilter.version), new OpenFileArgument(
				"Proteins/Noredundant", new String[] { "proteins", "noredundant" }),
				"Min Unique Peptide Count");
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedPeptideInUniqueXProteinFilter(Integer
				.parseInt(getArgument()));
	}

	public static void main(String[] args) {
		new IdentifiedPeptideInUniqueXProteinFilterUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Other };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			IdentifiedPeptideInUniqueXProteinFilterUI.main(new String[0]);
		}

		public String getVersion() {
			return IdentifiedPeptideInUniqueXProteinFilter.version;
		}
	}

}
