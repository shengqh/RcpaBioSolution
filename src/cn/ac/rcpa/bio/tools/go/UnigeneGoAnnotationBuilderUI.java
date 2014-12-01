/*
 * Created on 2006-1-19
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
import cn.ac.rcpa.utils.OpenFileArgument;

public class UnigeneGoAnnotationBuilderUI extends AbstractFileProcessorUI {
	public UnigeneGoAnnotationBuilderUI() {
		super(Constants.getSQHTitle(title, UnigeneGoAnnotationBuilder.version),
				new OpenFileArgument("Unigene Access Number File", "txt"));
	}

	private static String title = "GO Annotation - From Unigene Access Number File";

	@Override
	protected IFileProcessor getProcessor() {
		return new UnigeneGoAnnotationBuilder();
	}

	public static void main(String[] args) {
		new UnigeneGoAnnotationBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation, "GO Annotation Builder" };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			UnigeneGoAnnotationBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return UnigeneGoAnnotationBuilder.version;
		}
	}

}
