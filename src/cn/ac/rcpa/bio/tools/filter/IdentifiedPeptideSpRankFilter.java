/*
 * Created on Jun 24, 2005
 */
package cn.ac.rcpa.bio.tools.filter;

import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;

public class IdentifiedPeptideSpRankFilter
    implements IFileProcessor {
  public static final String version = "1.0.0";

  private int maxSpRank;

  public IdentifiedPeptideSpRankFilter(int maxSpRank) {
    this.maxSpRank = maxSpRank;
  }

  public static void main(String[] args) throws Exception {
    String filename =
        "/Users/sqh/Science/data/HLPP/cnhlpp/2D12_3D1_1-2/cnhlpp_2d12_3d1_1-2.peptides";
    new IdentifiedPeptideSpRankFilter(4).process(filename);
  }

  public List<String> process(String originFile) throws Exception {
    String result = originFile + ".sp" + maxSpRank;

    List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader().read(originFile);
    for (int i = pephits.size() - 1; i >= 0; i--) {
      BuildSummaryPeptide peptide = pephits.get(i).
          getPeptide(0);
      if (peptide.getSpRank() > maxSpRank) {
        pephits.remove(i);
      }
    }

    new BuildSummaryPeptideHitWriter().write(result, pephits);
    return Arrays.asList(new String[] {result});
  }
}
