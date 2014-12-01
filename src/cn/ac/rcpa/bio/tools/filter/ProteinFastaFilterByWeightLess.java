package cn.ac.rcpa.bio.tools.filter;

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.proteomics.SequenceValidateException;
import cn.ac.rcpa.bio.utils.MassCalculator;

public class ProteinFastaFilterByWeightLess extends AbstractProteinFastaFilter {
  private int maxWeight;
  private MassCalculator mc;
  public static final String version = "1.0.1";
  public ProteinFastaFilterByWeightLess(int maxWeight) {
    super(maxWeight + ".MaxWeight" + maxWeight);
    this.maxWeight = maxWeight;
    this.mc = new MassCalculator(false);
  }

  @Override
  protected boolean isValid(Sequence seq) throws SequenceValidateException {
    double weight = mc.getMass(seq.seqString());
    return weight <= maxWeight;
  }
}
