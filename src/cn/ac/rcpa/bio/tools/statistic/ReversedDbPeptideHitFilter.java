/*
 * Created on 2006-2-15
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.statistic;

import java.util.List;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.filter.IFilter;

public class ReversedDbPeptideHitFilter implements
    IFilter<IIdentifiedPeptideHit> {
  private Pattern pattern;

  public ReversedDbPeptideHitFilter(String reversedDbPattern) {
    pattern = Pattern.compile(reversedDbPattern);
  }

  /**
   * Return true if one of the proteins come from reversed database
   */
  public boolean accept(IIdentifiedPeptideHit e) {
    List<String> proteins = e.getPeptide(0).getProteinNames();
    for (String protein : proteins) {
      if (pattern.matcher(protein).find()) {
        return true;
      }
    }
    return false;
  }

  public String getType() {
    return "ReversedDB";
  }
}
