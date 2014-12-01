package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;
import cn.ac.rcpa.utils.SaveFileArgument;

public class ExtractFastaFileByAccessNumberPatternUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static final String title = "Extract Fasta File By Protein Access Number Pattern";

	private JRcpaTextField acPattern = new JRcpaTextField("acPattern",
			"Access Number Pattern", "^\\s*\\S+_HUMAN", true);

	public ExtractFastaFileByAccessNumberPatternUI() {
		super(Constants.getSQHTitle(title,
				ExtractFastaFileByAccessNumberPattern.version), new OpenFileArgument(
				"Database", "fasta"), new SaveFileArgument("Result", "fasta"));
		this.addComponent(acPattern);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ExtractFastaFileByAccessNumberPattern(acPattern.getText(),
				getArgument());
	}

	public static void main(String[] args) {
		new ExtractFastaFileByAccessNumberPatternUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Database };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			ExtractFastaFileByAccessNumberPatternUI.main(new String[0]);
		}

		public String getVersion() {
			return ExtractFastaFileByAccessNumberPattern.version;
		}
	}
}
