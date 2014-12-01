package cn.ac.rcpa.bio.tools;

import org.biojava.bio.proteomics.Protease;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.bio.utils.ProteaseRenderer;
import cn.ac.rcpa.bio.utils.RcpaProteaseFactory;
import cn.ac.rcpa.component.JRcpaCheckBox;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaRangeField;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

/**
 * @author Sheng Quan-Hu (shengqh@gmail.com)
 */
public class TheoreticalDigestorUI extends AbstractFileProcessorUI {
	private static String title = "Theoretical Digestor";

	JRcpaComboBox<Protease> cbProtease;

	JRcpaCheckBox precursorMonoisotopic;

	JRcpaRangeField rangeField;

	JRcpaTextField txtMaxMissCleavages;

	JRcpaTextField txtAminoacids;

	JRcpaCheckBox writePeptides;

	public TheoreticalDigestorUI() {
		super(Constants.getSQHTitle(title, TheoreticalDigestor.version),
				new OpenFileArgument("Database Fasta", new String[] { "fasta" }));
		Protease[] proteases = RcpaProteaseFactory.getInstance().getProteaseList();
		cbProtease = new JRcpaComboBox<Protease>("Protease", "Protease", proteases,
				proteases[0], new ProteaseRenderer());
		precursorMonoisotopic = new JRcpaCheckBox("PrecursorMonoisotopic",
				"Is precursor monoisotopic?", 1);
		rangeField = new JRcpaRangeField("range", "Filter Peptide Mass Weight",
				400, 5000, true);
		txtMaxMissCleavages = new JRcpaTextField("maxmisscleavages",
				"Max missed cleavages", "1", true);
		txtAminoacids = new JRcpaTextField("aminoacids",
				"Amino acids (for frequency)", "", false);
		writePeptides = new JRcpaCheckBox("WritePeptides","Output peptides?",1);
		
		addComponent(cbProtease);
		addComponent(precursorMonoisotopic);
		addComponent(rangeField);
		addComponent(txtMaxMissCleavages);
		addComponent(txtAminoacids);
		addComponent(writePeptides);
	}

	public static void main(String[] args) {
		new TheoreticalDigestorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		double minMW = rangeField.getFrom();
		double maxMW = rangeField.getTo();
		int maxMissedCleavages;
		try {
			maxMissedCleavages = Integer.parseInt(txtMaxMissCleavages.getText());
		} catch (NumberFormatException ex) {
			throw new IllegalStateException("Input valid max missed cleavages first!");
		}
		final ISequenceValidator validator = SequenceValidatorFactory
				.getMWValidator(minMW, maxMW, precursorMonoisotopic.isSelected());

		return new TheoreticalDigestor(cbProtease.getSelectedItem(),
				maxMissedCleavages, validator, precursorMonoisotopic.isSelected(),
				txtAminoacids.getText(),writePeptides.isSelected());
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Other };
		}

		public String getCaption() {
			return "Theoretical Digestor";
		}

		public void run() {
			TheoreticalDigestorUI.main(new String[0]);
		}

		public String getVersion() {
			return TheoreticalDigestor.version;
		}
	}
}
