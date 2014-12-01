/*
 * Created on 2005-12-25
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.report;

import java.util.List;

import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;

public class BuildSummaryPeptideHitReporter extends
    AbstractReporter<BuildSummaryPeptideHit> {

  public static String version = "1.0.0";

	@Override
  protected List<BuildSummaryPeptideHit> readFromFile(String originFile)
      throws Exception {
    return new BuildSummaryPeptideHitReader().read(originFile);
  }

}
