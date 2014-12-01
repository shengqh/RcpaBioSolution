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

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ObverseReverseIdentifiedPeptideSeparatorUI extends
		AbstractFileProcessorUI {
	private static String title = "Obverse/Reverse Peptide Separator";

	private static String version = "1.0.0";

	private JRcpaTextField patternField = new JRcpaTextField("pattern",
			"Decoy Database Pattern", "^REVERSE", true);

	public ObverseReverseIdentifiedPeptideSeparatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"BuildSummary Peptides", "peptides"));
		addComponent(patternField);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ObverseReverseIdentifiedPeptideSeparator(patternField.getText());
	}

	public static void main(String[] args) {
		new ObverseReverseIdentifiedPeptideSeparatorUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.MachineLearning };
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
