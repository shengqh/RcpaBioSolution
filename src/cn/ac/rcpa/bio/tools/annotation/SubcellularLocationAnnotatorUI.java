package cn.ac.rcpa.bio.tools.annotation;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
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

public class SubcellularLocationAnnotatorUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Subcellular Location Annotation By Fasta File";

	public SubcellularLocationAnnotatorUI() {
		super(Constants.getSQHTitle(title, SubcellularLocationAnnotator.version),
				new OpenFileArgument("Fasta", "fasta"));
	}

	public static void main(String[] args) {
		new SubcellularLocationAnnotatorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new SubcellularLocationAnnotator(getDatabaseType());
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			SubcellularLocationAnnotatorUI.main(new String[0]);
		}

		public String getVersion() {
			return SubcellularLocationAnnotator.version;
		}
	}
}
