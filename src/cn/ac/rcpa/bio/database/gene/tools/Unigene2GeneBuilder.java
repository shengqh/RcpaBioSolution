/*
 * Created on 2006-1-19
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.database.gene.tools;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import cn.ac.rcpa.bio.database.gene.Unigene2GeneQuery;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.RcpaObjectUtils;

public class Unigene2GeneBuilder implements IFileProcessor {
  public List<String> process(String originFile) throws Exception {
    List<String> unigenes = RcpaObjectUtils.asList(FileUtils.readLines(
        new File(originFile), null));

    String result = originFile + ".gene";
    Map<String, Integer> unigene2geneMap = new Unigene2GeneQuery().getUnigene2geneMap(unigenes);

    PrintWriter pw = new PrintWriter(result);
    try {
      Collections.sort(unigenes);
      for (String unigene : unigenes) {
        Integer gene = unigene2geneMap.get(unigene);
        pw.println(unigene + "\t" + (null == gene ? "" : gene));
      }
    } finally {
      pw.close();
    }

    return Arrays.asList(new String[] { result });
  }

  public static void main(String[] args) throws Exception {
    Unigene2GeneBuilder builder = new Unigene2GeneBuilder();
    builder.process("F:\\Science\\Data\\wjr\\DvsA_EvsB_FvsC_co-up.txt");
    builder.process("F:\\Science\\Data\\wjr\\DvsA_EvsB_FvsC_co-down.txt");
  }
}
