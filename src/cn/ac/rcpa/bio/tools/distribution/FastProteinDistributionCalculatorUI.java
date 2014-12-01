package cn.ac.rcpa.bio.tools.distribution;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.OpenFileArgument;

public class FastProteinDistributionCalculatorUI extends
    AbstractFileProcessorUI {
  private static String title = "Fast Protein Distribution Calculator";

  public FastProteinDistributionCalculatorUI() {
    super(Constants.getSQHTitle(title,
        FastProteinDistributionCalculator.version), new OpenFileArgument(
        "Statistic Option", "statistic.xml"));
  }

  public static void main(String[] args) {
    new FastProteinDistributionCalculatorUI().showSelf();
  }

  @Override
  protected IFileProcessor getProcessor() {
    return new FastProteinDistributionCalculator();
  }
}
