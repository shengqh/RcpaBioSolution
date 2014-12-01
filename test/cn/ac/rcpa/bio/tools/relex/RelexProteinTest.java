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

public class RelexProteinTest extends TestCase {

  /*
   * Test method for 'cn.ac.rcpa.bio.tools.relex.RelexProtein.parse(String)'
   */
  public void testParse() {
    String protein1 = "P\tADHX_MOUSE, Q8C662\tADHX_MOUSE (P28474) Alcohol dehydrogenase class III (EC 1.1.1.1) (Alcohol dehydrogenase 2) (Glutathione-dependent formaldehyde dehydrogenase) (EC 1.2.1.1) (FDH) (FALDH) (Alcohol dehydrogenase-B2) (ADH-B2), Q8C662 (Q8C662) Alcohol dehydrogenase 5\t1.644\t0.078\t3";
    assertTrue(RelexProtein.isRelexProtein(protein1));
    assertTrue(RcpaObjectUtils.objectEquals(RelexProtein.parse(protein1),
        "data/RelexProtein.xml.expect"));

    String protein2 = "ADHX_MOUSE, Q8C662\tADHX_MOUSE (P28474) Alcohol dehydrogenase class III (EC 1.1.1.1) (Alcohol dehydrogenase 2) (Glutathione-dependent formaldehyde dehydrogenase) (EC 1.2.1.1) (FDH) (FALDH) (Alcohol dehydrogenase-B2) (ADH-B2), Q8C662 (Q8C662) Alcohol dehydrogenase 5\t1.644\t0.078\t3";
    assertTrue(RelexProtein.isRelexProtein(protein2));
    assertTrue(RcpaObjectUtils.objectEquals(RelexProtein.parse(protein2),
        "data/RelexProtein.xml.expect"));
  }

}
