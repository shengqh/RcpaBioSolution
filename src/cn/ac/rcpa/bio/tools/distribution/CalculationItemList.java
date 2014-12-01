package cn.ac.rcpa.bio.tools.distribution;

import java.util.ArrayList;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;

public class CalculationItemList<
    E extends IIdentifiedPeptide,
    F extends IIdentifiedPeptideHit<E>> extends ArrayList<CalculationItem<E,F>>
 {
  public CalculationItemList() {
    super();
  }
}
