package cn.ac.rcpa.bio.tools;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.AbstractUI;
import cn.ac.rcpa.bio.aminoacid.Aminoacids;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.SequenceValidateException;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.bio.utils.MassCalculator;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaComponentProxy;
import cn.ac.rcpa.component.JRcpaTextField;

public class TheoreticalSpectrumBuilderUI extends AbstractUI {
	private static String title = "Theoretical Spectrum Builder";

	private static String version = "1.0.1";

	private JRcpaTextField txtPeptide = new JRcpaTextField("Peptide", "Peptide",
			"", true);

	private JRcpaComboBox<IsotopicType> cbIsotopicType = new JRcpaComboBox<IsotopicType>(
			"IsotopicType", "Isotopic Type", IsotopicType.values(),
			IsotopicType.Monoisotopic);

	private JRcpaTextField txtModified = new JRcpaTextField("Modified",
			"Modified Aminoacid (input: C +15.99 Y -17.99)", "", false);

	private JScrollPane pnlResult;

	private JTextArea txtResult = new JTextArea();

	private JRcpaComponentProxy proxyResult;

	public TheoreticalSpectrumBuilderUI() {
		super(Constants.getSQHTitle(title, version));
		pnlResult = new JScrollPane(txtResult);
		pnlResult.setMinimumSize(new Dimension(800, 600));
		pnlResult.setPreferredSize(new Dimension(800, 600));
		proxyResult = new JRcpaComponentProxy(pnlResult, 1.0);
		this.addComponent(txtPeptide);
		this.addComponent(cbIsotopicType);
		this.addComponent(txtModified);
		this.addComponent(proxyResult);
	}

	private Map<Character, Double> getStaticModification() {
		Map<Character, Double> result = new HashMap<Character, Double>();

		Pattern pattern = Pattern.compile("(\\S)\\s+([+-0123456789.]+)");
		Aminoacids aas = Aminoacids.getStableInstance();

		Matcher match = pattern.matcher(txtModified.getText());
		while (match.find()) {
			char aa = match.group(1).charAt(0);

			double value = Double.parseDouble(match.group(2));
			if (cbIsotopicType.getSelectedItem() == IsotopicType.Monoisotopic) {
				double staticValue = aas.get(aa).getMonoMass() + value;
				result.put(aa, staticValue);
			} else {
				double staticValue = aas.get(aa).getAverageMass() + value;
				result.put(aa, staticValue);
			}
		}

		return result;
	}

	@Override
	protected void doRealGo() {
		String peptide = txtPeptide.getText().trim().toUpperCase();
		txtPeptide.setText(peptide);

		MassCalculator massCalculator = new MassCalculator(cbIsotopicType
				.getSelectedItem() == IsotopicType.Monoisotopic);
		massCalculator.addStaticModifications(getStaticModification());
		try {
			double[] bseries = massCalculator.getBSeries(peptide);
			double[] yseries = massCalculator.getYSeries(peptide);
			showSeries(bseries, yseries);
		} catch (SequenceValidateException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showSeries(double[] bseries, double[] yseries) {
		final DecimalFormat df = new DecimalFormat("0.0000");
		StringBuffer sb = new StringBuffer();
		sb.append("No.\tb\ty\tb2\ty2\n");
		for (int i = 0; i <= bseries.length; i++) {
			int charIndex = i + 1;
			if (i == 0) {
				sb.append(charIndex + "\t" + df.format(bseries[i]) + "\t" + "-" + "\t"
						+ df.format((bseries[i] + MassCalculator.Hmono) / 2) + "\t" + "-"
						+ "\n");
			} else if (i == bseries.length) {
				sb
						.append(charIndex
								+ "\t"
								+ "-"
								+ "\t"
								+ df.format(yseries[yseries.length - i])
								+ "\t"
								+ "-"
								+ "\t"
								+ df
										.format((yseries[yseries.length - i] + MassCalculator.Hmono) / 2)
								+ "\n");
			} else {
				sb
						.append(charIndex
								+ "\t"
								+ df.format(bseries[i])
								+ "\t"
								+ df.format(yseries[yseries.length - i])
								+ "\t"
								+ df.format((bseries[i] + MassCalculator.Hmono) / 2)
								+ "\t"
								+ df
										.format((yseries[yseries.length - i] + MassCalculator.Hmono) / 2)
								+ "\n");
			}
		}
		txtResult.setText(sb.toString());
	}

	public static void main(String[] args) {
		new TheoreticalSpectrumBuilderUI().showSelf();
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
			TheoreticalSpectrumBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
