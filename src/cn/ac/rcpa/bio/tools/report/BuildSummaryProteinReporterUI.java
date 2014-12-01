/*
 * Created on 2005-12-30
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

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.IParser;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.parser.BuildSummaryProteinParserFactory;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaObjectCheckBox;
import cn.ac.rcpa.utils.OpenFileArgument;

public class BuildSummaryProteinReporterUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "BuildSummary Protein Information Distiller";

	private List<JRcpaObjectCheckBox<IParser<BuildSummaryProtein>>> parsers = new ArrayList<JRcpaObjectCheckBox<IParser<BuildSummaryProtein>>>();

	public BuildSummaryProteinReporterUI() {
		super(Constants.getSQHTitle(title, BuildSummaryProteinReporter.version),
				new OpenFileArgument("Noredundant", "noredundant"));

		for (IParser<BuildSummaryProtein> parser : BuildSummaryProteinParserFactory
				.getParsers(getDatabaseType())) {
			addParser(parser);
		}
	}

	private void addParser(IParser<BuildSummaryProtein> parser) {
		JRcpaObjectCheckBox<IParser<BuildSummaryProtein>> cb = new JRcpaObjectCheckBox<IParser<BuildSummaryProtein>>(
				parser.getTitle().replaceAll("\\W", "_"), parser.getDescription(),
				parser);
		addComponent(cb);
		parsers.add(cb);
	}

	public static void main(String[] args) {
		new BuildSummaryProteinReporterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		BuildSummaryProteinReporter result = new BuildSummaryProteinReporter();
		for (JRcpaObjectCheckBox<IParser<BuildSummaryProtein>> parser : parsers) {
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
			BuildSummaryProteinReporterUI.main(new String[0]);
		}

		public String getVersion() {
			return BuildSummaryProteinReporter.version;
		}
	}

}
