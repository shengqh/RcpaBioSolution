package cn.ac.rcpa.bio.tools.solution.commands;

import cn.ac.rcpa.bio.tools.modification.IdentifiedResultN15FilterUI;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;

public class IdentifiedResultN15FilterCommand implements IRcpaBioToolCommand {
	public IdentifiedResultN15FilterCommand() {
	}

	public String[] getMenuNames() {
		return new String[] { CommandType.Modification };
	}

	public String getCaption() {
		return "IdentifiedResult N15 Filter";
	}

	public void run() {
		IdentifiedResultN15FilterUI.main(new String[0]);
	}

	public String getVersion() {
		return IdentifiedResultN15FilterUI.version;
	}
}
