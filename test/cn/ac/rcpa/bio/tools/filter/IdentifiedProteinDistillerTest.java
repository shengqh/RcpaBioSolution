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

public class IdentifiedProteinDistillerTest extends TestCase {

  /*
   * Test method for 'cn.ac.rcpa.bio.tools.filter.IdentifiedProteinDistiller.isProteinLine(String)'
   */
  public void testIsProteinLine() {
    assertTrue(IdentifiedProteinInfoDistiller.isProteinLine("$134-1 IPI:IPI00459443.2|SWISS-PROT:P58871|TREMBL:Q6ZPI8|ENSEMBL:ENSMUSP00000045767 Tax_Id=10090 MKIAA1741 PROTEIN 4   2   2.64%   140575.44   4.66"));
    assertFalse(IdentifiedProteinInfoDistiller.isProteinLine("  JWH_SAX_35_050906,17598 R.SLS*SGFSPEEAQQQDEEFEK.K   2353.27544  0.58844 2   1   3.4900  0.4463  246.9   1   18|57   IPI:IPI00459443.2|SWISS-PROT:P58871|TREMBL:Q6ZPI8|ENSEMBL:ENSMUSP00000045767    3.83    R.S*LSSGFSPEEAQQQDEEFEK.K(3.4873,0.0008) ! R.SLSS*GFSPEEAQQQDEEFEK.K(3.4646,0.0073)"));
    assertFalse(IdentifiedProteinInfoDistiller.isProteinLine("  @935-1  IPI:IPI00215349.3|TREMBL:Q5RKI0|REFSEQ_XP:XP_341229|ENSEMBL:ENSRNOP00000024012 Tax_Id=10116 Hypothetical protein    2   2     3.14% 66181.29      6.15  IPI:IPI00215349.3|TREMBL:Q5RKI0|REFSEQ_XP:XP_341229|ENSEMBL:ENSRNOP00000024012 Tax_Id=10116 Hypothetical protein"));
  }

}
