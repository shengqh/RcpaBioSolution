package cn.ac.rcpa.bio.tools.database;

import junit.framework.TestCase;

import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;

public class ReversedDatabaseBuilderTest extends TestCase {

	/*
	 * Test method for 'cn.ac.rcpa.bio.tools.database.ReversedDatabaseBuilder.getReversedSeq(Sequence, int)'
	 */
	public void testGetReversedSeq() throws Exception {
		Sequence seq = ProteinTools.createProteinSequence("ABCDE","TEST");
		Sequence reversedSeq = new ReversedDatabaseBuilder(false).getReversedSeq(seq, 1);
		assertEquals("EDCBA", reversedSeq.seqString());
		assertEquals("REVERSED_00000001", reversedSeq.getName());

		reversedSeq = new ReversedDatabaseBuilder(true).getReversedSeq(seq, 1);
		assertEquals("EDCBA", reversedSeq.seqString());
		assertEquals("REVERSED_1", reversedSeq.getName());
	}

}
