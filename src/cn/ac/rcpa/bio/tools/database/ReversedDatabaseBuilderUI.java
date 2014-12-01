package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaCheckBox;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ReversedDatabaseBuilderUI extends AbstractFileProcessorUI {
	private static String title = "Build Reversed Database";

	private JRcpaCheckBox cbPadNumber = new JRcpaCheckBox("PadNumber",
			"Format as REVERSED_00000001", 1);

	public ReversedDatabaseBuilderUI() {
		super(Constants.getSQHTitle(title, ReversedDatabaseBuilder.version),
				new OpenFileArgument("Fasta", "fasta"));
		addComponent(cbPadNumber);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ReversedDatabaseBuilder(!cbPadNumber.isSelected());
	}

	public static void main(String[] args) {
		new ReversedDatabaseBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.MachineLearning };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			ReversedDatabaseBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return ReversedDatabaseBuilder.version;
		}
	}

}
