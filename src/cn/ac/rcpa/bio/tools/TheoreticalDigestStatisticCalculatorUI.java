package cn.ac.rcpa.bio.tools;

import org.biojava.bio.proteomics.Protease;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.ProteaseRenderer;
import cn.ac.rcpa.bio.utils.RcpaProteaseFactory;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaRangeField;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

/**
 * @author Sheng Quan-Hu (shengqh@gmail.com)
 */
public class TheoreticalDigestStatisticCalculatorUI extends
		AbstractFileProcessorUI {
	private static String title = "Theoretical Digest Statistic Calculator";

	JRcpaComboBox<Protease> cbProtease;

	JRcpaRangeField rangeField;

	JRcpaTextField txtMaxMissCleavages;

	public TheoreticalDigestStatisticCalculatorUI() {
		super(Constants.getSQHTitle(title, TheoreticalDigestor.version),
				new OpenFileArgument("Database Fasta", new String[] { "fasta" }));
		Protease[] proteases = RcpaProteaseFactory.getInstance().getProteaseList();
		cbProtease = new JRcpaComboBox<Protease>("Protease", "Protease", proteases,
				proteases[0], new ProteaseRenderer());
		rangeField = new JRcpaRangeField("range", "Mass Spectrometry M/Z Range",
				600, 2000, true);
		txtMaxMissCleavages = new JRcpaTextField("maxmisscleavages",
				"Max missed cleavages", "1", true);
		addComponent(cbProtease);
		addComponent(rangeField);
		addComponent(txtMaxMissCleavages);
	}

	public static void main(String[] args) {
		new TheoreticalDigestStatisticCalculatorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		double minMz = rangeField.getFrom();
		double maxMz = rangeField.getTo();
		int maxMissedCleavages;
		try {
			maxMissedCleavages = Integer.parseInt(txtMaxMissCleavages.getText());
		} catch (NumberFormatException ex) {
			throw new IllegalStateException("Input valid max missed cleavages first!");
		}

		return new TheoreticalDigestStatisticCalculator(cbProtease
				.getSelectedItem(), maxMissedCleavages, minMz, maxMz, new double[] {
				0.5, 1, 2, 5, 10, 20 });
	}
}
