/*
 * Created on 2005-12-31
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

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.IParser;
import cn.ac.rcpa.bio.parser.ProteinSequenceParserFactory;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaObjectCheckBox;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ProteinSequenceReporterUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Fasta Protein Sequence Information Distiller";

	private List<JRcpaObjectCheckBox<IParser<Sequence>>> parsers = new ArrayList<JRcpaObjectCheckBox<IParser<Sequence>>>();

	public ProteinSequenceReporterUI() {
		super(Constants.getSQHTitle(title, ProteinSequenceReporter.version),
				new OpenFileArgument("Fasta", "fasta"));

		for (IParser<Sequence> parser : ProteinSequenceParserFactory
				.getParsers(getDatabaseType())) {
			addParser(parser);
		}
	}

	private void addParser(IParser<Sequence> parser) {
		JRcpaObjectCheckBox<IParser<Sequence>> cb = new JRcpaObjectCheckBox<IParser<Sequence>>(
				parser.getTitle().replaceAll("\\W", "_"), parser.getDescription(),
				parser);
		addComponent(cb);
		parsers.add(cb);
	}

	public static void main(String[] args) {
		new ProteinSequenceReporterUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		ProteinSequenceReporter result = new ProteinSequenceReporter();
		for (JRcpaObjectCheckBox<IParser<Sequence>> parser : parsers) {
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
			ProteinSequenceReporterUI.main(new String[0]);
		}

		public String getVersion() {
			return ProteinSequenceReporter.version;
		}
	}

}
