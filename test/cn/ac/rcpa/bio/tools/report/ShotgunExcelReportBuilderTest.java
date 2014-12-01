/*
 * Created on 2005-12-5
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.report;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import cn.ac.rcpa.IParser;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.utils.PatternValueParser;

public class ShotgunExcelReportBuilderTest extends TestCase {

  private BuildSummaryPeptideHit getPeptideHit(String sequence, double xcorr,
      int charge) {
    BuildSummaryPeptideHit result = new BuildSummaryPeptideHit();
    BuildSummaryPeptide pep = new BuildSummaryPeptide();
    pep.setSequence(sequence);
    pep.setXcorr(xcorr);
    pep.setCharge(charge);
    result.addPeptide(pep);
    return result;
  }

  /*
   * Test method for
   * 'cn.ac.rcpa.bio.tools.report.ShotgunExcelReportBuilder.getNoredundantPeptides(List<BuildSummaryPeptideHit>)'
   */
  public void testGetNoredundantPeptides() {
    List<BuildSummaryPeptideHit> hitList = new ArrayList<BuildSummaryPeptideHit>();
    hitList.add(getPeptideHit("A.BBB.C", 3.0, 2));
    hitList.add(getPeptideHit("A.BBB.C", 4.0, 2));
    hitList.add(getPeptideHit("A.BBBBB.C", 3.0, 3));

    List<BuildSummaryPeptideHit> newHitList = ShotgunExcelReportBuilder
        .getNoredundantPeptides(hitList);

    assertEquals(2, newHitList.size());
    assertEquals("A.BBB.C", newHitList.get(0).getPeptide(0).getSequence());
    assertEquals(4.0, newHitList.get(0).getPeptide(0).getXcorr());
    assertEquals(2, newHitList.get(0).getPeptide(0).getCharge());
    assertEquals("A.BBBBB.C", newHitList.get(1).getPeptide(0).getSequence());
    assertEquals(3.0, newHitList.get(1).getPeptide(0).getXcorr());
    assertEquals(3, newHitList.get(1).getPeptide(0).getCharge());
  }

  /*
   * Test method for
   * 'cn.ac.rcpa.bio.tools.report.ShotgunExcelReportBuilder.getFraction(String)'
   */
  public void testGetFraction() {
    ShotgunExcelReportBuilder builder = new ShotgunExcelReportBuilder("","",SequenceDatabaseType.OTHER);
    assertEquals("JWH_SAX_25_050906", builder.getFraction("JWH_SAX_25_050906"));

    IParser<String> parser = new PatternValueParser("Fraction",
        "JWH_(\\S+)_(\\d+)_", "-", "%s is not a valid format!");
    builder.setFractionParser(parser);
    assertEquals("SAX-25", builder.getFraction("JWH_SAX_25_050906"));
  }

}
