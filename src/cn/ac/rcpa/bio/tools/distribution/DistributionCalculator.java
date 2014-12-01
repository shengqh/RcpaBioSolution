package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.proteomics.CountMap;
import cn.ac.rcpa.bio.proteomics.DistributionResultMap;
import cn.ac.rcpa.bio.proteomics.IDistributionReader;
import cn.ac.rcpa.bio.proteomics.IDistributionResultMapWriter;
import cn.ac.rcpa.bio.proteomics.filter.PeptideMinCountFilter;
import cn.ac.rcpa.bio.proteomics.filter.PeptideMinExistCountFilter;
import cn.ac.rcpa.bio.proteomics.impl.PeptideDistributionResultMapWriter;
import cn.ac.rcpa.bio.proteomics.impl.ProteinDistributionResultMapWriter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitExperimentalReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultExperimentalReader;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType;
import cn.ac.rcpa.filter.IFilter;

public class DistributionCalculator {
  private DistributionOption option;
  private IDistributionReader reader;
  private IDistributionResultMapWriter writer;
  private SequenceDatabaseType dbType;

  public DistributionCalculator() {
  }

  public void process(File optionFile) throws Exception {
    option = (DistributionOption) DistributionOption.unmarshal(new FileReader(
        optionFile));

    dbType = SequenceDatabaseType.valueOf(option.getDatabaseType());

    final File sourceFile = new File(option.getSourceFile().getFileName());
    if (option.getDistributionType() == DistributionType.PROTEIN) {
      reader = BuildSummaryResultExperimentalReader.getInstance();
      writer = new ProteinDistributionResultMapWriter(dbType);
    }
    else {
      reader = BuildSummaryPeptideHitExperimentalReader.getInstance();
      writer = new PeptideDistributionResultMapWriter();
    }

    final DistributionResultMap map = reader.getExperimentalMap(
        sourceFile.
        getAbsolutePath());
    final Map<String, String> experimentalClassifiedNameMap =
        DistributionOptionUtils.getExperimentalClassifiedNamesMap(option);

    map.classify(experimentalClassifiedNameMap);

    String prefix = new File(optionFile.getParent(),
                             sourceFile.getName() + "." +
                             option.getDistributionType() + "_" +
                             option.getClassificationInfo().
                             getClassificationPrinciple()).getAbsolutePath();
    saveResult(prefix, map);
  }

  private void saveResult(String prefix, DistributionResultMap map) throws
      IOException {
    writer.write(prefix + ".distribution", map);

    List<String> classifiedNames = map.getClassifiedNames();
    for (int i = 5; i <= 20; i += 5) {
      IFilter<CountMap<String>> countFilter = new PeptideMinCountFilter(i, 20);
      DistributionResultMap countFilteredMap = map.filter(countFilter);
      for (int j = 1; j <= classifiedNames.size(); j++) {
        IFilter<CountMap<String>>
            existFilter = new PeptideMinExistCountFilter(j,
            classifiedNames.size());
        DistributionResultMap existFilteredMap = countFilteredMap.filter(
            existFilter);
        final String resultFilename = prefix + "." +
            countFilter.getType() +
            "." + existFilter.getType() + ".distribution";
        writer.write(resultFilename, existFilteredMap);
      }
    }
  }

  public static void main(String[] args) throws Exception {
//    File optionFile = new File("X:\\summary\\mouse_liver\\profile\\Peptide_METHOD_CLASSIFICATION\\mouse_liver_profile.peptides.Peptide_METHOD.statistic.xml");
    File optionFile = new File("X:\\summary\\mouse_liver\\profile\\Protein_METHOD_CLASSIFICATION\\mouse_liver_profile.proteins.Protein_METHOD.statistic.xml");
//    File optionFile = new File("F:\\Science\\Data\\HPPP\\2DLC_Micro_LCQ\\1.9_2.2_3.75_0.1_4\\Peptide_METHOD_CLASSIFICATION\\2DLC_HPPP_Serum.noredundant.Peptide_METHOD.statistic.xml");
    new DistributionCalculator().process(optionFile);
  }
}
