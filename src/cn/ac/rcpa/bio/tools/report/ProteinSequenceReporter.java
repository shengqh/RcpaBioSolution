/*
 * Created on 2005-12-31
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.report;

import java.util.List;

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.utils.SequenceUtils;

public class ProteinSequenceReporter extends AbstractReporter<Sequence> {

	public static String version = "1.0.1";

	@Override
	protected List<Sequence> readFromFile(String originFile) throws Exception {
		return SequenceUtils.readFastaProteins(originFile);
	}
}
