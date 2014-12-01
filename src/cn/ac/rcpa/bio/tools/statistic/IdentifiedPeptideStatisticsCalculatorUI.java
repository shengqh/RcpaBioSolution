package cn.ac.rcpa.bio.tools.statistic;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.biojava.bio.proteomics.Protease;
import org.biojava.bio.proteomics.ProteaseManager;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.annotation.StatisticRanges;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.classification.ClassificationFactory;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.proteomics.classification.impl.IdentifiedPeptideDecoyDatabaseClassification;
import cn.ac.rcpa.bio.proteomics.classification.impl.IdentifiedPeptideMWClassification;
import cn.ac.rcpa.bio.proteomics.classification.impl.IdentifiedPeptideMissedCleavagesClassification;
import cn.ac.rcpa.bio.proteomics.classification.impl.IdentifiedPeptideNumberOfProteaseTerminalClassification;
import cn.ac.rcpa.bio.proteomics.classification.impl.IdentifiedPeptidePrecursorDiffMzClassification;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.classification.SpRankClassification;
import cn.ac.rcpa.bio.proteomics.statistics.IStatisticsCalculator;
import cn.ac.rcpa.bio.proteomics.statistics.StatisticsCalculatorFactory;
import cn.ac.rcpa.bio.proteomics.statistics.impl.CompositeStatisticsCalculator;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.bio.utils.ProteaseRenderer;
import cn.ac.rcpa.bio.utils.RcpaProteaseFactory;
import cn.ac.rcpa.component.IRcpaComponent;
import cn.ac.rcpa.component.JRcpaCheckBox;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaComponentProxy;
import cn.ac.rcpa.component.JRcpaHorizontalComponentList;
import cn.ac.rcpa.component.JRcpaObjectCheckBox;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedPeptideStatisticsCalculatorUI extends
		AbstractFileProcessorUI {
	private final static String title = "Identified Peptide Statistic Calculator";

	private final static String version = "1.0.6";

	private JLabel lblType = new JLabel("Select Classfication Type");

	private JRcpaComponentProxy lblTypeLabel = new JRcpaComponentProxy(lblType,
			0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, 1);

	JRcpaComboBox<Protease> cbProtease;

	JRcpaCheckBox precursorMonoisotopic;

	private JRcpaTextField txtReversedDbPattern = new JRcpaTextField("pattern",
			"Decoy Database Pattern", "^REVERSE", true);

	private IdentifiedPeptideMWClassification mwClassification = new IdentifiedPeptideMWClassification(
			StatisticRanges.getPeptideMWRange(), true);

	private IClassification<IIdentifiedPeptide> piClassification = ClassificationFactory
			.getIdentifiedPeptidePIClassification(StatisticRanges.getPIRange());

	private IdentifiedPeptideMissedCleavagesClassification missCleavagedClassification = new IdentifiedPeptideMissedCleavagesClassification(
			ProteaseManager.getTrypsin());

	private IdentifiedPeptideNumberOfProteaseTerminalClassification nptClassification = new IdentifiedPeptideNumberOfProteaseTerminalClassification(
			ProteaseManager.getTrypsin());

	private IClassification<IIdentifiedPeptide> gravyClassification = ClassificationFactory
			.getIdentifiedPeptideGRAVYClassification(StatisticRanges.getGRAVYRange());

	private IClassification<IIdentifiedPeptide> precursorMzClassification = ClassificationFactory
			.getIdentifiedPeptidePrecursorMassClassification(StatisticRanges
					.getPrecursorMZRange());

	private IClassification<IIdentifiedPeptide> precursorDiffMzClassification = new IdentifiedPeptidePrecursorDiffMzClassification(
			StatisticRanges.getPrecursorDiffMZRange());

	private IClassification<IIdentifiedPeptide> chargeClassification = ClassificationFactory
			.getIdentifiedPeptideChargeClassification();

	private IClassification<IIdentifiedPeptide> xcorrClassification = ClassificationFactory
			.getIdentifiedPeptideXcorrClassification(StatisticRanges.getXCorrRange());

	private IClassification<IIdentifiedPeptide> deltaCnClassification = ClassificationFactory
			.getIdentifiedPeptideDeltaCnClassification(StatisticRanges
					.getDeltaCnRange());

	private IClassification<IIdentifiedPeptide> spRankClassification = new SpRankClassification();

	private IClassification<IIdentifiedPeptide> lengthClassification = ClassificationFactory
			.getIdentifiedPeptideLengthClassification();

	private IdentifiedPeptideDecoyDatabaseClassification fpClassification = new IdentifiedPeptideDecoyDatabaseClassification(
			"");

	JRcpaObjectCheckBox<IdentifiedPeptideMWClassification> mwClass = new JRcpaObjectCheckBox<IdentifiedPeptideMWClassification>(
			"MW", "Peptide Mass Weight", 1, mwClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> piClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"PI", "Isotopic Point", 2, piClassification);

	JRcpaObjectCheckBox<IdentifiedPeptideMissedCleavagesClassification> missCleavagedClass = new JRcpaObjectCheckBox<IdentifiedPeptideMissedCleavagesClassification>(
			"MissCleavage", "Number of Miss Cleavage", 3, missCleavagedClassification);

	JRcpaObjectCheckBox<IdentifiedPeptideNumberOfProteaseTerminalClassification> nptClass = new JRcpaObjectCheckBox<IdentifiedPeptideNumberOfProteaseTerminalClassification>(
			"NumberOfProteaseTerminal", "Number of Protease Terminal", 4,
			nptClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> precursorMzClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"PrecursorMz", "Precursor Mz", 1, precursorMzClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> precursorDiffMzClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"PrecursorDiffMz", "Precursor Diff Mz", 2, precursorDiffMzClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> chargeClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"Charge", "Precursor Charge", 3, chargeClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> gravyClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"GRAVY", "GRAVY", 4, gravyClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> xcorrClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"XCorr", "XCorr", 1, xcorrClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> deltaCnClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"DeltaCn", "DeltaCn", 2, deltaCnClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> spRankClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"spRank", "spRank", 3, spRankClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> lengthClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"PeptideLength", "Peptide Length", 4, lengthClassification);

	JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> fpClass = new JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>(
			"DecoyDatabase", "Decoy Database", 1, fpClassification);

	private JRcpaHorizontalComponentList comp1 = new JRcpaHorizontalComponentList();

	private JRcpaHorizontalComponentList comp2 = new JRcpaHorizontalComponentList();

	private JRcpaHorizontalComponentList comp3 = new JRcpaHorizontalComponentList();

	private JRcpaHorizontalComponentList comp4 = new JRcpaHorizontalComponentList();

	public IdentifiedPeptideStatisticsCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"Identified Peptide", "peptides"));
		Protease[] proteases = RcpaProteaseFactory.getInstance().getProteaseList();
		cbProtease = new JRcpaComboBox<Protease>("Protease", "Protease", proteases,
				proteases[0], new ProteaseRenderer());
		precursorMonoisotopic = new JRcpaCheckBox("PrecursorMonoisotopic",
				"Is precursor monoisotopic?", 1);

		comp1.addComponent(lblTypeLabel);
		comp1.addComponent(mwClass);
		comp1.addComponent(piClass);
		comp1.addComponent(missCleavagedClass);
		comp1.addComponent(nptClass);

		comp2.addComponent(precursorMzClass);
		comp2.addComponent(precursorDiffMzClass);
		comp2.addComponent(chargeClass);
		comp2.addComponent(gravyClass);

		comp3.addComponent(xcorrClass);
		comp3.addComponent(deltaCnClass);
		comp3.addComponent(spRankClass);
		comp3.addComponent(lengthClass);

		comp4.addComponent(fpClass);

		addComponent(comp1);
		addComponent(comp2);
		addComponent(comp3);
		addComponent(comp4);

		addComponent(cbProtease);
		addComponent(precursorMonoisotopic);
		addComponent(txtReversedDbPattern);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedPeptideStatisticsCalculator(getStatisticCalculator(
				cbProtease.getSelectedItem(), precursorMonoisotopic.isSelected()));
	}

	private List<IClassification<IIdentifiedPeptide>> getClassificationFromComponentList(
			JRcpaHorizontalComponentList lst) {
		List<IClassification<IIdentifiedPeptide>> result = new ArrayList<IClassification<IIdentifiedPeptide>>();
		for (IRcpaComponent comp : lst.getComponents()) {
			if (comp instanceof JRcpaObjectCheckBox) {
				JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>> ocb = (JRcpaObjectCheckBox<IClassification<IIdentifiedPeptide>>) comp;
				if (ocb.isSelected()) {
					result.add(ocb.getObject());
				}
			}
		}
		return result;
	}

	private IStatisticsCalculator<IIdentifiedPeptide> getStatisticCalculator(
			Protease protease, boolean precursorMonoIsotopic) {
		mwClassification.setMonoIsotopic(precursorMonoisotopic.isSelected());

		missCleavagedClassification.setProtease(cbProtease.getSelectedItem());

		nptClassification.setProtease(cbProtease.getSelectedItem());

		fpClassification.setReversedDbPattern(txtReversedDbPattern.getText());

		ArrayList<IStatisticsCalculator<IIdentifiedPeptide>> calcs = new ArrayList<IStatisticsCalculator<IIdentifiedPeptide>>();

		List<IClassification<IIdentifiedPeptide>> classes = new ArrayList<IClassification<IIdentifiedPeptide>>();
		classes.addAll(getClassificationFromComponentList(comp1));
		classes.addAll(getClassificationFromComponentList(comp2));
		classes.addAll(getClassificationFromComponentList(comp3));
		classes.addAll(getClassificationFromComponentList(comp4));

		for (IClassification<IIdentifiedPeptide> ic : classes) {
			calcs.add(StatisticsCalculatorFactory.getStatisticsCalculator(ic));
		}

		for (int i = 0; i < classes.size(); i++) {
			for (int j = i + 1; j < classes.size(); j++) {
				calcs.add(StatisticsCalculatorFactory.getStatisticsCalculator(classes
						.get(i), classes.get(j), StatisticsCalculatorFactory.OUTPUT_NONE));
			}
		}

		IStatisticsCalculator<IIdentifiedPeptide> calc = new CompositeStatisticsCalculator<IIdentifiedPeptide>(
				calcs);

		return calc;
	}

	public static void main(String[] args) {
		new IdentifiedPeptideStatisticsCalculatorUI().showSelf();
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

		public String getVersion() {
			return version;
		}

		public void run() {
			main(new String[0]);
		}
	}

	@Override
	protected int getPerfectWidth() {
		return 1024;
	}
}
