package cn.ac.rcpa.bio.tools.modification;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideHitFilterByPeptideFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideModificationFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;
import cn.ac.rcpa.bio.proteomics.utils.IdentifiedPeptideHitUtils;
import cn.ac.rcpa.bio.proteomics.utils.IdentifiedResultUtils;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.filter.IFilter;

public class PairwiseDifferentModificationPeptideFilter
    implements IFileProcessor {
  public static String version = "1.0.1";

  private String modifiedAminoacids;

  public PairwiseDifferentModificationPeptideFilter(String modifiedAminoacids) {
    this.modifiedAminoacids = modifiedAminoacids;
  }

  /**
   * 读入peptide文件，判断里面的肽段是否有成对出现的不同修饰肽段，
   * 也就是说，同样的肽段，有两个不同的修饰位点。
   *
   * @param originFile String
   * @return List
   * @throws Exception
   */
  public List<String> process(String originFile) throws Exception {
    final String resultFile = originFile + ".pairwise_diff_modified_site";

    final List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader().read(originFile);

    BuildSummaryPeptideHitWriter writer = new BuildSummaryPeptideHitWriter();

    final IFilter<IIdentifiedPeptideHit> pepFilter = new
        IdentifiedPeptideHitFilterByPeptideFilter(new
                                                  IdentifiedPeptideModificationFilter(
        modifiedAminoacids, 0, false));

    final List<BuildSummaryPeptideHit> modifiedPeptides =
        IdentifiedPeptideHitUtils.filter(pephits, pepFilter);

    final Map<String,
        List<BuildSummaryPeptideHit>> scanPeptideHitMap =
        IdentifiedResultUtils.get_PureSequence_PeptideHit_Map(modifiedPeptides);

    final Map<String,
        List<BuildSummaryPeptideHit>> diffMap =
        getDiffModifiedSiteSequencePeptideHitMap(scanPeptideHitMap);

    PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
    try {
      int peptideCount = 0;
      List<String> keys = new ArrayList<String> (diffMap.keySet());
      Collections.sort(keys);
      for (String key : keys) {
        pw.println(key);
        writer.writeItems(pw, diffMap.get(key));
        peptideCount += diffMap.get(key).size();
      }

      pw.println();
      pw.println("Peptide count: " + peptideCount);
      pw.println("Different modification site peptide pair count: " + diffMap.size());
    }
    finally {
      pw.close();
    }

    return Arrays.asList(new String[] {resultFile});
  }

  final Map<String, List<BuildSummaryPeptideHit>>
      getDiffModifiedSiteSequencePeptideHitMap(Map<String,
                                               List<BuildSummaryPeptideHit>>
                                               scanPeptideHitMap) {
    Map<String, List<BuildSummaryPeptideHit>> result = new HashMap<String,
        List<BuildSummaryPeptideHit>> ();
    List<String> keys = new ArrayList<String> (scanPeptideHitMap.keySet());
    Collections.sort(keys);
    for (String key : keys) {
      List<BuildSummaryPeptideHit> peps = scanPeptideHitMap.get(key);
      HashSet<String> modifiedPeptide = new HashSet<String> ();
      for (BuildSummaryPeptideHit pep : peps) {
        modifiedPeptide.add(PeptideUtils.getSpecialModifiedPeptideSequence(pep.
            getPeptide(0).getSequence(), modifiedAminoacids));
      }
      if (modifiedPeptide.size() > 1) {
        List<String> seqs = new ArrayList<String> (modifiedPeptide);
        Collections.sort(seqs);
        result.put(seqs.toString(), peps);
      }
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    final String file =
        "F:\\Science\\Data\\jwh\\phospho\\All.noredundant.Modified_STY.peptides";
    System.out.println(new PairwiseDifferentModificationPeptideFilter("STY").
                       process(file));
  }

}
