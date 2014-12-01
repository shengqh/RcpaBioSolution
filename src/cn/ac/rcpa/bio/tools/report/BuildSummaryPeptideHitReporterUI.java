/*
 * Created on 2005-12-25
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.report;

import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.BlankParser;
import cn.ac.rcpa.Constants;
import cn.ac.rcpa.IParser;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.parser.BuildSummaryPeptideHitParserFactory;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaObjectCheckBox;
import cn.ac.rcpa.utils.OpenFileArgument;

public class BuildSummaryPeptideHitReporterUI extends AbstractFileProcessorUI {
	private static String title = "Peptide Information Distiller";

	private List<JRcpaObjectCheckBox<IParser<BuildSummaryPeptideHit>>> parsers = new ArrayList<JRcpaObjectCheckBox<IParser<BuildSummaryPeptideHit>>>();

	public BuildSummaryPeptideHitReporterUI() {
		super(Constants.getSQHTitle(title, BuildSummaryPeptideHitReporter.version),
				new OpenFileArgument("Peptides", "peptides"));

		addParser(new BlankParser<BuildSummaryPeptideHit>());
		for (IParser<BuildSummaryPeptideHit> parser : BuildSummaryPeptideHitParserFactory
				.getParsers()) {
			addParser(parser);
		}
	}

	private void addParser(IParser<BuildSummaryPeptideHit> parser) {
		JRcpaObjectCheckBox<IParser<BuildSummaryPeptideHit>> cb = new JRcpaObjectCheckBox<IParser<BuildSummaryPeptideHit>>(
				parser.getTitle().replaceAll("\\W", "_"), parser.getDescription(),
				parser);
		addComponent(cb);
		parsers.add(cb);
	}

	public static void main(String[] args) {
		new BuildSummaryPeptideHitReporterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		BuildSummaryPeptideHitReporter result = new BuildSummaryPeptideHitReporter();
		for (JRcpaObjectCheckBox<IParser<BuildSummaryPeptideHit>> parser : parsers) {
			if (parser.isSelected()) {
				result.getParsers().add(parser.getObject());
			}
		}

		return result;
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Report };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			BuildSummaryPeptideHitReporterUI.main(new String[0]);
		}

		public String getVersion() {
			return BuildSummaryPeptideHitReporter.version;
		}
	}

}
