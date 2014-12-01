package cn.ac.rcpa.bio.tools.modification;

import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideModificationFilter;
import cn.ac.rcpa.bio.tools.filter.IdentifiedPeptideHitFilter;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.filter.NotFilter;

public class BuildSummaryPeptideHitModificationFilter
    implements IFileProcessor{
  public static String version = "1.0.0";
  
	private String modifiedAminoacids;
  public BuildSummaryPeptideHitModificationFilter(String modifiedAminoacids) {
    this.modifiedAminoacids = modifiedAminoacids;
  }

  public static void main(String[] args) throws Exception {
    final String file = "F:\\Science\\Data\\zhouhu\\peptidefilter\\test.noredundant";

    new BuildSummaryPeptideHitModificationFilter("STY").process(file);
  }

  public List<String> process(String originFile) throws Exception {
    final IFilter<IIdentifiedPeptide> pepFilter = new IdentifiedPeptideModificationFilter(modifiedAminoacids, 0, false);

    List<String> result = new ArrayList<String>();
    String modifiedFile = IdentifiedPeptideHitFilter.processByPeptideFile(originFile, pepFilter);
    result.add(modifiedFile);
    result.add(IdentifiedPeptideHitFilter.processByPeptideFile(originFile, new NotFilter<IIdentifiedPeptide>( pepFilter)));

    result.add(IdentifiedPeptideHitFilter.processByPeptideFile(modifiedFile, new IdentifiedPeptideModificationFilter(modifiedAminoacids, 1, true)));
    result.add(IdentifiedPeptideHitFilter.processByPeptideFile(modifiedFile, new IdentifiedPeptideModificationFilter(modifiedAminoacids, 2, true)));
    result.add(IdentifiedPeptideHitFilter.processByPeptideFile(modifiedFile, new IdentifiedPeptideModificationFilter(modifiedAminoacids, 3, false)));

    return result;
  }
}
