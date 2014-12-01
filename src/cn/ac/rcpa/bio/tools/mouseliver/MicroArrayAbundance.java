package cn.ac.rcpa.bio.tools.mouseliver;

public class MicroArrayAbundance {
  private String probe;
  private String protein;
  private double fold1;
  private double fold2;

  public MicroArrayAbundance() {
  }

  public String getProtein() {
    return protein;
  }

  public String getProbe() {
    return probe;
  }

  public double getFold2() {
    return fold2;
  }

  public void setFold1(double fold1) {
    this.fold1 = fold1;
  }

  public void setProtein(String protein) {
    this.protein = protein;
  }

  public void setProbe(String probe) {
    this.probe = probe;
  }

  public void setFold2(double fold2) {
    this.fold2 = fold2;
  }

  public double getFold1() {
    return fold1;
  }

  public void addAbundance(MicroArrayAbundance another) {
    setFold1(this.getFold1() + another.getFold1());
    setFold2(this.getFold2() + another.getFold2());
  }

  public double getAverageAbundance() {
    return (getFold1() + getFold2()) / 2;
  }

  public static MicroArrayAbundance parse(String line) {
    String[] lines = line.split("\t");
    if (lines.length < 4) {
      throw new IllegalArgumentException(line +
          " is not a valid microarray abundance line");
    }

    MicroArrayAbundance result = new MicroArrayAbundance();

    result.setProbe(lines[0]);
    result.setProtein(lines[1]);
    try {
      result.setFold1(Double.parseDouble(lines[2]));
      result.setFold2(Double.parseDouble(lines[3]));
    }
    catch (Exception ex) {
      throw new IllegalArgumentException(line +
                                         " is not a valid microarray abundance line");
    }
    return result;
  }

  @Override
  public String toString() {
    return probe + "\t" + protein + "\t" + fold1 + "\t" + fold2;
  }
}
