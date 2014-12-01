/*
 * Created on 2005-11-23
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.filter;

import cn.ac.rcpa.filter.IFilter;

public class ProteinLineUniquePeptideCountFilter implements IFilter<String> {
  private int minUniquePeptideCount;

  public ProteinLineUniquePeptideCountFilter(int minUniquePeptideCount) {
    this.minUniquePeptideCount = minUniquePeptideCount;
  }

  public boolean accept(String e) {
    String[] lines = e.split("\t");
    return lines.length >= 7
        && Integer.parseInt(lines[3]) >= minUniquePeptideCount;
  }

  public String getType() {
    return "MinUniPep" + minUniquePeptideCount;
  }

}
