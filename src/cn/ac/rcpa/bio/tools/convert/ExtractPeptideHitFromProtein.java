package cn.ac.rcpa.bio.tools.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.comparison.IdentifiedPeptideHitComparator;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitWriter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;

public class ExtractPeptideHitFromProtein
    implements IFileProcessor {
  public static final String version = "1.0.0";
  private static ExtractPeptideHitFromProtein instance;

  public static ExtractPeptideHitFromProtein getInstance() {
    if (instance == null) {
      instance = new ExtractPeptideHitFromProtein();
    }
    return instance;
  }

  private ExtractPeptideHitFromProtein() {
  }

  public List<String> process(String proteinFilename) throws Exception {
    List<BuildSummaryPeptideHit> pephits =
        new BuildSummaryResultReader().readOnly(proteinFilename).getPeptideHits();

    Collections.sort(pephits, IdentifiedPeptideHitComparator.getInstance());

    final List<String> result = new ArrayList<String>();

    final String resultFile = proteinFilename + ".peptides";

    new BuildSummaryPeptideHitWriter().write(resultFile, pephits);

    result.add(resultFile);

    return result;
  }
}
