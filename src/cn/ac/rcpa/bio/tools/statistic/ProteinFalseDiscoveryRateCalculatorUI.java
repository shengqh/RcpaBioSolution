/*
 * Created on 2006-2-17
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
import cn.ac.rcpa.component.JRcpaDoubleField;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ProteinFalseDiscoveryRateCalculatorUI extends
		AbstractFileProcessorUI {
	private final static String title = "Protein False Discovery Rate Calculator";

	private static String version = "1.0.1";

	private JRcpaTextField txtDecoyDatabasePattern = new JRcpaTextField(
			"DecoyDatabasePattern", "Decoy Database Pattern", "REVERSED_", true);

	private JRcpaDoubleField txtUniquePeptideCount = new JRcpaDoubleField(
			"UniquePeptideCount", "Unique Peptide Count", 2, true);

	public ProteinFalseDiscoveryRateCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"BuildSummary Proteins", "noredundant"));
		addComponent(txtDecoyDatabasePattern);
		addComponent(txtUniquePeptideCount);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ProteinFalseDiscoveryRateCalculator(txtDecoyDatabasePattern
				.getText(), (int) txtUniquePeptideCount.getValue());
	}

	public static void main(String[] args) {
		new ProteinFalseDiscoveryRateCalculatorUI().showSelf();
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
