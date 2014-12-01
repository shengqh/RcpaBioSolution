package cn.ac.rcpa.bio.tools.solution.commands;

import cn.ac.rcpa.bio.proteomics.results.buildsummary.viewer.BuildSummaryResultViewer;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;

public class BuildSummaryResultViewerCommand implements IRcpaBioToolCommand {
	public BuildSummaryResultViewerCommand() {
	}

	public String[] getMenuNames() {
		return new String[] { CommandType.Other };
	}

	public String getCaption() {
		return "BuildSummary Result Viewer";
	}

	public void run() {
		BuildSummaryResultViewer.main(new String[0]);
	}

	public String getVersion() {
		return BuildSummaryResultViewer.version;
	}
}
