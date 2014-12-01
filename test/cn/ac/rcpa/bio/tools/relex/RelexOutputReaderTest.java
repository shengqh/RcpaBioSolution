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

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import cn.ac.rcpa.utils.RcpaObjectUtils;

public class RelexOutputReaderTest extends TestCase {

  /*
   * Test method for 'cn.ac.rcpa.bio.tools.relex.RelexOutputReader.read(String)'
   */
  public void testRead() throws IOException {
    List<RelexProtein> proteins = RelexOutputReader.read("data/Relex-Output.txt");
    assertEquals(2, proteins.size());
    assertTrue(RcpaObjectUtils.objectEquals(proteins, "data/Relex-Output.xml.expect"));
  }

}
