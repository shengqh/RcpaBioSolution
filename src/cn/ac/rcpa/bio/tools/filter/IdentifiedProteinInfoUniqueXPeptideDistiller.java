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


public class IdentifiedProteinInfoUniqueXPeptideDistiller extends IdentifiedProteinInfoDistiller{
  public static String version = "1.0.0";

  public IdentifiedProteinInfoUniqueXPeptideDistiller(int minUniquePeptideCount) {
    super(new ProteinLineUniquePeptideCountFilter(minUniquePeptideCount));
  }
}
