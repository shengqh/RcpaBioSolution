package cn.ac.rcpa.bio.tools.statistic;

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
import cn.ac.rcpa.utils.OpenFileArgument;

public class PeptideSequenceStatisticsCalculatorUI extends
		AbstractFileProcessorUI {
	private final static String title = "Peptide Sequence Statistic Calculator";

	private final static String version = "1.0.1";

	JRcpaComboBox<Protease> cbProtease;

	JRcpaCheckBox precursorMonoisotopic;

	public PeptideSequenceStatisticsCalculatorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"Peptide Sequence", "txt"));
		Protease[] proteases = RcpaProteaseFactory.getInstance().getProteaseList();
		cbProtease = new JRcpaComboBox<Protease>("Protease", "Protease", proteases,
				proteases[0], new ProteaseRenderer());
		precursorMonoisotopic = new JRcpaCheckBox("PrecursorMonoisotopic",
				"Is precursor monoisotopic?", 1);
		addComponent(cbProtease);
		addComponent(precursorMonoisotopic);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new PeptideSequenceStatisticsCalculator(
				cbProtease.getSelectedItem(), precursorMonoisotopic.isSelected());
	}

	public static void main(String[] args) {
		new PeptideSequenceStatisticsCalculatorUI().showSelf();
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
