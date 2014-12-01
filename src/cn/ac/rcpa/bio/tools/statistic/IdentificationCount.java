/*
 * Created on 2005-6-16
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.statistic;

public class IdentificationCount {
  private int groupCount;

  private int proteinCount;

  private int groupUnique2Count;

  private int proteinUnique2Count;

  public IdentificationCount() {
  }

  public int getGroupCount() {
    return groupCount;
  }

  public void setGroupCount(int groupCount) {
    this.groupCount = groupCount;
  }

  public int getGroupUnique2Count() {
    return groupUnique2Count;
  }

  public void setGroupUnique2Count(int groupUnique2Count) {
    this.groupUnique2Count = groupUnique2Count;
  }

  public double getGroupUnique2Percent() {
    if (groupCount == 0) {
      return 0;
    } else {
      return groupUnique2Count * 100.0 / groupCount;
    }
  }

  public int getProteinCount() {
    return proteinCount;
  }

  public void setProteinCount(int proteinCount) {
    this.proteinCount = proteinCount;
  }

  public int getProteinUnique2Count() {
    return proteinUnique2Count;
  }

  public void setProteinUnique2Count(int proteinUnique2Count) {
    this.proteinUnique2Count = proteinUnique2Count;
  }

  public double getProteinUnique2Percent() {
    if (proteinCount == 0) {
      return 0;
    } else {
      return proteinUnique2Count * 100.0 / proteinCount;
    }
  }
}
