package cn.ac.rcpa.bio.tools.statistic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.JDOMException;

public class RawScanDistributionCalculator {
  public RawScanDistributionCalculator() {
  }

  public static void main(String[] args) throws Exception {
    String scanFilename1 =
        "F:\\Science\\Data\\HPPP\\2DLC_Micro_LTQ\\HPPP_Salt.msLevel";
    String scanFilename2 = "F:\\Science\\Data\\HIPP\\hippocampi_2d.msLevel";

    calculate(scanFilename1);

    calculate(scanFilename2);
  }

  public static void calculate(String scanFilename) throws
      Exception {
    Map<String, Map<Integer,
        Integer>> rawfileMap = RawScanFile.getRawScanMap(scanFilename);

    Map<Integer,Integer> indexCount = new HashMap<Integer,Integer>();
    for(Map<Integer, Integer> scanMap:rawfileMap.values()){
      for(Integer index:scanMap.values()){
        if (!indexCount.containsKey(index)){
          indexCount.put(index, 0);
        }
        indexCount.put(index, indexCount.get(index).intValue() + 1);
      }
    }

    List<Integer> indexs = new ArrayList<Integer>(indexCount.keySet());
    Collections.sort(indexs);
    PrintWriter pw = new PrintWriter(new FileWriter(scanFilename + ".scan"));
    try {
      pw.println("ScanIndex\tCount");
      for (Integer index : indexs) {
        pw.println(index + "\t" + indexCount.get(index));
        System.out.println(index + "\t" + indexCount.get(index));
      }
    }
    finally {
      pw.close();
    }
  }
}
