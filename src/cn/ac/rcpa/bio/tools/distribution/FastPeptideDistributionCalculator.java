package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.CountMap;
import cn.ac.rcpa.bio.proteomics.DistributionResultMap;
import cn.ac.rcpa.bio.proteomics.filter.PeptideMinCountFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitExperimentalReader;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.filter.IFilter;

public class FastPeptideDistributionCalculator implements IFileProcessor {
  public static final String version = "1.0.0";

  private DistributionOption option;

  private File resultDir;

  private File sourceFile;

  private Map<String, String> experimentalClassifiedNamesMap;

  private int maxPeptideCountWidth;

  public FastPeptideDistributionCalculator() {
  }

  public List<String> process(String optionFile) throws Exception {
    init(optionFile);

    DistributionResultMap map = BuildSummaryPeptideHitExperimentalReader
        .getInstance().getExperimentalMap(sourceFile.getAbsolutePath());

    map.classify(experimentalClassifiedNamesMap);

    List<String> result = new ArrayList<String>();
    for (int iMinCount = option.getFilterByPeptide().getFrom(); iMinCount <= option
        .getFilterByPeptide().getTo(); iMinCount += option.getFilterByPeptide()
        .getStep()) {
      File resultFile = new File(resultDir, sourceFile.getName()
          + "."
          + option.getClassificationInfo().getClassificationPrinciple()
          + "."
          + StringUtils.leftPad(Integer.toString(iMinCount),
              maxPeptideCountWidth, '0') + ".distribution");

      IFilter<CountMap<String>> filter = new PeptideMinCountFilter(iMinCount,
          option.getFilterByPeptide().getTo());

      final String pepCountResultFile = resultFile.getAbsolutePath()
          + ".PepCount";
      PrintWriter pwPepCount = new PrintWriter(new FileWriter(
          pepCountResultFile));
      map.write(pwPepCount, filter);
      pwPepCount.close();
      result.add(pepCountResultFile);
    }
    return result;
  }

  protected void init(String optionFile) throws Exception {
    option = (DistributionOption) DistributionOption.unmarshal(new FileReader(
        optionFile));

    resultDir = new File(optionFile).getParentFile();

    sourceFile = new File(option.getSourceFile().getFileName());

    experimentalClassifiedNamesMap = DistributionOptionUtils
        .getExperimentalClassifiedNamesMap(option);

    maxPeptideCountWidth = DistributionOptionUtils
        .getMaxPeptideCountWidth(option);
  }
}
