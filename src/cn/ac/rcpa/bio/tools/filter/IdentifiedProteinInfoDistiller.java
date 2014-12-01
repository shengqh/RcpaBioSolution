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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.filter.IFilter;

public class IdentifiedProteinInfoDistiller implements IFileProcessor {
  private static Pattern proteinPattern = Pattern.compile("^\\$(\\d+)-(\\d+)\\s");
  private IFilter<String> proteinLineFilter;

  public IdentifiedProteinInfoDistiller(IFilter<String> proteinLineFilter) {
    this.proteinLineFilter = proteinLineFilter;
  }

  public static boolean isProteinLine(String line){
    return proteinPattern.matcher(line).find();
  }
  
  private boolean accept(String line){
    return proteinLineFilter == null || proteinLineFilter.accept(line);
  }
  
  public List<String> process(String originFile) throws Exception {
    final String resultFile = originFile + "." + proteinLineFilter.getType()
        + ".proinfo";
    PrintWriter pw = new PrintWriter(resultFile);
    try {
      BufferedReader br = new BufferedReader(new FileReader(originFile));
      try {
        String proteinHeader = br.readLine();
        String peptideHeader = br.readLine();
        if (proteinHeader != null && peptideHeader != null){
          pw.println(proteinHeader);
          String line;
          while (null != (line = br.readLine())) {
            if (isProteinLine(line) && accept(line)){
              pw.println(line);
            }
          }
        }
      } finally {
        br.close();
      }
    } finally {
      pw.close();
    }
    return Arrays.asList(new String[]{resultFile});
  }

}
