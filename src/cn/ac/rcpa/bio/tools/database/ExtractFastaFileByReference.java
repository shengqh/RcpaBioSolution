package cn.ac.rcpa.bio.tools.database;

import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.filter.SequenceReferenceFilter;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ExtractFastaFileByReference implements IFileProcessor{
  public static final String version = "1.0.0";

  private String databaseFile;

  public ExtractFastaFileByReference(String databaseFile) {
    this.databaseFile = databaseFile;
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 2){
      System.out.println(ExtractFastaFileByReference.class.getName() + " databaseFile referenceFile");
      return;
    }

    final List<String> resultFiles = new ExtractFastaFileByReference(args[0]).process(args[1]);
    System.out.println("Result saved to " + resultFiles);
  }

  public List<String> process(String tokenFile) throws Exception {
    final String[] tokens = RcpaFileUtils.readFile(tokenFile, true);
    final String resultFile = tokenFile + ".fasta";
    final IFilter<Sequence> seqFilter = new SequenceReferenceFilter(Arrays.asList(tokens));
    final List<String> result = new ExtractFastaFileBase(seqFilter, resultFile).process(databaseFile);
    return result;
  }
}
