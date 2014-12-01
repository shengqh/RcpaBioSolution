package cn.ac.rcpa.bio.tools.statistic;

import javax.swing.JOptionPane;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.AbstractUI;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaIntegerField;
import cn.ac.rcpa.utils.HypergeometricDistributionCalculator;

public class PValueOfOverRepresentedByHypergeometricCalculatorUI extends
		AbstractUI {
	private static String title = "pValue Of Over Represented By Hypergeometric Calculator";

	private static String version = "1.0.0";

	private JRcpaIntegerField xField = new JRcpaIntegerField("x",
			"x (positive count in sub dataset)", 10, true);

	private JRcpaIntegerField nField = new JRcpaIntegerField("n",
			"n (total count in sub dataset)", 100, true);

	private JRcpaIntegerField MField = new JRcpaIntegerField("M",
			"M (positive count in dataset)", 100, true);

	private JRcpaIntegerField NField = new JRcpaIntegerField("N",
			"N (total count in dataset)", 10000, true);

	public PValueOfOverRepresentedByHypergeometricCalculatorUI() {
		super(Constants.getSQHTitle(title, version));
		this.addComponent(xField);
		this.addComponent(nField);
		this.addComponent(MField);
		this.addComponent(NField);
	}

	@Override
	protected void doRealGo() {
		double probability = HypergeometricDistributionCalculator.getInstance()
				.pValueOfOverRepresentedByHypergeometric(xField.getValue(),
						nField.getValue(), MField.getValue(), NField.getValue());
		System.out.println("Probability = " + probability);
		JOptionPane.showMessageDialog(this, "Probability = " + probability);
	}

	public static void main(String[] args) {
		new PValueOfOverRepresentedByHypergeometricCalculatorUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Statistic };
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
