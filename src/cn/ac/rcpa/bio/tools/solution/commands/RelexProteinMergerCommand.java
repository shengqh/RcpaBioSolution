package cn.ac.rcpa.bio.tools.solution.commands;

import cn.ac.rcpa.bio.tools.relex.RelexProteinMergerUI;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;

public class RelexProteinMergerCommand implements IRcpaBioToolCommand {
	public RelexProteinMergerCommand() {
	}

	public String[] getMenuNames() {
		return new String[] { CommandType.Quantification };
	}

	public String getCaption() {
		return "Relex Protein Result Merger";
	}

	public void run() {
		RelexProteinMergerUI.main(new String[0]);
	}

	public String getVersion() {
		return RelexProteinMergerUI.version;
	}
}
