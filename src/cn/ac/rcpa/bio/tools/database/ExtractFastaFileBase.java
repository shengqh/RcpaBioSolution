package cn.ac.rcpa.bio.tools.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.biojava.bio.seq.Sequence;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ExtractFastaFileBase implements IFileProcessor{
  private IFilter<Sequence> seqFilter;
  private String resultFile;
  public ExtractFastaFileBase(IFilter<Sequence> seqFilter) {
    this.seqFilter = seqFilter;
  }

  public ExtractFastaFileBase(IFilter<Sequence> seqFilter, String resultFile) {
    this.seqFilter = seqFilter;
    this.resultFile = resultFile;
  }

  public List<String> process(String originFile) throws Exception {
    final List<Sequence> accepted = SequenceUtils.readFastaProteins(new File(originFile), seqFilter);

    final String saveFile = resultFile != null ? resultFile : RcpaFileUtils.changeExtension(originFile,"") + "." + seqFilter.getType() + ".fasta";
    SequenceUtils.writeFasta(new File(saveFile), accepted);

    return Arrays.asList(new String[]{saveFile});
  }
}
