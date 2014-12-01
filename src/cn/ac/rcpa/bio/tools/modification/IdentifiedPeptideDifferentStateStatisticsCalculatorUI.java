package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.OpenFileArgument;

/**
 * 这个程序用于统计BuildSummary的肽段文件中，同时出现非修饰和多种修饰，非修饰和一种修饰，只有多种修饰这三种类型的肽段数量。
 * 
 * @author Quanhu Sheng
 * 
 */
public class IdentifiedPeptideDifferentStateStatisticsCalculatorUI extends
		AbstractFileProcessorWithArgumentUI {
	private static final String title = "Identified Peptide Different Modification State Statistic Calculaotr";

	public IdentifiedPeptideDifferentStateStatisticsCalculatorUI() {
		super(Constants.getSQHTitle(title,
				IdentifiedPeptideDifferentStateStatisticsCalculator.version),
				new OpenFileArgument("BuildSummary Peptides", "peptides"),
				"Modification Aminoacids");
	}

	public static void main(String[] args) {
		new IdentifiedPeptideDifferentStateStatisticsCalculatorUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedPeptideDifferentStateStatisticsCalculator(
				getArgument());
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
			IdentifiedPeptideDifferentStateStatisticsCalculatorUI.main(new String[0]);
		}

		public String getVersion() {
			return IdentifiedPeptideDifferentStateStatisticsCalculator.version;
		}
	}

}
