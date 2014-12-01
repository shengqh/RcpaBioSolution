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
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.DirectoryArgument;

public class GeneGoAnnotationTaxonomyBuilderUI extends AbstractFileProcessorUI {
	private final static String title = "GO Annotation - Gene Database Annotation By Taxonomy";

	private final static String version = "1.0.0";

	private JRcpaTextField taxonomy_id = new JRcpaTextField("taxonomy_id",
			"Taxonomy ID", "9606", true);

	private JRcpaTextField taxonomy_name = new JRcpaTextField("taxonomy_name",
			"Taxonomy Name", "mouse", true);

	public GeneGoAnnotationTaxonomyBuilderUI() {
		super(Constants.getSQHTitle(title, version), new DirectoryArgument(
				"GO Annotation Result"));
		addComponent(taxonomy_id);
		addComponent(taxonomy_name);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new GeneGoAnnotationTaxonomyBuilder(taxonomy_id.getText(),
				taxonomy_name.getText());
	}

	public static void main(String[] args) {
		new GeneGoAnnotationTaxonomyBuilderUI().showSelf();
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
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
