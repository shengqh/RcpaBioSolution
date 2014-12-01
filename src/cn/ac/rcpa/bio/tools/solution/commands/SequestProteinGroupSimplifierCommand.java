package cn.ac.rcpa.bio.tools.solution.commands;

import cn.ac.rcpa.bio.tools.SequestProteinGroupSimplifierUI;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;

public class SequestProteinGroupSimplifierCommand implements
		IRcpaBioToolCommand {
	public SequestProteinGroupSimplifierCommand() {
	}

	public String[] getMenuNames() {
		return new String[] { CommandType.Annotation };
	}

	public String getCaption() {
		return "Sequest Protein Group Simplifier";
	}

	public void run() {
		SequestProteinGroupSimplifierUI.main(new String[0]);
	}

	public String getVersion() {
		return SequestProteinGroupSimplifierUI.version;
	}
}
