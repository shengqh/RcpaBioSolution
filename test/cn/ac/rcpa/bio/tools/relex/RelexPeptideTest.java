/*
 * Created on 2006-1-12
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.relex;

import junit.framework.TestCase;
import cn.ac.rcpa.utils.RcpaObjectUtils;

public class RelexPeptideTest extends TestCase {

  /*
   * Test method for 'cn.ac.rcpa.bio.tools.relex.RelexPeptide.parse(String)'
   */
  public void testParse() {
    String peptide1 = "R\tN\tK.ELGATEC*INPQDYSK.P\t5.708\tMDJ_Elute_1_051013_Nano_05.13555.13555.2.chro\tu:\\MaDJ\\2d_icat_pn_cn\\chro\\\t7.37\t";
    assertTrue(RelexPeptide.isRelexPeptide(peptide1));
    assertTrue(RcpaObjectUtils.objectEquals(RelexPeptide.parse(peptide1),
        "data/RelexPeptide1.xml.expect"));

    String peptide2 = "S\tN\tK.ELGATEC*INPQDYSK.P\t5.708\tMDJ_Elute_1_051013_Nano_05.13555.13555.2.chro\tu:\\MaDJ\\2d_icat_pn_cn\\chro\\\t7.37\tOmitted - HIGH";
    assertTrue(RelexPeptide.isRelexPeptide(peptide2));
    assertTrue(RcpaObjectUtils.objectEquals(RelexPeptide.parse(peptide2),
        "data/RelexPeptide2.xml.expect"));
  }

}
