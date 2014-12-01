/*
 * Created on 2005-6-3
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.comparator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ac.rcpa.bio.proteomics.IIdentifiedProteinGroup;
import cn.ac.rcpa.bio.proteomics.IIdentifiedResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultProteinOnlyReader;
import cn.ac.rcpa.utils.Pair;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class IdentifiedResultPairwiseComparator {
  private static Set<String> getProteinNames(IIdentifiedProteinGroup group) {
    Set<String> result = new HashSet<String>();
    for (int i = 0; i < group.getProteinCount(); i++) {
      result.add(group.getProtein(i).getProteinName());
    }
    return result;
  }

  private static List<Pair<IIdentifiedProteinGroup, Set<String>>> getGroupProteinsList(
      IIdentifiedResult ir) {
    List<Pair<IIdentifiedProteinGroup, Set<String>>> result = new ArrayList<Pair<IIdentifiedProteinGroup, Set<String>>>();

    for (int i = 0; i < ir.getProteinGroupCount(); i++) {
      result.add(new Pair<IIdentifiedProteinGroup, Set<String>>(ir
          .getProteinGroup(i), getProteinNames(ir.getProteinGroup(i))));
    }

    return result;
  }

  private static String compareSelf(
      List<Pair<IIdentifiedProteinGroup, Set<String>>> list1) {
    int itotal = 0;
    for (Pair<IIdentifiedProteinGroup, Set<String>> group1 : list1) {
      if (group1.fst.getParentCount() > 0) {
        continue;
      }

      itotal++;
    }
    return itotal + "/100%/0%";
  }

  private static String compare(
      List<Pair<IIdentifiedProteinGroup, Set<String>>> list1,
      List<Pair<IIdentifiedProteinGroup, Set<String>>> list2) {
    DecimalFormat df = new DecimalFormat("##.#");
    int itotal = 0;
    int isame = 0;
    int ihomolog = 0;
    for (Pair<IIdentifiedProteinGroup, Set<String>> group1 : list1) {
      if (group1.fst.getParentCount() > 0) {
        continue;
      }

      itotal++;

      for (Pair<IIdentifiedProteinGroup, Set<String>> group2 : list2) {
        if (group1.snd.equals(group2.snd)) {
          isame++;
          break;
        }

        boolean bFound = false;
        for (String name : group1.snd) {
          if (group2.snd.contains(name)) {
            bFound = true;
            break;
          }
        }

        if (bFound) {
          ihomolog++;
          break;
        }
      }
    }

    return itotal + "/" + df.format((double)isame * 100 / itotal) + "%/" + df.format((double)ihomolog * 100 / itotal) + "%";
  }

  public static void main(String[] args) throws Exception {
    final File rootDir = new File("Z:/summary/mouse_liver");
//    final File rootDir = new File("F:\\Science\\Data\\MouseLiver\\test");
    doCompare(rootDir);
  }

  private static void doCompare(final File rootDir) throws IOException {
    final File[] resultDirs = RcpaFileUtils.getSubDirectories(rootDir);
    List<File> resultFiles = new ArrayList<File>();
    for (File resultDir : resultDirs) {
      File[] proteinsFiles = resultDir.listFiles(new SpecialIOFileFilter(
          "proteins", true));
      resultFiles.addAll(Arrays.asList(proteinsFiles));
    }

    List<List<Pair<IIdentifiedProteinGroup, Set<String>>>> resultList = new ArrayList<List<Pair<IIdentifiedProteinGroup, Set<String>>>>();
    for (File resultFile : resultFiles) {
      resultList.add(getGroupProteinsList(BuildSummaryResultProteinOnlyReader
            .getInstance().read(resultFile.getAbsolutePath())));
    }

    final String saveFile = rootDir.getAbsolutePath() + "/compare.xls";

    Map<String, Map<String, String>> compared = new LinkedHashMap<String, Map<String,String>>();
      for (File resultFile : resultFiles) {
        compared.put(resultFile.getName(), new LinkedHashMap<String, String>());
      }

      for (int i = 0; i < resultFiles.size(); i++) {
        Map<String, String> current = compared.get(resultFiles.get(i).getName());
        System.out.print(resultFiles.get(i).getName());
        List<Pair<IIdentifiedProteinGroup, Set<String>>> resulti = resultList.get(i);
        for (int j = 0; j < resultFiles.size(); j++) {
          List<Pair<IIdentifiedProteinGroup, Set<String>>> resultj = resultList.get(j);
          String compareResult = (j == i) ? compareSelf(resulti) : compare(resulti, resultj);
          current.put(resultFiles.get(j).getName(), compareResult);
          System.out.print("\t" + compareResult);
        }
        System.out.println();
      }

    PrintWriter pw = new PrintWriter(new FileWriter(saveFile));
    try {
      for (String origin:compared.keySet()){
        pw.print("\t" + RcpaFileUtils.changeExtension(origin,""));
      }
      pw.println();

      for (String compareTo:compared.keySet()){
        pw.print(RcpaFileUtils.changeExtension(compareTo,""));
        for (String origin:compared.keySet()){
          pw.print("\t" + compared.get(origin).get(compareTo));
        }
        pw.println();
      }
    } finally {
      pw.close();
    }
  }
}
