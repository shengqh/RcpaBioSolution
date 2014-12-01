package cn.ac.rcpa.bio.tools.filter;

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.proteomics.SequenceValidateException;
import cn.ac.rcpa.bio.utils.MassCalculator;

public class ProteinFastaFilterByWeightLarger extends AbstractProteinFastaFilter {
  private int minWeight;
  private MassCalculator mc;
  public static final String version = "1.0.1";
  public ProteinFastaFilterByWeightLarger(int minWeight) {
    super(minWeight + ".MinWeight" + minWeight);
    this.minWeight = minWeight;
    this.mc = new MassCalculator(false);
  }

  @Override
  protected boolean isValid(Sequence seq) throws SequenceValidateException {
    double weight = mc.getMass(seq.seqString());
    return weight >= minWeight;
  }
}
