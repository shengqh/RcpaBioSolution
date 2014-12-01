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
package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import cn.ac.rcpa.bio.annotation.GOAAspectType;
import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.impl.UnigeneGoAnnotationQuery;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.RcpaObjectUtils;

public class UnigeneGoAnnotationBuilder implements IFileProcessor {
  public static String version = "1.0.0";

  public List<String> process(String originFile) throws Exception {
    String[] unigenes = RcpaObjectUtils.toStringArray(FileUtils.readLines(
        new File(originFile), null));

    File unigeneFile = new File(originFile);
    File subDir = new File(unigeneFile.getParent(), "STATISTIC");
    subDir.mkdirs();

    ArrayList<String> result = new ArrayList<String>();
    for (GOAAspectType type : GOAAspectType.GOA_ASPECT_TYPES) {
      GOAClassificationEntry entry = new UnigeneGoAnnotationQuery()
          .getAnnotation(type.getRoot().getAccession(), type.getDefaultLevel(),
              unigenes);
      File resultFile = new File(subDir, unigeneFile.getName() + ".go_"
          + type.getRoot().getName() + ".tree");
      entry.saveToFile(resultFile.getAbsolutePath());
      result.add(resultFile.getAbsolutePath());
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    UnigeneGoAnnotationBuilder builder = new UnigeneGoAnnotationBuilder();
    builder.process("F:\\Science\\Data\\wjr\\DvsA_EvsB_FvsC_co-up.txt");
    builder.process("F:\\Science\\Data\\wjr\\DvsA_EvsB_FvsC_co-down.txt");
  }
}
