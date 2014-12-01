package cn.ac.rcpa.bio.tools.convert;

import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BioworkExcelReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultWriter;
import cn.ac.rcpa.bio.proteomics.utils.IdentifiedResultUtils;

public class Bioworks2BuildSummary implements IFileProcessor{
  private String database;
  public Bioworks2BuildSummary(String database) {
    this.database = database;
  }

  public List<String> process(String originFile) throws Exception {
    final BioworkExcelReader reader = BioworkExcelReader.getInstance();
    final BuildSummaryResult ir = reader.read(originFile);
    final String resultFile = originFile + ".proteins";
    if (database != null){
      IdentifiedResultUtils.fillSequenceByName(ir.getProteins(), database);
    }
    BuildSummaryResultWriter.getInstance().write(resultFile, ir);
    return Arrays.asList(new String[]{resultFile});
  }

  public static void main(String[] args) throws Exception {
    final String[] files = {"F:\\Science\\Data\\daijie\\peptide_PI\\EGFA431_pH45start.xls",
        "F:\\Science\\Data\\daijie\\peptide_PI\\EGFA431_NB_lowflow.xls"
    };
    for(String file :files){
//      new Bioworks2BuildSummary("d:\\database\\uniprot_sprot_human.fasta").process(file);
      new Bioworks2BuildSummary(null).process(file);
    }
  }
}
