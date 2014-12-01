package cn.ac.rcpa.bio.tools.filter;

import java.io.IOException;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.sequest.SequestParseException;
import cn.ac.rcpa.utils.RcpaFileUtils;

/**
 * <p>Title: Unique1GroupCorrespondingPeptideExtractor </p>
 *
 * <p>Description: Extract the peptide only exists in group which having only 1
 * unique peptide</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: RCPA.SIBS.AC.CN</p>
 * @author Sheng QuanHu
 * @version 1.0
 */
public class Unique1GroupCorrespondingPeptideExtractor {
  public Unique1GroupCorrespondingPeptideExtractor() {
  }

  public static void main(String[] args) throws IOException,
      SequestParseException {
    String resultFile = "F:\\Science\\Data\\HIPP\\summary\\ipi.RAT.3.04\\hippocampi_2d\\hippocampi_2d.noredundant";
//    String resultFile = "F:\\Science\\Data\\HPPP\\2DLC_Micro_LTQ\\1.9_2.2_3.75_0.1_4\\2D_LTQ_HPPP.noredundant";
    BuildSummaryResult ir = IdentifiedResultIOFactory.readBuildSummaryResult(resultFile);
    List<BuildSummaryProteinGroup> groups = ir.getProteinGroups();
    ir.clearProteinGroups();
    for(BuildSummaryProteinGroup group:groups){
      if (group.getProtein(0).getUniquePeptides().length == 1){
        ir.addProteinGroup(group);
      }
    }

    String peptideFile = RcpaFileUtils.changeExtension(resultFile,"unique1.peptides");
    IdentifiedResultIOFactory.writeBuildSummaryPeptideHit(peptideFile, ir.getPeptideHits());
  }
}
