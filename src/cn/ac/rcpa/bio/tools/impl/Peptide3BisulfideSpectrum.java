package cn.ac.rcpa.bio.tools.impl;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.ac.rcpa.bio.proteomics.SequenceValidateException;
import cn.ac.rcpa.bio.tools.IBisulfideSpectrum;
import cn.ac.rcpa.bio.utils.MassCalculator;

public class Peptide3BisulfideSpectrum
    implements IBisulfideSpectrum {
  private MassCalculator monoMc = new MassCalculator(true);
  private MassCalculator averMc = new MassCalculator(false);
  private DecimalFormat df = new DecimalFormat("#.##");

  public Peptide3BisulfideSpectrum() {
  }

  private void generateBYIons(PrintStream ps, String peptide1,
                              String otherPeptideInfo, double monoTotalCharge1) throws
      SequenceValidateException {
    ps.println("Fragment in peptide " + peptide1);

    int ipos = 0;
    while ( (ipos = peptide1.indexOf('C', ipos)) != -1) {
      boolean bBeforeC = true;
      ps.println("Assuming the C(" + (ipos + 1) + ") of " + peptide1 +
                 " is linked by S-S");
      ps.println("Series\tPeptide\tCharge1\tCharge2\tCharge3\tSeries\tPeptide\tCharge1\tCharge2\tCharge3");
      ps.println("B\t-\t-\t-\t-\t" + "Y\t" +
                 getMzOfCharge(peptide1 + " + " + otherPeptideInfo,
                               monoTotalCharge1, MassCalculator.Hmono, 3));
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
              otherPeptideInfo;
          yIon = monoTotalCharge1 + MassCalculator.Hmono - bIon;
        }
        else {
          ySeq = peptide1.substring(i + 1, peptide1.length());
          yIon = ySeq.length() > 0 ? monoMc.getMass(ySeq) + MassCalculator.Hmono :
              monoMc.getCTermMass() + monoMc.getNTermMass() + MassCalculator.Hmono;
          bSeq = peptide1.substring(0, i + 1) + " + " + otherPeptideInfo;
          bIon = monoTotalCharge1 + MassCalculator.Hmono - yIon;
        }
        if (i == peptide1.length() - 1) {
          ps.println("B\t" +
                     getMzOfCharge(bSeq, bIon, MassCalculator.Hmono, 3) +
                     "\tY\t-\t-\t-\t-");
        }
        else {
          ps.println("B\t" +
                     getMzOfCharge(bSeq, bIon, MassCalculator.Hmono, 3) +
                     "\t" +
                     "Y\t" +
                     getMzOfCharge(ySeq, yIon, MassCalculator.Hmono, 3));
        }
      }
      ps.println("");
      ipos++;
    }
  }

  private String getMzOfCharge(String peptideInfo,
                               double mzOfCharge1, double massH, int maxCharge) {
    StringBuffer sb = new StringBuffer();
    sb.append(peptideInfo);
    for (int i = 1; i <= maxCharge; i++) {
      sb.append("\t" + df.format( (mzOfCharge1 + (i - 1) * massH) / i));
    }
    return sb.toString();
  }

  private void generatePrecursor(PrintStream ps, String peptide1,
                                 String peptide2, String peptide3) throws
      SequenceValidateException {
    double averMass1 = averMc.getMass(peptide1);
    double averMass2 = averMc.getMass(peptide2);
    double averMass3 = averMc.getMass(peptide3);
    double averTotal = averMass1 + averMass2 + averMass3 + MassCalculator.Hmono -
        4 * MassCalculator.Havg;

    ps.println("Peptide\tCharge1\tCharge2\tCharge3\tCharge4");
    ps.println(getMzOfCharge(peptide1 + " + " + peptide2 + " + " +
                             peptide3 +
                             " with S-S:", averTotal,
                             MassCalculator.Havg, 4));
    ps.println(getMzOfCharge(peptide1 + ":",
                             averMass1 + MassCalculator.Havg, MassCalculator.Havg, 4));
    ps.println(getMzOfCharge(peptide2 + ":",
                             averMass2 + MassCalculator.Havg, MassCalculator.Havg, 4));
    ps.println(getMzOfCharge(peptide3 + ":",
                             averMass3 + MassCalculator.Havg, MassCalculator.Havg, 4));
    ps.println("");
  }

  public void generate(PrintStream ps, String[] peptides) throws
      SequenceValidateException {
    if (peptides.length != 3) {
      throw new IllegalArgumentException(
          "Parameter peptides of generate should contains three peptide only!");
    }
    generatePrecursor(ps, peptides[0], peptides[1], peptides[2]);
    double monoTotalCharge1 = monoMc.getMass(peptides[0]) +
        monoMc.getMass(peptides[1]) + monoMc.getMass(peptides[2]) +
        MassCalculator.Hmono - 4 * MassCalculator.Hmono;

    generateBYIons(ps, peptides[0], peptides[1] + " + " + peptides[2],
                   monoTotalCharge1);
    generateBYIons(ps, peptides[2], peptides[1] + " + " + peptides[0],
                   monoTotalCharge1);
    generateCentreBYIons(ps, peptides[1], peptides[0], peptides[2],
                         monoTotalCharge1);
    generateCentreBYIons(ps, peptides[1], peptides[2], peptides[0],
                         monoTotalCharge1);

  }

  private void generateCentreBYIons(PrintStream ps, String peptide2,
                                    String peptide1, String peptide3,
                                    double monoTotalCharge1) throws
      SequenceValidateException {
    double monoMass1 = monoMc.getMass(peptide1);

    ArrayList<Integer> posList = new ArrayList<Integer> ();
    int ipos = 0;
    while ( (ipos = peptide2.indexOf('C', ipos)) != -1) {
      posList.add(ipos);
      ipos++;
    }

    for (int i = 0; i < posList.size() - 1; i++) {
      int firstPos = posList.get(i);
      for (int j = i + 1; j < posList.size(); j++) {
        int secondPos = posList.get(j);
        boolean bBeforeAll = true;
        boolean bAfterAll = false;
        ps.println("Assuming the C(" + (firstPos + 1) + ") and the C(" +
                   (secondPos + 1) + ") of " + peptide2 +
                   " are linked by S-S");
        ps.println("Series\tPeptide\tCharge1\tCharge2\tCharge3\tSeries\tPeptide\tCharge1\tCharge2\tCharge3");
        ps.println("B\t-\t-\t-\t-\t" + "Y\t" +
                   getMzOfCharge(peptide1 + " + " + peptide2 + " + " +
                                 peptide3,
                                 monoTotalCharge1, MassCalculator.Hmono, 3));
        for (int k = 0; k < peptide2.length(); k++) {
          if (k >= firstPos) {
            bBeforeAll = false;
          }
          if (k >= secondPos) {
            bAfterAll = true;
          }

          double bIon;
          double yIon;
          String bSeq = peptide2.substring(0, k + 1);
          String ySeq = peptide2.substring(k + 1, peptide2.length());

          if (bBeforeAll) {
            bIon = monoMc.getMass(bSeq) + MassCalculator.Hmono - monoMc.getNTermMass() -
                monoMc.getCTermMass();
            ySeq = peptide1 + " + " + ySeq + " + " + peptide3;
            yIon = monoTotalCharge1 + MassCalculator.Hmono - bIon;
          }
          else if (!bAfterAll) {
            bIon = monoMass1 + monoMc.getMass(bSeq) + MassCalculator.Hmono -
                monoMc.getNTermMass() -
                monoMc.getCTermMass() - 2 * MassCalculator.Hmono;
            bSeq = peptide1 + " + " + bSeq;
            yIon = monoTotalCharge1 + MassCalculator.Hmono - bIon;
            ySeq = ySeq + " + " + peptide2;
          }
          else {
            yIon = ySeq.length() > 0 ? monoMc.getMass(ySeq) + MassCalculator.Hmono :
                monoMc.getCTermMass() + monoMc.getNTermMass() + MassCalculator.Hmono;
            bIon = monoTotalCharge1 + MassCalculator.Hmono - yIon;
            bSeq = peptide1 + " + " + bSeq + " + " + peptide3;
          }

          if (k == peptide2.length() - 1) {
            ps.println("B\t" +
                       getMzOfCharge(bSeq, bIon, MassCalculator.Hmono, 3) +
                       "\tY\t-\t-\t-\t-");
          }
          else {
            ps.println("B\t" +
                       getMzOfCharge(bSeq, bIon, MassCalculator.Hmono, 3) +
                       "\t" +
                       "Y\t" +
                       getMzOfCharge(ySeq, yIon, MassCalculator.Hmono, 3));
          }
        }
        ps.println("");
      }
    }
  }

  public static void main(String[] args) throws SequenceValidateException,
      FileNotFoundException {
    Peptide3BisulfideSpectrum bs = new Peptide3BisulfideSpectrum();
    PrintStream pw = new PrintStream("data/bisulfide3.xls");
    String[] peptides = new String[] {"CRKY", "CQCKF", "KPGCHLASF"};
    bs.generate(pw, peptides);
    pw.close();
    bs.generate(System.out, peptides);
  }
}
