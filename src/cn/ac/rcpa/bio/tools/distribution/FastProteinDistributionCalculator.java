package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.impl.MergedAccessNumberParser;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.DistributionResultMap;
import cn.ac.rcpa.bio.proteomics.StringPeptideMap;
import cn.ac.rcpa.bio.proteomics.filter.DistributionResultMapPepCountFilter;
import cn.ac.rcpa.bio.proteomics.filter.DistributionResultMapUniPepCountFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultExperimentalReader;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.bio.tools.distribution.option.types.FilterType;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.RcpaStringUtils;

public class FastProteinDistributionCalculator
    implements IFileProcessor {
  public static final String version = "1.0.0";

  private DistributionOption option;

  private File resultDir;
  private File sourceFile;
  private Map<String, String> experimentalClassifiedNamesMap;
  private SequenceDatabaseType dbType;
  private int maxPeptideCountWidth;

  public FastProteinDistributionCalculator() {
  }

  public List<String> process(String optionFile) throws Exception {
    init(optionFile);

    StringPeptideMap map = BuildSummaryResultExperimentalReader.getInstance().
        getExperimentalDetailMap(sourceFile.getAbsolutePath());

    map.classify(experimentalClassifiedNamesMap);

    List<String> result = new ArrayList<String> ();
    for (int iMinCount = option.getFilterByPeptide().getFrom();
         iMinCount <= option.getFilterByPeptide().getTo();
         iMinCount += option.getFilterByPeptide().getStep()) {
      File resultFile = new File(
          resultDir, sourceFile.getName() + "." +
          option.getClassificationInfo().getClassificationPrinciple() +
          "." + RcpaStringUtils.intToString(iMinCount, maxPeptideCountWidth) +
          ".distribution");

      MergedAccessNumberParser parser = new MergedAccessNumberParser(
          AccessNumberParserFactory.getParser(dbType), " ! ");

      IFilter<DistributionResultMap> filter;
      if (option.getFilterByPeptide().getFilterType() == FilterType.UNIQUEPEPTIDECOUNT){
        filter = new DistributionResultMapUniPepCountFilter(experimentalClassifiedNamesMap.values(), iMinCount);
      }
      else {
        filter = new DistributionResultMapPepCountFilter(experimentalClassifiedNamesMap.values(), iMinCount);
      }

      final String uniPepCountResultFile = resultFile.getAbsolutePath() + ".UniPepCount";
      PrintWriter pwUniPepCount = new PrintWriter(new FileWriter(uniPepCountResultFile));
      map.write(pwUniPepCount, parser, filter,true);
      pwUniPepCount.close();
      result.add(uniPepCountResultFile);

      final String pepCountResultFile = resultFile.getAbsolutePath() + ".PepCount";
      PrintWriter pwPepCount = new PrintWriter(new FileWriter(pepCountResultFile));
      map.write(pwPepCount, parser, filter,false);
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

    experimentalClassifiedNamesMap = DistributionOptionUtils.
        getExperimentalClassifiedNamesMap(option);

    dbType = SequenceDatabaseType.valueOf(option.getDatabaseType());

    maxPeptideCountWidth = DistributionOptionUtils
        .getMaxPeptideCountWidth(option);
  }

  public static void main(String[] args) throws Exception {
    String optionFile = "F:/Science/Data/HPPP/2DLC_Micro_LCQ/1.9_2.2_3.75_0.1_4/Protein_ABUNDANCE_CLASSIFICATION/2DLC_HPPP_Serum.noredundant.Protein_ABUNDANCE.statistic.xml";
    System.out.println(new FastProteinDistributionCalculator().process(
        optionFile));
  }

}
