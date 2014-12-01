/*
 * Created on 2005-11-29
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.go;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaCheckBox;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.utils.OpenFileArgument;

public class GOAnnotation2OverRepresentedTableProcessorUI extends
		AbstractFileProcessorUI {
	private final static String title = "GO Annotation - Build Over Represented Category Html File";

	private final static String version = "1.0.0";

	private JRcpaFileField pictureFile = new JRcpaFileField("pictureFile",
			new OpenFileArgument("Corresponding Picture", "png"), false);

	private JRcpaCheckBox drawPngFile = new JRcpaCheckBox("drawPngFile",
			"Draw Over Represent Entry Map");

	public GOAnnotation2OverRepresentedTableProcessorUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"GOAnnotation Tree", "special.tree"));
		addComponent(pictureFile);
		addComponent(drawPngFile);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new GOAnnotation2OverRepresentedTableProcessor(pictureFile
				.getFilename(), drawPngFile.isSelected());
	}

	public static void main(String[] args) {
		new GOAnnotation2OverRepresentedTableProcessorUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation, "GO Annotation Analysis" };
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
