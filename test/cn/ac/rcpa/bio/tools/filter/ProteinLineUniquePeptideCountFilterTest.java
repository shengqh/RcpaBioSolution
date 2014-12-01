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

import junit.framework.TestCase;

public class ProteinLineUniquePeptideCountFilterTest extends TestCase {

  /*
   * Test method for 'cn.ac.rcpa.bio.tools.filter.ProteinLineUniquePeptideCountFilter.accept(String)'
   */
  public void testAccept() {
    ProteinLineUniquePeptideCountFilter filter = new ProteinLineUniquePeptideCountFilter(2);
    assertTrue(filter.accept("$935-1\tIPI:IPI00215349.3\t2\t2\t3.14%\t66181.29\t6.15\tIPI:IPI00215349.3|TREMBL:Q5RKI0|REFSEQ_XP:XP_341229|ENSEMBL:ENSRNOP00000024012 Tax_Id=10116 Hypothetical protein"));
    assertFalse(filter.accept("$1997-1\tIPI:IPI00364112.1\t1\t1\t5.52%\t19722.13\t5.19\tIPI:IPI00364112.1|REFSEQ_XP:XP_341230 Tax_Id=10116 Similar to WD-repeat protein 1 (Actin interacting protein 1) (AIP1)"));
  }

}
