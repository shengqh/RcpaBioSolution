/*
 * Created on 2006-3-1
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.modification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.proteomics.modification.DiffStateModificationPeptides;
import cn.ac.rcpa.bio.proteomics.modification.HomologModificationPeptidesList;
import cn.ac.rcpa.bio.proteomics.modification.SameModificationPeptides;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.tools.modification.IdentifiedPeptideModificationStatisticsCalculator;

class IdentifiedPeptideModificationStatisticsCalculatorProxy extends
    IdentifiedPeptideModificationStatisticsCalculator {

  public IdentifiedPeptideModificationStatisticsCalculatorProxy(
      String modifiedAminoacids) {
    super(modifiedAminoacids);
  }

  @Override
  public List<String> getModifiedSequenceList(BuildSummaryPeptideHit pephit) {
    return super.getModifiedSequenceList(pephit);
  }

  @Override
  public Map<String, List<BuildSummaryPeptideHit>> getPepSetMap(
      List<BuildSummaryPeptideHit> pephits) {
    return super.getPepSetMap(pephits);
  }

  @Override
  public List<List<BuildSummaryPeptideHit>> getUniquePeptidesSet(
      Map<String, List<BuildSummaryPeptideHit>> pepSetMap) {
    return super.getUniquePeptidesSet(pepSetMap);
  }

  @Override
  public List<SameModificationPeptides> getModificationPeptidesList(
      List<List<BuildSummaryPeptideHit>> uniquePeptidesList) {
    return super.getModificationPeptidesList(uniquePeptidesList);
  }

  @Override
  public List<DiffStateModificationPeptides> mergeSameModifiedSequenceButDifferentState(
      List<SameModificationPeptides> transedPeptides) {
    return super.mergeSameModifiedSequenceButDifferentState(transedPeptides);
  }

  @Override
  public HomologModificationPeptidesList getHomologModificationPeptides(
      List<DiffStateModificationPeptides> modificationPeptides) {
    return super.getHomologModificationPeptides(modificationPeptides);
  }

}

public class IdentifiedPeptideModificationStatisticsCalculatorTest extends
    TestCase {
  IdentifiedPeptideModificationStatisticsCalculatorProxy calculator = new IdentifiedPeptideModificationStatisticsCalculatorProxy(
      "STY");

  public void testGetModifiedSequenceList() throws Exception {
    BuildSummaryPeptideHit hit = BuildSummaryPeptideHitReader
        .parse("\tJWH_SAX_5_050906,11183\tR.AKPAAQSEEETATS*PAAS*PTPQSAER.S\t2773.69340\t-0.16660\t3\t1\t4.8095\t0.3620\t341.6\t1\t27|100\tIPI:IPI00229859.1|TREMBL:Q8CIJ3;Q8JZQ9;Q922K2|REFSEQ_NP:NP_598677|ENSEMBL:ENSMU/IPI:IPI00469272.1|ENSEMBL:ENSMUSP0\tR.AKPAAQSEEETATS*PAASPT*PQSAER.S(4.7281,0.0169) ! R.AKPAAQSEEETATS*PAASPTPQS*AER.S(4.5166,0.0609) ! R.AKPAAQSEEETAT*SPAAS*PTPQSAER.S(4.3683,0.0918)\t4.49");
    List<String> sequences = calculator.getModifiedSequenceList(hit);
    assertEquals(4, sequences.size());
    assertEquals("AKPAAQSEEETATS*PAAS*PTPQSAER", sequences.get(0));
    assertEquals("AKPAAQSEEETATS*PAASPT*PQSAER", sequences.get(1));
    assertEquals("AKPAAQSEEETATS*PAASPTPQS*AER", sequences.get(2));
    assertEquals("AKPAAQSEEETAT*SPAAS*PTPQSAER", sequences.get(3));
  }

  /**
   * All modified peptides have same pure sequence, same modification site count (1).
   * @throws Exception
   */
  public void testPhosphoPeptide1() throws Exception {
    List<BuildSummaryPeptideHit> hits = new BuildSummaryPeptideHitReader()
        .read("data/TestPhospho1.peptides");
    assertEquals(2, hits.size());

    Map<String, List<BuildSummaryPeptideHit>> pepSetMap = calculator
        .getPepSetMap(hits);
    assertEquals(3, pepSetMap.size());
    assertEquals(2, pepSetMap.get("PGPGPDSDS*DSDSDREEQEEEEEDQR").size());
    assertEquals(2, pepSetMap.get("PGPGPDSDSDS*DSDREEQEEEEEDQR").size());
    assertEquals(1, pepSetMap.get("PGPGPDS*DSDSDSDREEQEEEEEDQR").size());

    List<List<BuildSummaryPeptideHit>> uniquePeptides = calculator
        .getUniquePeptidesSet(pepSetMap);
    assertEquals(1, uniquePeptides.size());

    List<SameModificationPeptides> transedPeptides = calculator
        .getModificationPeptidesList(uniquePeptides);
    assertEquals(1, transedPeptides.size());
    assertEquals(2, transedPeptides.get(0).getPeptideHitCount());
    assertEquals("PGPGPDSDSpDSpDSDREEQEEEEEDQR", transedPeptides.get(0)
        .getMatchSequence());

    List<DiffStateModificationPeptides> modificationPeptides = calculator
        .mergeSameModifiedSequenceButDifferentState(transedPeptides);
    assertEquals(1, modificationPeptides.size());
    assertEquals("PGPGPDSDSpDSpDSDREEQEEEEEDQR", modificationPeptides.get(0)
        .getSequence().toString());

    HomologModificationPeptidesList homoPeptides = calculator
        .getHomologModificationPeptides(modificationPeptides);
    assertEquals(1, homoPeptides.size());
    assertEquals("PGPGPDSDSpDSpDSDREEQEEEEEDQR", homoPeptides.get(0)
        .getSequence().toString());
  }

  /**
   * Three double phospholated peptides, one single phospholated peptide
   * @throws Exception
   */
  public void testPhosphoPeptide2() throws Exception {
    List<BuildSummaryPeptideHit> hits = new BuildSummaryPeptideHitReader()
        .read("data/TestPhospho2.peptides");
    assertEquals(4, hits.size());

    Map<String, List<BuildSummaryPeptideHit>> pepSetMap = calculator
        .getPepSetMap(hits);
    assertEquals(12, pepSetMap.size());
    assertEquals(new HashSet<BuildSummaryPeptideHit>(Arrays
        .asList(new BuildSummaryPeptideHit[] { hits.get(0), hits.get(1) })),
        new HashSet<BuildSummaryPeptideHit>(pepSetMap
            .get("AKPAAQSEEETATS*PAASPT*PQSAER")));

    List<List<BuildSummaryPeptideHit>> uniquePeptides = calculator
        .getUniquePeptidesSet(pepSetMap);
    assertEquals(3, uniquePeptides.size());

    List<SameModificationPeptides> transedPeptides = calculator
        .getModificationPeptidesList(uniquePeptides);
    assertEquals(2, transedPeptides.size());
    assertEquals("AKPAAQSEEETATS*PAASPT*PQSAER", transedPeptides.get(0)
        .getMatchSequence());
    assertEquals(3, transedPeptides.get(0).getPeptideHitCount());
    assertEquals("AKPAAQSEEETATS*PAASPTPQSAER", transedPeptides.get(1)
        .getMatchSequence());
    assertEquals(1, transedPeptides.get(1).getPeptideHitCount());

    List<DiffStateModificationPeptides> modificationPeptides = calculator
        .mergeSameModifiedSequenceButDifferentState(transedPeptides);
    assertEquals(1, modificationPeptides.size());
    assertEquals("AKPAAQSEEETATS*PAASPT*PQSAER", modificationPeptides.get(0)
        .getSequence().toString());
    assertEquals(2, modificationPeptides.get(0).getSameModificationPeptidesList().size());
    assertEquals(4, modificationPeptides.get(0).getPeptideHitCount());

    HomologModificationPeptidesList homoPeptides = calculator
        .getHomologModificationPeptides(modificationPeptides);
    assertEquals(1, homoPeptides.size());
    assertEquals("AKPAAQSEEETATS*PAASPT*PQSAER", homoPeptides.get(0)
        .getSequence().toString());
    assertEquals(1, homoPeptides.get(0).getModificationPeptidesList().size());
    assertEquals(4, homoPeptides.get(0).getPeptideHitCount());
  }

  /**
   * Test sample with other modification (M+) except phosphorylation
   */
  public void testPhosphoPeptide3() throws Exception {
    List<BuildSummaryPeptideHit> hits = new BuildSummaryPeptideHitReader()
        .read("data/TestPhospho3.peptides");
    assertEquals(4, hits.size());

    Map<String, List<BuildSummaryPeptideHit>> pepSetMap = calculator
        .getPepSetMap(hits);
    assertEquals(4, pepSetMap.size());

    List<List<BuildSummaryPeptideHit>> uniquePeptides = calculator
        .getUniquePeptidesSet(pepSetMap);
    assertEquals(4, uniquePeptides.size());

    List<SameModificationPeptides> transedPeptides = calculator
        .getModificationPeptidesList(uniquePeptides);
    assertEquals(4, transedPeptides.size());
    assertEquals("KEESEES*DDDMGFGLFD", transedPeptides.get(0).getSequence().toString());
    assertEquals("KEES*EESDDDMGFGLFD", transedPeptides.get(1).getSequence().toString());
    assertEquals("KEES*EES*DDDMGFGLFD", transedPeptides.get(2).getSequence().toString());
    assertEquals("EESEES*DDDMGFGLFD", transedPeptides.get(3).getSequence().toString());

    List<DiffStateModificationPeptides> modificationPeptides = calculator
        .mergeSameModifiedSequenceButDifferentState(transedPeptides);
    assertEquals(2, modificationPeptides.size());
    assertEquals("KEES*EES*DDDMGFGLFD", modificationPeptides.get(0).getSequence().toString());
    assertEquals(3, modificationPeptides.get(0).getPeptideHitCount());
    assertEquals("EESEES*DDDMGFGLFD", modificationPeptides.get(1).getSequence().toString());

    HomologModificationPeptidesList homoPeptides = calculator
        .getHomologModificationPeptides(modificationPeptides);
    assertEquals(1, homoPeptides.size());
    assertEquals("KEES*EES*DDDMGFGLFD", homoPeptides.get(0)
        .getSequence().toString());
  }

  /**
   * Two phosphorylated peptides with same pure sequence, same count of positive site count
   * but different site.
   * 
   * @throws Exception
   */
  
  public void testPhosphoPeptide5() throws Exception {
    List<BuildSummaryPeptideHit> hits = new BuildSummaryPeptideHitReader()
        .read("data/TestPhospho5.peptides");
    assertEquals(2, hits.size());

    Map<String, List<BuildSummaryPeptideHit>> pepSetMap = calculator
        .getPepSetMap(hits);
    assertEquals(2, pepSetMap.size());

    List<List<BuildSummaryPeptideHit>> uniquePeptides = calculator
        .getUniquePeptidesSet(pepSetMap);
    assertEquals(2, uniquePeptides.size());

    List<SameModificationPeptides> transedPeptides = calculator
        .getModificationPeptidesList(uniquePeptides);
    assertEquals(2, transedPeptides.size());
    assertEquals("APEEVVRES*DDDDDDSD", transedPeptides.get(0).getSequence().toString());
    assertEquals("APEEVVRESDDDDDDS*D", transedPeptides.get(1).getSequence().toString());

    List<DiffStateModificationPeptides> modificationPeptides = calculator
        .mergeSameModifiedSequenceButDifferentState(transedPeptides);
    assertEquals(1, modificationPeptides.size());
    assertEquals("APEEVVRES*DDDDDDS*D", modificationPeptides.get(0).getSequence().toString());

    HomologModificationPeptidesList homoPeptides = calculator
        .getHomologModificationPeptides(modificationPeptides);
    assertEquals(1, homoPeptides.size());
    assertEquals("APEEVVRES*DDDDDDS*D", homoPeptides.get(0)
        .getSequence().toString());
  }

  /**
   * Two phosphorylated peptides with same pure sequence, different count of modified site count
   * 
   * @throws Exception
   */
  
  public void testPhosphoPeptide6() throws Exception {
    List<BuildSummaryPeptideHit> hits = new BuildSummaryPeptideHitReader()
        .read("data/TestPhospho6.peptides");
    assertEquals(2, hits.size());

    Map<String, List<BuildSummaryPeptideHit>> pepSetMap = calculator
        .getPepSetMap(hits);
    assertEquals(7, pepSetMap.size());

    List<List<BuildSummaryPeptideHit>> uniquePeptides = calculator
        .getUniquePeptidesSet(pepSetMap);
    assertEquals(2, uniquePeptides.size());

    List<SameModificationPeptides> transedPeptides = calculator
        .getModificationPeptidesList(uniquePeptides);
    assertEquals(2, transedPeptides.size());
    assertEquals("SSPKEEVAS*EPEEAASpPTpTpPK", transedPeptides.get(0).getSequence().toString());
    assertEquals(2, transedPeptides.get(0).getSequence().getModifiedCount());
    assertEquals("SpSpPKEEVAS*EPEEAASpPTpTPK", transedPeptides.get(1).getSequence().toString());
    assertEquals(3, transedPeptides.get(1).getSequence().getModifiedCount());

    List<DiffStateModificationPeptides> modificationPeptides = calculator
        .mergeSameModifiedSequenceButDifferentState(transedPeptides);
    assertEquals(1, modificationPeptides.size());
    assertEquals("SpSpPKEEVAS*EPEEAASpPTpTpPK", modificationPeptides.get(0).getSequence().toString());
    assertEquals(3, modificationPeptides.get(0).getSequence().getModifiedCount());
    assertEquals(1, modificationPeptides.get(0).getSequence().getTrueModifiedCount());
    assertEquals(5, modificationPeptides.get(0).getSequence().getAmbiguousModifiedCount());
  }
}
