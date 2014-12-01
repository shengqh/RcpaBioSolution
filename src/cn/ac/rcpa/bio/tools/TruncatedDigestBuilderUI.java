package cn.ac.rcpa.bio.tools;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.proteomics.Protease;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.JRcpaModificationTextField;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.tools.impl.SequenceMWValidator;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.bio.utils.MassCalculator;
import cn.ac.rcpa.bio.utils.ProteaseRenderer;
import cn.ac.rcpa.bio.utils.RcpaProteaseFactory;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaRangeField;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.filter.AndFilter;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.filter.impl.StringPatternFilter;
import cn.ac.rcpa.utils.OpenFileArgument;

public class TruncatedDigestBuilderUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Truncated Digest Builder";

	private JRcpaComboBox<Protease> cbProtease;

	private JRcpaRangeField rangeField;

	private JRcpaTextField txtMaxMissCleavages;

	private JRcpaTextField txtSequencePattern;

	private JRcpaComboBox<IsotopicType> cbIsotopicType = new JRcpaComboBox<IsotopicType>(
			"IsotopicType", "Isotopic Type", IsotopicType.values(),
			IsotopicType.Monoisotopic);

	private JRcpaModificationTextField txtModified = new JRcpaModificationTextField(
			false);

	public TruncatedDigestBuilderUI() {
		super(Constants.getSQHTitle(title, TruncatedDigestBuilder.version),
				new OpenFileArgument("Database Fasta", new String[] { "fasta" }));
		Protease[] proteases = RcpaProteaseFactory.getInstance().getProteaseList();
		cbProtease = new JRcpaComboBox<Protease>("Protease", "Protease", proteases,
				proteases[0], new ProteaseRenderer());
		rangeField = new JRcpaRangeField("pmRange", "Peptide Mass Range", 600,
				6000, true);
		txtMaxMissCleavages = new JRcpaTextField("maxmisscleavages",
				"Max missed cleavages", "1", true);
		txtSequencePattern = new JRcpaTextField("SequencePattern",
				"Sequence Regex Pattern", "N[RK]$|N[^P][TS]", false);
		this.addComponent(cbProtease);
		this.addComponent(rangeField);
		this.addComponent(txtMaxMissCleavages);
		this.addComponent(cbIsotopicType);
		this.addComponent(txtModified);
		this.addComponent(txtSequencePattern);
	}

	public static void main(String[] args) {
		new TruncatedDigestBuilderUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		MassCalculator massCalculator = new MassCalculator(cbIsotopicType
				.getSelectedItem() == IsotopicType.Monoisotopic);
		massCalculator.addStaticModifications(txtModified
				.getModificationMap(cbIsotopicType.getSelectedItem()));

		IFilter<String> validators = getValidator(massCalculator);

		int maxMissedCleavages;
		try {
			maxMissedCleavages = Integer.parseInt(txtMaxMissCleavages.getText());
		} catch (NumberFormatException ex) {
			throw new IllegalStateException("Input valid max missed cleavages first!");
		}

		return new TruncatedDigestBuilder(getDatabaseType(), massCalculator,
				cbProtease.getSelectedItem(), maxMissedCleavages, validators);
	}

	private IFilter<String> getValidator(MassCalculator massCalculator) {
		List<IFilter<String>> filterList = new ArrayList<IFilter<String>>();
		IFilter<String> validator = new SequenceMWValidator(rangeField.getFrom(),
				rangeField.getTo(), massCalculator);
		filterList.add(validator);

		if (txtSequencePattern.getText().trim().length() > 0) {
			filterList.add(new StringPatternFilter(txtSequencePattern.getText()
					.trim()));
		}

		return new AndFilter<String>(filterList);
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
			TruncatedDigestBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return TruncatedDigestBuilder.version;
		}
	}

}
