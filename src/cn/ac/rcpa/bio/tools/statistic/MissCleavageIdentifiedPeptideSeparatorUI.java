/*
 * Created on 2006-2-16
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.statistic;

import org.biojava.bio.proteomics.Protease;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.bio.utils.ProteaseRenderer;
import cn.ac.rcpa.bio.utils.RcpaProteaseFactory;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.utils.OpenFileArgument;

public class MissCleavageIdentifiedPeptideSeparatorUI extends
		AbstractFileProcessorUI {
	private static String title = "Miss Cleavage Peptide Separator";

	private static String version = "1.0.0";

	private JRcpaComboBox<Protease> cbProtease;

	public MissCleavageIdentifiedPeptideSeparatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"BuildSummary Peptides", "peptides"));
		Protease[] proteases = RcpaProteaseFactory.getInstance().getProteaseList();
		cbProtease = new JRcpaComboBox<Protease>("Protease", "Protease", proteases,
				proteases[0], new ProteaseRenderer());
		addComponent(cbProtease);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new MissCleavageIdentifiedPeptideSeparator(cbProtease
				.getSelectedItem());
	}

	public static void main(String[] args) {
		new MissCleavageIdentifiedPeptideSeparatorUI().showSelf();
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
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
