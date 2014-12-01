package cn.ac.rcpa.bio.tools.modification;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ModifiedPeptidePairFractionAndPhValueReportBuilderUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Modified Peptide Pair Fraction And PH Value Report Builder";

	private JRcpaTextField txtUrl = new JRcpaTextField("url", "Web Url",
			"http://192.168.22.100:8080/msms/", true);

	private JRcpaTextField txtProject = new JRcpaTextField("project",
			"Project Name", "", true);

	private JRcpaFileField txtFastaFile = new JRcpaFileField("ProteinDatabase",
			new OpenFileArgument("Protein Database", "fasta"), true);

	private JRcpaTextField txtModifiedAminoacids = new JRcpaTextField(
			"ModifiedAminoacid", "Modified Aminoacid", "STY", true);

	private JRcpaTextField txtFractionPattern = new JRcpaTextField(
			"FractionPattern", "Fraction Regex Pattern", "(SAX|SCX)", true);

	private JRcpaTextField txtPhValuePattern = new JRcpaTextField(
			"PhValuePattern", "Ph Value Regex Pattern", "_(\\d+)$", true);

	public ModifiedPeptidePairFractionAndPhValueReportBuilderUI() {
		super(Constants.getSQHTitle(title,
				ModifiedPeptidePairFractionAndPhValueReportBuilder.version),
				new OpenFileArgument("BuildSummary Peptides", "peptides"));
		this.addComponent(txtFastaFile);
		this.addComponent(txtUrl);
		this.addComponent(txtProject);
		this.addComponent(txtModifiedAminoacids);
		this.addComponent(txtFractionPattern);
		this.addComponent(txtPhValuePattern);
	}

	@Override
	protected IFileProcessor getProcessor() throws NoSuchElementException,
			IOException, BioException {
		return new ModifiedPeptidePairFractionAndPhValueReportBuilder(txtUrl
				.getText(), txtProject.getText(), getDatabaseType(),
				txtModifiedAminoacids.getText().trim(), txtFractionPattern.getText()
						.trim(), txtPhValuePattern.getText().trim(), txtFastaFile
						.getFilename());
	}

	public static void main(String[] args) {
		new ModifiedPeptidePairFractionAndPhValueReportBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Modification };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			ModifiedPeptidePairFractionAndPhValueReportBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return ModifiedPeptidePairFractionAndPhValueReportBuilder.version;
		}
	}

}
