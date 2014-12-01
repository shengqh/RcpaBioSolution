/*
 * Created on 2005-6-20
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.statistic;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.DirectoryArgument;

public class IdentifiedResultSummaryBuilderUI extends AbstractFileProcessorUI {
	private static String title = "Identified Result Summary Builder";

	private static String version = "1.0.1";

	public IdentifiedResultSummaryBuilderUI() {
		super(Constants.getSQHTitle(title, version), new DirectoryArgument("Root"));
	}

	public static void main(String[] args) {
		new IdentifiedResultSummaryBuilderUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new IdentifiedResultSummaryBuilder();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Statistic };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
