/*
 * Created on 2006-2-17
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
import cn.ac.rcpa.component.JRcpaDoubleField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class MassShiftProcessorUI extends AbstractFileProcessorUI {
	private final static String title = "Mass Shift Calculator";

	private final static String version = "1.0.0";

	private JRcpaDoubleField txtMaxPPMTolerance = new JRcpaDoubleField(
			"MaxPPMTolerance", "Consider Mass Shift Range(ppm)", 20, true);

	public MassShiftProcessorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"BuildSummary Proteins", "noredundant"));
		this.addComponent(txtMaxPPMTolerance);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new MassShiftProcessor(txtMaxPPMTolerance.getValue());
	}

	public static void main(String[] args) {
		new MassShiftProcessorUI().showSelf();
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

		public String getVersion() {
			return version;
		}

		public void run() {
			main(new String[0]);
		}
	}

}
