package cn.ac.rcpa.bio.tools.distribution;

import java.util.Map;

import junit.framework.TestCase;

public class DistributionComparisonResultReaderTest extends TestCase {

	/*
	 * Test method for 'cn.ac.rcpa.bio.tools.distribution.DistributionComparisonResultReader.readMap(String)'
	 */
	public void testReadMap() throws Exception {
		String file = "data/distribution.comparation";
		Map<String, Map<String, Integer>> result = DistributionComparisonResultReader.readMap(file);
		assertEquals(6, result.size());
		assertEquals((Integer)136,result.get("LCQ_Bound").get("LCQ_Bound"));
		assertEquals((Integer)65,result.get("LCQ_Bound").get("LCQ_Flow"));
		assertEquals((Integer)133,result.get("LCQ_Flow").get("LCQ_Flow"));
		assertEquals((Integer)191,result.get("LTQ_Bound").get("LTQ_Bound"));
		assertEquals((Integer)137,result.get("LTQ_Bound").get("LTQ_Flow"));
		assertEquals((Integer)233,result.get("LTQ_Flow").get("LTQ_Flow"));
		assertEquals((Integer)199,result.get("Orbitrap_Bound").get("Orbitrap_Bound"));
		assertEquals((Integer)173,result.get("Orbitrap_Bound").get("Orbitrap_Flow"));
		assertEquals((Integer)258,result.get("Orbitrap_Flow").get("Orbitrap_Flow"));
	}

}
