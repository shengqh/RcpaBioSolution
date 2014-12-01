/*
 * Created on 2005-12-27
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.report;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ShotgunExcelReportBuilderUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Shotgun Excel Report Builder";

	private static String version = "1.0.1";

	private JRcpaTextField url = new JRcpaTextField("url", "Web Url",
			"http://192.168.22.100:8080/msms/", true);

	private JRcpaTextField project = new JRcpaTextField("project",
			"Project Name", "", true);

	public ShotgunExcelReportBuilderUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"noredundant", "Noredundant"));
		addComponent(url);
		addComponent(project);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new ShotgunExcelReportBuilder(url.getText(), project.getText(),
				getDatabaseType());
	}

	public static void main(String[] args) {
		new ShotgunExcelReportBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Report, "MS/MS Display" };
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
