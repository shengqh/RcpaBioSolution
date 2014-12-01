package cn.ac.rcpa.bio.tools.convert;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.database.ebi.protein.ProteinEntryParser;
import cn.ac.rcpa.bio.database.ebi.protein.entry.ProteinEntry;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.RcpaStringUtils;

public class UniprotDat2Fasta
    implements IFileProcessor {
  public static final String version = "1.0.0";

  public UniprotDat2Fasta() {
  }

  public List<String> process(String originFile) throws Exception {
    final ProteinEntryParser parser = new ProteinEntryParser();
    final String resultFile = RcpaFileUtils.changeExtension(originFile, ".fasta");
    final PrintWriter fastaWriter = new PrintWriter(new FileWriter(resultFile));

    parser.open(originFile);
    while (parser.hasNext()) {
      ProteinEntry entry = parser.getNextEntry();
      fastaWriter.println(">" + entry.getEntry_name() + " (" +
                          entry.getAc_number(0) + ") " + entry.getDescription());
      fastaWriter.println(RcpaStringUtils.warpString(entry.getSequence(), 70));
    }
    fastaWriter.close();

    return Arrays.asList(new String[] {resultFile});
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0){
      System.out.println(UniprotDat2Fasta.class.getName() + " UniprotDatFile");
    }
  }
}
