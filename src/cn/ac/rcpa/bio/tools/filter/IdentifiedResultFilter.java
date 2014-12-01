package cn.ac.rcpa.bio.tools.filter;

import java.io.IOException;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedProteinGroup;
import cn.ac.rcpa.bio.proteomics.IIdentifiedResult;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupPeptideHitRemoverFilter;
import cn.ac.rcpa.bio.proteomics.processor.IdentifiedResultGroupFilterProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.sequest.SequestParseException;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.processor.IProcessor;

public class IdentifiedResultFilter {
  private IdentifiedResultFilter() {
  }

  public static String processByPeptideFilter(String file,
                                              IFilter<IIdentifiedPeptide>
                                              pepFilter) throws IOException,
      SequestParseException {
    final IFilter<IIdentifiedProteinGroup> groupFilter = new
        IdentifiedProteinGroupPeptideHitRemoverFilter(
            pepFilter);

    return processByGroupFilter(file, groupFilter);
  }

  public static String processByGroupFilter(String file,
                                            IFilter<IIdentifiedProteinGroup>
                                            groupFilter) throws IOException,
      SequestParseException {
    BuildSummaryResult ir = IdentifiedResultIOFactory
        .readBuildSummaryResult(file);

    return processByGroupFilter(ir, file, groupFilter);
  }

  public static String processByGroupFilter(BuildSummaryResult ir,
                                            String originResultFilename,
                                            IFilter<IIdentifiedProteinGroup>
                                            groupFilter) throws IOException {
    final IProcessor<IIdentifiedResult> resultProcessor = new
        IdentifiedResultGroupFilterProcessor(
            groupFilter);

    System.out.println("Processing " + originResultFilename +
                       " with filter ..."
                       + groupFilter.getType());

    resultProcessor.process(ir);

    System.out.println("Writing result ...");

    final String result = originResultFilename + "." + groupFilter.getType();
    IdentifiedResultIOFactory.writeBuildSummaryResult(result, ir);

    System.out.println("Finished!");
    return result;
  }
}
