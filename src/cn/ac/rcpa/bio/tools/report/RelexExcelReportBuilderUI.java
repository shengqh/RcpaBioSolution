/*
 * Created on 2006-1-12
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
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
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class RelexExcelReportBuilderUI extends
		AbstractFileProcessorByDatabaseTypeUI {
	private static String title = "Relex Excel Report Builder";

	private JRcpaTextField url = new JRcpaTextField("url", "Web Url",
			"http://192.168.22.100:8080/msms/", true);

	private JRcpaTextField project = new JRcpaTextField("project",
			"Project Name", "", true);

	private JRcpaFileField peptidesFile = new JRcpaFileField("PeptidesFile",
			new OpenFileArgument("BuildSummary Peptides", "peptides"), true);

	public RelexExcelReportBuilderUI() {
		super(Constants.getSQHTitle(title, RelexExcelReportBuilder.version),
				new OpenFileArgument("Relex Output", "txt"));
		addComponent(peptidesFile);
		addComponent(url);
		addComponent(project);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new RelexExcelReportBuilder(url.getText(), project.getText(),
				getDatabaseType(), peptidesFile.getFilename());
	}

	public static void main(String[] args) {
		new RelexExcelReportBuilderUI().showSelf();
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
			RelexExcelReportBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return RelexExcelReportBuilder.version;
		}
	}

}
