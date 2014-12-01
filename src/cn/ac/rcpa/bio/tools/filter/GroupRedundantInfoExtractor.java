package cn.ac.rcpa.bio.tools.filter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultGroupInfoReader;

public class GroupRedundantInfoExtractor implements IFileProcessor {
  public GroupRedundantInfoExtractor() {
  }

  public List<String> process(String originFile) throws Exception {
    LinkedHashMap<Integer, Boolean> noredundantMap = new BuildSummaryResultGroupInfoReader().read(originFile);

    String resultFile = originFile + ".groupinfo";
    PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
    try {
      pw.println("Group\tNoredundant");
      for(Integer group:noredundantMap.keySet()){
        pw.println(group + "\t" + noredundantMap.get(group));
      }
    }
    finally {
      pw.close();
    }
    return Arrays.asList(new String[] {resultFile});
  }

  public static void main(String[] args) throws Exception {
    new GroupRedundantInfoExtractor().process("X:\\summary\\mouse_liver\\summary\\all\\mouse_liver_all.proteins");
  }
}
