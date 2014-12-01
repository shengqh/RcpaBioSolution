/*
 * Created on 2005-11-8
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class GOAnnotation2OverRepresentedTableDirectoryProcessor implements
		IFileProcessor {
	private String extension;

	public GOAnnotation2OverRepresentedTableDirectoryProcessor(String extension) {
		super();
		this.extension = extension;
	}

	public List<String> process(String treeDirectory) throws Exception {
		File[] treeFiles = new File(treeDirectory)
				.listFiles(new SpecialIOFileFilter(extension, true));

		ArrayList<String> result = new ArrayList<String>();
		GOAnnotation2OverRepresentedTableProcessor processor = new GOAnnotation2OverRepresentedTableProcessor();

		for (File treeFile : treeFiles) {
			result.addAll(processor.process(treeFile.getAbsolutePath()));
		}

		return result;
	}
}
