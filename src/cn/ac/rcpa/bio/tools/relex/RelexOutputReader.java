/*
 * Created on 2006-1-12
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.relex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelexOutputReader {
  public static List<RelexProtein> read(String relexOutputFile)
      throws IOException {
    ArrayList<RelexProtein> result = new ArrayList<RelexProtein>();

    BufferedReader br = new BufferedReader(new FileReader(relexOutputFile));
    String line;
    while (null != (line = br.readLine())) {
      if (RelexProtein.isRelexProtein(line)) {
        result.add(RelexProtein.parse(line));
      } else if (RelexPeptide.isRelexPeptide(line)) {
        result.get(result.size() - 1).getPeptides().add(
            RelexPeptide.parse(line));
      }
    }
    br.close();

    return result;
  }

}
