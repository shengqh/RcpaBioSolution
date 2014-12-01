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
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ProteinPositiveProbabilityCalculatorUI extends
		AbstractFileProcessorUI {
	private final static String title = "Protein Positive Probability Calculator";

	private final static String version = "1.0.0";

	private JRcpaTextField peptideTpr = new JRcpaTextField("peptideTPR",
			"Peptide True Positive Rate", "0.95", true);

	public ProteinPositiveProbabilityCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"BuildSummary Proteins", "noredundant"));
		addComponent(peptideTpr);
	}

	@Override
	protected IFileProcessor getProcessor() {
		double peptideTprValue = Double.parseDouble(peptideTpr.getText());
		if (peptideTprValue <= 0 || peptideTprValue >= 1.0) {
			throw new IllegalArgumentException(peptideTpr.getText()
					+ " is not a valid True Positive Rate (0 ~ 1.0)");
		}
		return new ProteinPositiveProbabilityCalculator(peptideTprValue);
	}

	public static void main(String[] args) {
		new ProteinPositiveProbabilityCalculatorUI().showSelf();
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
