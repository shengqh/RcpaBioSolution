package cn.ac.rcpa.bio.tools.convert;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class FastaToAligmentCompatibleConverter implements IFileProcessor{
  public static final String version = "1.0.1";

  private SequenceDatabaseType dbType;

  public FastaToAligmentCompatibleConverter(SequenceDatabaseType dbType) {
    this.dbType = dbType;
  }

  public List<String> process(String originFile) throws Exception {
    List<Sequence> seqs = SequenceUtils.readFastaProteins(new File(originFile)) ;

    final String resultFile = RcpaFileUtils.changeExtension(originFile, ".align.fasta");
    PrintWriter pw = new PrintWriter(new FileWriter(resultFile));

    IAccessNumberParser acParser = AccessNumberParserFactory.getParser(dbType);
    for(Sequence seq:seqs){
      final String ac = acParser.getValue(seq.getName());
      final String description = SequenceUtils.getProteinReference(seq.getAnnotation().getProperty("description").toString().trim());
      final String newAc = (ac + "_" + description).replace(' ','_');
      pw.println(">" + newAc);
      pw.println(seq.seqString());
    }
    pw.close();
    return Arrays.asList(new String[]{resultFile});
  }

}
