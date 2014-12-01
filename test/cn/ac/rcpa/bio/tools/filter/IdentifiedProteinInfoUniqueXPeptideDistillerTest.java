/*
 * Created on 2005-11-23
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class IdentifiedProteinInfoUniqueXPeptideDistillerTest extends TestCase {

  /*
   * Test method for
   * 'cn.ac.rcpa.bio.tools.filter.IdentifiedProteinInfoDistiller.process(String)'
   */
  public void testProcess() throws Exception {
    IdentifiedProteinInfoUniqueXPeptideDistiller distiller = new IdentifiedProteinInfoUniqueXPeptideDistiller(
        2);
    List<String> results = new ArrayList<String>();
    try {
      results = distiller.process("data/parent_children.proteins");
      assertEquals(1, results.size());
      String[] lines = RcpaFileUtils.readFile(results.get(0));
      assertEquals(2, lines.length);
    } finally {
      for (String file : results) {
        new File(file).delete();
      }
    }

  }

}
