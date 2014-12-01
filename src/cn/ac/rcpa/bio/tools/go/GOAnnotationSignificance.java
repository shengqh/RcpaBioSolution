package cn.ac.rcpa.bio.tools.go;

import java.util.LinkedHashMap;
import java.util.Map;

public class GOAnnotationSignificance {
  private final static Map<SignificanceLevel, String> colors;
  
  static {
    colors = new LinkedHashMap<SignificanceLevel, String>();
    colors.put(SignificanceLevel.NORMAL,"#F7F7DE");
    colors.put(SignificanceLevel.SIGNIFICANCE,"#BDFFFF");
    colors.put(SignificanceLevel.SIGNIFICANCE_PLUS,"#7BC3EF");
    colors.put(SignificanceLevel.SIGNIFICANCE_PLUS_PLUS,"#4269E7");
  }

  private double significance;
  private double significance_plus;
  private double significance_plus_plus;

  public GOAnnotationSignificance(int totalEntryCount) {
    significance = 0.05 / totalEntryCount;
    significance_plus = significance / 100;
    significance_plus_plus = significance_plus / 100;
  }

  public SignificanceLevel getLevel(double overRepresentedProbability) {
    if (overRepresentedProbability > significance) {
      return SignificanceLevel.NORMAL;
    }

    if (overRepresentedProbability > significance_plus) {
      return SignificanceLevel.SIGNIFICANCE;
    }

    if (overRepresentedProbability > significance_plus_plus) {
      return SignificanceLevel.SIGNIFICANCE_PLUS;
    }

    return SignificanceLevel.SIGNIFICANCE_PLUS_PLUS;
  }
  
  public double getProbability(SignificanceLevel level){
    if (SignificanceLevel.NORMAL == level){
      return 1.0;
    }
    
    if (SignificanceLevel.SIGNIFICANCE == level){
      return significance;
    }
    
    if (SignificanceLevel.SIGNIFICANCE_PLUS == level){
      return significance_plus;
    }
    
    if (SignificanceLevel.SIGNIFICANCE_PLUS_PLUS == level){
      return significance_plus_plus;
    }
    
    throw new IllegalStateException("I don't know the significance of " + level);
  }

  public static String getColor(SignificanceLevel level) {
    return colors.get(level);
  }

  public String getColor(double overRepresentedProbability) {
    return colors.get(getLevel(overRepresentedProbability));
  }

  public double getSignificance() {
    return significance;
  }

  public double getSignificance_plus() {
    return significance_plus;
  }

  public double getSignificance_plus_plus() {
    return significance_plus_plus;
  }
}
