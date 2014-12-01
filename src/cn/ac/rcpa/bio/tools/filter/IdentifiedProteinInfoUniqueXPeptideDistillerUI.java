/*
 * Created on 2005-11-23
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.filter;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedProteinInfoUniqueXPeptideDistillerUI extends
		AbstractFileProcessorWithArgumentUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7852000068440093208L;
	private static String title = "Extract Protein Information By Unique Peptide Count";

	public IdentifiedProteinInfoUniqueXPeptideDistillerUI() {
		super(Constants.getSQHTitle(title,
				IdentifiedProteinInfoUniqueXPeptideDistiller.version),
				new OpenFileArgument("Proteins", new String[] { "proteins",
						"noredundant" }), "Min Peptide Count");
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedProteinInfoUniqueXPeptideDistiller(Integer
				.parseInt(getArgument()));
	}

	public static void main(String[] args) {
		new IdentifiedProteinInfoUniqueXPeptideDistillerUI().showSelf();
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
			IdentifiedProteinInfoUniqueXPeptideDistillerUI.main(new String[0]);
		}

		public String getVersion() {
			return IdentifiedProteinInfoUniqueXPeptideDistiller.version;
		}
	}

}
