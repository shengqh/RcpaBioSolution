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

import java.io.IOException;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.sequest.SequestParseException;

public class BuildSummaryProteinReporter extends
    AbstractReporter<BuildSummaryProtein> {

  public static String version = "1.0.0";

	@Override
  protected List<BuildSummaryProtein> readFromFile(String originFile)
      throws SequestParseException, IOException {
    return new BuildSummaryResultReader().read(originFile).getProteins();
  }
}
