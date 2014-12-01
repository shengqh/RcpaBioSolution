/*
 * Created on 2005-3-25
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools;

import java.io.IOException;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultWriter;
import cn.ac.rcpa.bio.sequest.SequestParseException;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class SplitModifiedPeptides {
  private static boolean isModified(String sequence) {
    for (int j = 0; j < sequence.length(); j++) {
      char aa = sequence.charAt(j);
      if (aa != '.' && aa != '-' && (aa < 'A' || aa > 'Z')) {
        return true;
      }
    }

    return false;
  }

  public static void main(String[] args) throws IOException,
      SequestParseException {
    for (String file : args) {
      writeModifiedOnlyFile(file);
      writeUnmodifiedOnlyFile(file);
      writeModifiedFile(file);
      writeUnmodifiedFile(file);
    }
  }

  private static void writeModifiedOnlyFile(String file) throws IOException,
      SequestParseException {
    BuildSummaryResult ir = new BuildSummaryResultReader().read(file);
    for (int i = ir.getProteinGroupCount() - 1; i >= 0; i--) {
      List<BuildSummaryPeptideHit> peptides = ir.getProteinGroup(i).getPeptideHits();
      for (IIdentifiedPeptideHit peptide : peptides) {
        if (!isModified(peptide.getPeptide(0).getSequence())) {
          ir.removeProteinGroup(i);
          break;
        }
      }
    }
    String outputfile = RcpaFileUtils.changeExtension(file, "modifiedonly");
    BuildSummaryResultWriter.getInstance().write(outputfile, ir);
  }

  private static void writeUnmodifiedOnlyFile(String file) throws IOException,
      SequestParseException {
    BuildSummaryResult ir = new BuildSummaryResultReader().read(file);
    for (int i = ir.getProteinGroupCount() - 1; i >= 0; i--) {
      List<BuildSummaryPeptideHit> peptides = ir.getProteinGroup(i).getPeptideHits();
      for (IIdentifiedPeptideHit peptide : peptides) {
        if (isModified(peptide.getPeptide(0).getSequence())) {
          ir.removeProteinGroup(i);
          break;
        }
      }
    }
    String outputfile = RcpaFileUtils.changeExtension(file, "unmodifiedonly");
    BuildSummaryResultWriter.getInstance().write(outputfile, ir);
  }

  private static void writeModifiedFile(String file) throws IOException,
      SequestParseException {
    BuildSummaryResult ir = new BuildSummaryResultReader().read(file);
    for (int i = ir.getProteinGroupCount() - 1; i >= 0; i--) {
      List<BuildSummaryPeptideHit> peptides = ir.getProteinGroup(i).getPeptideHits();
      for(int k = 0;k < peptides.size();k++){
        final IIdentifiedPeptideHit peptide = peptides.get(k);
        if (!isModified(peptide.getPeptide(0).getSequence())) {
          for (int j = 0; j < ir.getProteinGroup(i).getProteinCount(); j++) {
            ir.getProteinGroup(i).getProtein(j).removePeptide(k);
          }
        }
      }

      if (ir.getProteinGroup(i).getPeptideHitCount() == 0) {
        ir.removeProteinGroup(i);
      }
    }
    String outputfile = RcpaFileUtils.changeExtension(file, "modified");
    BuildSummaryResultWriter.getInstance().write(outputfile, ir);
  }

  private static void writeUnmodifiedFile(String file) throws IOException,
      SequestParseException {
    BuildSummaryResult ir = new BuildSummaryResultReader().read(file);
    for (int i = ir.getProteinGroupCount() - 1; i >= 0; i--) {
      List<BuildSummaryPeptideHit> peptides = ir.getProteinGroup(i).getPeptideHits();
      for(int k = 0;k < peptides.size();k++){
        final IIdentifiedPeptideHit peptide = peptides.get(k);
        if (isModified(peptide.getPeptide(0).getSequence())) {
          for (int j = 0; j < ir.getProteinGroup(i).getProteinCount(); j++) {
            ir.getProteinGroup(i).getProtein(j).removePeptide(k);
          }
        }
      }

      if (ir.getProteinGroup(i).getPeptideHitCount() == 0) {
        ir.removeProteinGroup(i);
      }
    }
    String outputfile = RcpaFileUtils.changeExtension(file, "unmodified");
    BuildSummaryResultWriter.getInstance().write(outputfile, ir);
  }

}
