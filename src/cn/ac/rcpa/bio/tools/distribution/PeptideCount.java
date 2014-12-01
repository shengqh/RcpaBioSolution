package cn.ac.rcpa.bio.tools.distribution;

public class PeptideCount {
  private int peptideCount;
  private int uniquePeptideCount;

  public int getPeptideCount() {
    return peptideCount;
  }

  public void setUniquePeptideCount(int uniquePeptideCount) {
    this.uniquePeptideCount = uniquePeptideCount;
  }

  public void setPeptideCount(int peptideCount) {
    this.peptideCount = peptideCount;
  }

  public int getUniquePeptideCount() {
    return uniquePeptideCount;
  }

  public PeptideCount() {
    this.peptideCount = 0;
    this.uniquePeptideCount = 0;
  }
}
