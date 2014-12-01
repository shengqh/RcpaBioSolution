package cn.ac.rcpa.bio.tools.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedProteinGroup;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupPeptideCountFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupPeptideUnremoveFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.filter.impl.PeptideXCorrFilter;
import cn.ac.rcpa.bio.sequest.SequestParseException;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.filter.NotFilter;
import cn.ac.rcpa.filter.OrFilter;

public class IdentifiedResultXCorrFilter {
  public IdentifiedResultXCorrFilter() {
  }

  public static void main(String[] args) throws IOException,
      SequestParseException {
    // String file =
    // "\\\\192.168.88.249\\work\\DAIJIE\\PG_ML\\summary\\ML_S_SUMMARY2\\ML_S.noredundant";
    String[] files = new String[] {
        "\\\\192.168.88.249\\work\\DAIJIE\\Peigang_QB1_Total\\Total.noredundant"};
    for (String file : files) {
      IFilter<IIdentifiedProteinGroup> pepCountFilter = new IdentifiedProteinGroupPeptideCountFilter(
          2, null);

      IFilter<IIdentifiedPeptide> pepXCorrFilter = new PeptideXCorrFilter(
          new double[] { 1.9, 2.5, 3.75 });
      IFilter<IIdentifiedProteinGroup> groupXCorrFilter = new IdentifiedProteinGroupPeptideUnremoveFilter(
          pepXCorrFilter, 1);

      List<IFilter<IIdentifiedProteinGroup>> groupFilters = new ArrayList<IFilter<IIdentifiedProteinGroup>>();
      groupFilters.add(pepCountFilter);
      groupFilters.add(groupXCorrFilter);

      OrFilter<IIdentifiedProteinGroup> groupFilter = new OrFilter<IIdentifiedProteinGroup>(
          groupFilters);

      IdentifiedResultFilter.processByGroupFilter(file, groupFilter);
      IdentifiedResultFilter.processByGroupFilter(file,
          new NotFilter<IIdentifiedProteinGroup>(groupFilter));
    }
  }
}
