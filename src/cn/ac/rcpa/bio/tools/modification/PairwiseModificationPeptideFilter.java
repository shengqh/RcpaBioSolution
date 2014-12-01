package cn.ac.rcpa.bio.tools.modification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideModificationFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;
import cn.ac.rcpa.bio.proteomics.utils.IdentifiedResultUtils;
import cn.ac.rcpa.filter.IFilter;

public class PairwiseModificationPeptideFilter implements IFileProcessor{
  public static String version = "1.0.1";
  private String modifiedAminoacids;

  public PairwiseModificationPeptideFilter(String modifiedAminoacids) {
    this.modifiedAminoacids = modifiedAminoacids;
  }

  /**
   * 读入peptide文件，判断里面的肽段是否有成对出现的修饰肽段，
   * 也就是说，有修饰肽段，同时又有没有修饰的肽段。
   *
   * @param originFile String
   * @return String
   * @throws Exception
   */
  public List<String> process(String originFile) throws Exception {
    final String resultFile = originFile + ".pairwise_modified";

    final List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader().read(originFile);

    final Map<String,List<BuildSummaryPeptideHit>> scanPeptideHitMap =
        IdentifiedResultUtils.get_PureSequence_PeptideHit_Map(pephits);

    final IFilter<IIdentifiedPeptide> pepFilter = new IdentifiedPeptideModificationFilter(modifiedAminoacids, 0, false);

    final List<BuildSummaryPeptideHit> pairwisePeptides = getPairwiseModifiedPeptides(scanPeptideHitMap, pepFilter);

    new BuildSummaryPeptideHitWriter().write(resultFile, pairwisePeptides);

    return Arrays.asList(new String[]{resultFile});
  }

  private List<BuildSummaryPeptideHit> getPairwiseModifiedPeptides(Map<
      String, List<BuildSummaryPeptideHit>> scanPeptideHitMap,
      IFilter<IIdentifiedPeptide> pepFilter) {
    final List<BuildSummaryPeptideHit> pairwisePeptides = new ArrayList<BuildSummaryPeptideHit>();
    for(List<BuildSummaryPeptideHit> peptideHitList:scanPeptideHitMap.values()){
      boolean bModified = false;
      boolean bUnmodified = false;
      for(IIdentifiedPeptideHit pephit:peptideHitList){
        if (pepFilter.accept(pephit.getPeptide(0))){
          bModified = true;
        }else{
          bUnmodified = true;
        }
      }

      if (bModified && bUnmodified){
        pairwisePeptides.addAll(peptideHitList);
      }
    }
    return pairwisePeptides;
  }

  public static void main(String[] args) throws Exception{
    final String file = "F:\\Science\\Data\\jwh\\phospho\\Stringent_all_RP_SAX_Phospho.noredundant.peptides";
    System.out.println(new PairwiseModificationPeptideFilter("STY").process(file));
  }

}
