package cn.ac.rcpa.bio.tools.distribution;

import java.util.List;

import junit.framework.TestCase;

public class ProteinDistributionResultReaderTest extends TestCase {

	/*
	 * Test method for 'cn.ac.rcpa.bio.tools.distribution.ProteinDistributionResultReader.read(String)'
	 */
	public void testRead() throws Exception {
		List<ProteinDistributionItem> items = ProteinDistributionResultReader.read("data/distribution.distribution");
		assertEquals(2, items.size());
		assertEquals(1,items.get(0).getNames().length);
		assertEquals("IPI:IPI00022229.1", items.get(0).getNames()[0]);
		assertEquals("Apolipoprotein B-100 precursor", items.get(0).getDescriptions()[0]);
		assertEquals(24, items.get(0).getItems().size());
		assertEquals("7", items.get(0).getItems().get("LCQ_Bound_UniPepCount"));

		assertEquals(2,items.get(1).getNames().length);
		assertEquals("IPI:IPI00022418.1", items.get(1).getNames()[0]);
		assertEquals("IPI:IPI00339228.1", items.get(1).getNames()[1]);
		assertEquals("Splice Isoform 1 of Fibronectin precursor", items.get(1).getDescriptions()[0]);
		assertEquals("Splice Isoform 8 of Fibronectin precursor", items.get(1).getDescriptions()[1]);
		assertEquals(24, items.get(1).getItems().size());
		assertEquals("2", items.get(1).getItems().get("LCQ_Bound_UniPepCount"));
	}
}
