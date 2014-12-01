package cn.ac.rcpa.bio.tools.report;

import java.util.Map;
import java.util.Set;

import org.biojava.bio.seq.Sequence;

public interface IProteinPeptideMapReader {
	Map<Sequence, Set<String>> read(String filename) throws Exception;
}
