/*
 * Created on 2005-6-16
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.statistic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.tools.statistic.IdentifiedResultSummaryBuilder.ValueType;

public class IdentifiedResultSummaryBuilderTest extends TestCase {
	private IdentifiedResultSummaryBuilder builder = new IdentifiedResultSummaryBuilder();

	public void testGetScanCount() throws IOException {
		assertEquals("88536", builder.getScanCount("data/summary/data.sequest.log"));
	}

	public void testGetDtaCount() throws IOException {
		assertEquals("149930", builder.getDtaCount("data/summary/data.sequest.log"));
	}

	public void testParsePeptideCount() throws IOException {
		File curDir = new File(".");
		System.out.println(curDir.getAbsolutePath());

		Map<ValueType, String> values = new HashMap<ValueType, String>();
		new IdentifiedResultSummaryBuilder().parsePeptideCount(values,
				"data/summary/data.peptides");
		assertEquals("159", values.get(ValueType.PEPTIDE_COUNT));
		assertEquals("52", values.get(ValueType.UNIQUE_PEPTIDE_COUNT));
	}

	public void testGetIdentificationCount() throws IOException {
		IdentificationCount ic = builder
				.getIdentificationCount("data/summary/data.noredundant");
		assertEquals(12, ic.getProteinCount());
		assertEquals(11, ic.getGroupCount());
		assertEquals(8, ic.getGroupUnique2Count());
		assertEquals(8, ic.getProteinUnique2Count());
	}

}
