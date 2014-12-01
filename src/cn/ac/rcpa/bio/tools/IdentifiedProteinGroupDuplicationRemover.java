package cn.ac.rcpa.bio.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinNameFilter;
import cn.ac.rcpa.bio.proteomics.processor.IdentifiedProteinGroupRemainProteinByFilterProcessor;
import cn.ac.rcpa.bio.proteomics.processor.IdentifiedProteinGroupRemainProteinBySequenceLengthProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.processor.IProcessor;

public class IdentifiedProteinGroupDuplicationRemover {
  public IdentifiedProteinGroupDuplicationRemover() {
  }

  public static void main(String[] args) throws RcpaParseException, IOException {
    final String file =
        "F:\\Science\\Data\\HPPP\\BeiJing\\Chinese Sera.noredundant";
    final String resultfile =
        "F:\\Science\\Data\\HPPP\\BeiJing\\Chinese Sera.noredundant.unduplicated";
    BuildSummaryResult result = IdentifiedResultIOFactory.readBuildSummaryResult(file);

    HashMap<HashSet<String>, Integer> statistic = new HashMap<HashSet<String>,
        Integer> ();

    IProcessor<BuildSummaryProteinGroup> swProcessor = new
        IdentifiedProteinGroupRemainProteinByFilterProcessor<BuildSummaryPeptide, BuildSummaryPeptideHit, BuildSummaryProtein, BuildSummaryProteinGroup>(new
        IdentifiedProteinNameFilter("SWISS-PROT"));

    IProcessor<BuildSummaryProteinGroup> refProcessor = new
        IdentifiedProteinGroupRemainProteinByFilterProcessor<BuildSummaryPeptide, BuildSummaryPeptideHit, BuildSummaryProtein, BuildSummaryProteinGroup>(new
        IdentifiedProteinNameFilter("REFSEQ_NP"));

    IProcessor<BuildSummaryProteinGroup> lengthProcessor = new
        IdentifiedProteinGroupRemainProteinBySequenceLengthProcessor<BuildSummaryProteinGroup>(true);

    for (int i = 0; i < result.getProteinGroupCount(); i++) {
      HashSet<String> key = new HashSet<String> ();
      BuildSummaryProteinGroup group = result.getProteinGroup(i);
      if (group.getProteinCount() == 1) {
        key.add("UnduplicatedGroup");
      }

      if (swProcessor.process(group).size() > 0) {
        key.add("SWISS-PROT");
      }

      if (refProcessor.process(group).size() > 0) {
        key.add("REFSEQ_NP");
      }

      if (lengthProcessor.process(group).size() > 0) {
        key.add("SequenceLength");
      }

      increase(statistic, key);
    }

    ArrayList<HashSet<String>>
        keys = new ArrayList<HashSet<String>> (statistic.keySet());
    Collections.sort(keys, new Comparator<HashSet<String>> () {
      @Override
      public boolean equals(Object obj) {
        return false;
      }

      public int compare(HashSet<String> set1, HashSet<String> set2) {
        return set1.size() - set2.size();
      }
    });

    for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
      Object item = (Object) iter.next();
      System.out.println(item + "\t" + statistic.get(item));
    }

    IdentifiedResultIOFactory.writeBuildSummaryResult(resultfile, result);
  }

  static private void increase(HashMap<HashSet<String>, Integer> statistic,
                               HashSet<String> key) {
    if (statistic.containsKey(key)) {
      statistic.put(key, statistic.get(key) + 1);
    }
    else {
      statistic.put(key, 1);
    }
  }
}
