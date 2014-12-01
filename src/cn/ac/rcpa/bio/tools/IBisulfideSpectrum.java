package cn.ac.rcpa.bio.tools;

import java.io.PrintStream;

import cn.ac.rcpa.bio.proteomics.SequenceValidateException;

public interface IBisulfideSpectrum {
  public void generate(PrintStream ps, String[] peptides) throws
      SequenceValidateException;

}
