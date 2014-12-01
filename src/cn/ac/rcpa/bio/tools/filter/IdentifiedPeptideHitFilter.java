package cn.ac.rcpa.bio.tools.filter;

import java.io.IOException;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.sequest.SequestParseException;
import cn.ac.rcpa.filter.IFilter;

public class IdentifiedPeptideHitFilter {
  private IdentifiedPeptideHitFilter() {
  }

  private static void doProcess(String resultFilename,
                                IFilter<IIdentifiedPeptide> pepFilter,
                                List<BuildSummaryPeptideHit> peptides) throws
      IOException {
    for (int i = peptides.size() - 1; i >= 0; i--) {
      if (!pepFilter.accept(peptides.get(i).getPeptide(0))) {
        peptides.remove(i);
      }
    }

    new BuildSummaryPeptideHitWriter().write(resultFilename, peptides);
  }

  public static String processByPeptideFile(String filename,
                                            IFilter<IIdentifiedPeptide>
                                            pepFilter) throws Exception {
    final List<BuildSummaryPeptideHit> peptides = new BuildSummaryPeptideHitReader().read(filename);

    final String result = filename + "." + pepFilter.getType();
    doProcess(result, pepFilter, peptides);
    return result;
  }

  public static String processByProteinFile(String filename,
                                            IFilter<IIdentifiedPeptide>
                                            pepFilter) throws
      SequestParseException, IOException {
    final List<BuildSummaryPeptideHit> peptides = new BuildSummaryResultReader().
        readOnly(filename).getPeptideHits();

    final String result = filename + "." + pepFilter.getType() + ".peptides";
    doProcess(result, pepFilter, peptides);
    return result;
  }

}
