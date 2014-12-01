package cn.ac.rcpa.bio.tools.impl;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;

import cn.ac.rcpa.bio.proteomics.SequenceValidateException;
import cn.ac.rcpa.bio.tools.IBisulfideSpectrum;
import cn.ac.rcpa.bio.utils.MassCalculator;

public class Peptide2BisulfideSpectrum implements IBisulfideSpectrum{
  private MassCalculator monoMc = new MassCalculator(true);
  private MassCalculator averMc = new MassCalculator(false);
  private DecimalFormat df = new DecimalFormat("#.##");

  public Peptide2BisulfideSpectrum() {
  }

  private void generateBYIons(PrintStream ps, String peptide1, String peptide2) throws
      SequenceValidateException {
    double monoMass1 = monoMc.getMass(peptide1);
    double monoMass2 = monoMc.getMass(peptide2);

    double monoTotalCharge1 = monoMass1 + monoMass2 - MassCalculator.Hmono;

    ps.println("Fragment in peptide " + peptide1);

    int ipos = 0;
    while ( (ipos = peptide1.indexOf('C', ipos)) != -1) {
      boolean bBeforeC = true;
      ps.println("Assuming the C(" + (ipos + 1) + ") of " + peptide1 +
                 " is linked by S-S");
      ps.println("Series\tPeptide\tCharge1\tCharge2\tCharge3\tSeries\tPeptide\tCharge1\tCharge2\tCharge3");
      ps.println("B\t-\t-\t-\t-\t" + "Y\t" +
                 getMzOfOneTwoThreeCharge(peptide1 + " + " + peptide2,
                                          monoTotalCharge1, MassCalculator.Hmono));
      for (int i = 0; i < peptide1.length(); i++) {
        if (i >= ipos) {
          bBeforeC = false;
        }

        double bIon;
        double yIon;
        String bSeq;
        String ySeq;
        if (bBeforeC) {
          bSeq = peptide1.substring(0, i + 1);
          bIon = monoMc.getMass(bSeq) + MassCalculator.Hmono - monoMc.getNTermMass() -
              monoMc.getCTermMass();
          ySeq = peptide1.substring(i + 1, peptide1.length()) + " + " +
              peptide2;
          yIon = monoTotalCharge1 + MassCalculator.Hmono - bIon;
        }
        else {
          ySeq = peptide1.substring(i + 1, peptide1.length());
          yIon = ySeq.length() > 0 ? monoMc.getMass(ySeq) + MassCalculator.Hmono :
              monoMc.getCTermMass() + monoMc.getNTermMass() + MassCalculator.Hmono;
          bSeq = peptide1.substring(0, i + 1) + " + " + peptide2;
          bIon = monoTotalCharge1 + MassCalculator.Hmono - yIon;
        }
        if (i == peptide1.length() - 1) {
          ps.println("B\t" +
                     getMzOfOneTwoThreeCharge(bSeq, bIon, MassCalculator.Hmono) +
                     "\tY\t-\t-\t-\t-");
        }
        else {
          ps.println("B\t" +
                     getMzOfOneTwoThreeCharge(bSeq, bIon, MassCalculator.Hmono) +
                     "\t" +
                     "Y\t" +
                     getMzOfOneTwoThreeCharge(ySeq, yIon, MassCalculator.Hmono));
        }
      }
      ps.println("");
      ipos++;
    }
  }

  private String getMzOfOneTwoThreeCharge(String peptideInfo,
                                          double mzOfCharge1, double massH) {
    return peptideInfo + "\t" + df.format(mzOfCharge1) + "\t" +
        df.format( (mzOfCharge1 + massH) / 2) + "\t" +
        df.format( (mzOfCharge1 + 2 * massH) / 3);
  }

  private void generatePrecursor(PrintStream ps, String peptide1,
                                 String peptide2) throws
      SequenceValidateException {
    double averMass1 = averMc.getMass(peptide1);
    double averMass2 = averMc.getMass(peptide2);
    double averTotal = averMass1 + averMass2 - MassCalculator.Havg;

    ps.println("Peptide\tCharge1\tCharge2\tCharge3");
    ps.println(getMzOfOneTwoThreeCharge(peptide1 + " + " + peptide2 +
                                        " with S-S:", averTotal,
                                        MassCalculator.Havg));
    ps.println(getMzOfOneTwoThreeCharge(peptide1 + ":",
                                        averMass1 + MassCalculator.Havg, MassCalculator.Havg));
    ps.println(getMzOfOneTwoThreeCharge(peptide2 + ":",
                                        averMass2 + MassCalculator.Havg, MassCalculator.Havg));
    ps.println("");
  }

  public void generate(PrintStream ps, String[] peptides) throws
      SequenceValidateException {
    if (peptides.length != 2){
      throw new IllegalArgumentException("Parameter peptides of generate should contains two peptide only!");
    }
    generatePrecursor(ps, peptides[0], peptides[1]);
    generateBYIons(ps, peptides[0], peptides[1]);
    generateBYIons(ps, peptides[1], peptides[0]);
  }

  public static void main(String[] args) throws SequenceValidateException,
      FileNotFoundException {
    Peptide2BisulfideSpectrum bs = new Peptide2BisulfideSpectrum();
    PrintStream pw = new PrintStream("data/bisulfide.xls");
    bs.generate(pw, new String[]{"CVELCVSL", "NDQVCESKNSFL"});
    pw.close();
    bs.generate(System.out, new String[]{"CVELCVSL", "NDQVCESKNSFL"});
  }
}
