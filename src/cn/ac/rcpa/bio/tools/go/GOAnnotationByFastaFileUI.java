package cn.ac.rcpa.bio.tools.go;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.annotation.impl.GOAnnotationProcessorByGO;
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
 * @author Sheng QuanHu
 * @version 1.0.1
 */

public class GOAnnotationByFastaFileUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static final String title = "GO Annotation - From Protein Fasta File";

	private static final String version = "1.0.1";

	public GOAnnotationByFastaFileUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument("Fasta",
				"fasta"));
	}

	public static void main(String[] args) {
		new GOAnnotationByFastaFileUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new GOAnnotationProcessorByGO(getDatabaseType());
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation, "GO Annotation Builder" };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
