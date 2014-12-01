package cn.ac.rcpa.bio.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.bio.sequest.SequestParseException;

public class ProteinPeptideMapBuilder {
  public ProteinPeptideMapBuilder() {
  }

  public static void main(String[] args) throws IOException,
      SequestParseException {
    String unduplidatedFile = "data/HIPP2.noredundant.unduplicated";
    if(args.length > 0){
      unduplidatedFile = args[0];
    }

    if (!new File(unduplidatedFile).exists()){
      printPrompt();
    }

    SequenceDatabaseType dbType = SequenceDatabaseType.IPI;

    build(unduplidatedFile, dbType);
  }

  private static void build(String unduplidatedFile, SequenceDatabaseType dbType) throws
      IOException, SequestParseException {
    BuildSummaryResult ir = IdentifiedResultIOFactory.readBuildSummaryResult(unduplidatedFile);
    Set<String> purePeptides = getPurePeptidesFromPeptideHit(ir.getPeptideHits());

    IAccessNumberParser acParser = AccessNumberParserFactory.getParser(dbType);

    PrintWriter pw = new PrintWriter(new FileWriter(unduplidatedFile + ".map"));

    for(String peptide:purePeptides){
      pw.print("\t" + peptide);
    }
    pw.println();

    List<BuildSummaryProtein> proteins = ir.getProteins();
    for(BuildSummaryProtein protein:proteins){
      List<BuildSummaryPeptide> currPeptides = protein.getPeptides();
      Set<String> pureCurrPeptides = getPurePeptidesFromPeptide(currPeptides);

      pw.print(acParser.getValue(protein.getProteinName()));
      for(String peptide:purePeptides){
        if (pureCurrPeptides.contains(peptide)){
          pw.print("\t1");
        }
        else{
          pw.print("\t0");
        }
      }
      pw.println();
    }
    pw.close();
  }

  private static Set<String> getPurePeptidesFromPeptideHit(List<? extends IIdentifiedPeptideHit> peptides) {
    Set<String> result = new HashSet<String>();
    for(IIdentifiedPeptideHit peptide:peptides){
      result.add(PeptideUtils.getPurePeptideSequence(peptide.getPeptide(0).getSequence()));
    }
    return result;
  }

  private static Set<String> getPurePeptidesFromPeptide(List<? extends IIdentifiedPeptide> peptides) {
    Set<String> result = new HashSet<String>();
    for(IIdentifiedPeptide peptide:peptides){
      result.add(PeptideUtils.getPurePeptideSequence(peptide.getSequence()));
    }
    return result;
  }

  private static void printPrompt() {
    System.err.println(ClassUtils.getShortClassName(ProteinPeptideMapBuilder.class) + " BuildSummary_unduplicated_file");
  }
}
