package cn.ac.rcpa.bio.tools.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultWriter;
import cn.ac.rcpa.bio.utils.AccessNumberUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class IdentifiedResultFilterByProteinId
    implements IFileProcessor {
  private String originFile;
  public static final String version = "1.0.1";

  public IdentifiedResultFilterByProteinId(String originFile) {
    this.originFile = originFile;
  }

  private BuildSummaryProteinGroup findGroupContainsProtein(List<BuildSummaryProteinGroup> groups,String upcasedProteinName){
    for (BuildSummaryProteinGroup group : groups) {
      final List<BuildSummaryProtein> proteins = group.getProteins();
      for (BuildSummaryProtein protein : proteins) {
        if (protein.getProteinName().toUpperCase().indexOf(upcasedProteinName) !=
            -1) {
          return group;
        }
      }
    }
    return null;
  }

  public List<String> process(String tokenFile) throws Exception {
    final List<String> proteinIds = Arrays.asList(AccessNumberUtils.
                                                  loadFromFile(tokenFile,false));

    final BuildSummaryResult origin = new BuildSummaryResultReader().read(
        originFile);
    BuildSummaryResult matched = (BuildSummaryResult) origin.clone();
    matched.clearProteinGroups();

    final List<String> missedProteinId = new ArrayList<String>();
    final List<BuildSummaryProteinGroup> groups = origin.getProteinGroups();
    for (String proteinId : proteinIds) {
      final String upcasedProteinId = proteinId.toUpperCase();
      final BuildSummaryProteinGroup findGroup = findGroupContainsProtein(groups, upcasedProteinId);
      if (findGroup == null){
        missedProteinId.add(proteinId);
      }
      else {
        matched.addProteinGroup(findGroup);
      }
    }

    matched.sort();

    String resultFile = tokenFile + ".proteins";
    BuildSummaryResultWriter.getInstance().write(resultFile, matched);

    final List<String> result = new ArrayList<String>(Arrays.asList(new String[] {resultFile}));
    if (missedProteinId.size() > 0){
      String missedFile = tokenFile + ".miss";
      RcpaFileUtils.writeFile(missedFile, missedProteinId.toArray(new String[0]));
      result.add(missedFile);
    }

    return result;
  }
}
