package cn.ac.rcpa.bio.tools.distribution;

import java.io.BufferedReader;
import java.io.FileReader;

public class DistributionResultSpliter {
  public DistributionResultSpliter() {
  }

  public static void main(String[] args) throws Exception {
    final String file = "F:\\Science\\Data\\MouseLiver\\all\\Peptide_Location_CLASSIFICATION\\mouse_liver_all.noredundant.Peptide_Location.statistic.stat";
    BufferedReader br = new BufferedReader(new FileReader(file));
    String classifiedNames = br.readLine();
    String[] classifiedNameList = classifiedNames.split("\t");
    String line;
    while((line = br.readLine()) != null){
      String[] info = line.split("\t");
      if (info.length == classifiedNameList.length + 1){

      }
    }
  }
}
