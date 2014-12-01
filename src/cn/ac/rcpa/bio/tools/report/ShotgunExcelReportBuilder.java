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

import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.poi.hssf.usermodel.HSSFRow;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.utils.AccessNumberUtils;
import cn.ac.rcpa.utils.POIExcelUtils;
import cn.ac.rcpa.utils.Pair;

public class ShotgunExcelReportBuilder extends AbstractExcelReportBuilder {
	public ShotgunExcelReportBuilder(String url, String project,
      SequenceDatabaseType dbType) {
    super(url, project, dbType);
  }

  public List<String> process(String originFile) throws Exception {
    BuildSummaryResult ir = new BuildSummaryResultReader().read(originFile);

    initExcel("Shutgun");

    int rowindex = 0;
    HSSFRow row;

    /**
     * <code>
     row = sheet.createRow((short) (rowindex));
     HSSFCell cell = row.createCell((short) 1);
     cell.setCellValue("");
     sheet.addMergedRegion(new Region(rowindex, (short) 0, rowindex, (short) 6));
     rowindex++;
     </code>
     */
    row = sheet.createRow((short) (rowindex++));
    POIExcelUtils.createCell(row, 0, normalStyle, "Protein");
    POIExcelUtils.createCell(row, 1, normalStyle, "Peptide");
    POIExcelUtils.createCell(row, 2, normalStyle, "Charge");
    POIExcelUtils.createCell(row, 3, normalStyle, "XCorr");
    POIExcelUtils.createCell(row, 4, normalStyle, "Fraction");
    POIExcelUtils.createCell(row, 5, normalStyle, "PI");
    POIExcelUtils.createCell(row, 6, normalStyle, "Nodes");

    String rootUrl = getUrl();
    if(!rootUrl.endsWith("/")){
    	rootUrl = rootUrl + "/";
    }
    
    DecimalFormat df = new DecimalFormat("0.00");
    for (int i = 0; i < ir.getProteinGroupCount(); i++) {
      BuildSummaryProteinGroup group = ir.getProteinGroup(i);
      String proteinNames = AccessNumberUtils.getAccessNumber(group
          .getProteinNames(), getDbType());
      row = sheet.createRow((short) (rowindex++));
      POIExcelUtils.createCell(row, 0, normalStyle, proteinNames);

      List<BuildSummaryPeptideHit> pephits = getNoredundantPeptides(group
          .getPeptideHits());
      for (BuildSummaryPeptideHit hit : pephits) {
        row = sheet.createRow((short) (rowindex++));
        BuildSummaryPeptide peptide = hit.getPeptide(0);
        String seq = peptide.getSequence();
        String outfilename = hit.getPeakListInfo().getLongFilename();
        if(outfilename.endsWith(".")){
        	outfilename = outfilename + "out"; 
        }
        String curUrl = rootUrl + "showdta.do?project=" + getProject()
            + "&outfile=" + outfilename
            + "&peptide=" + URLEncoder.encode(seq, "UTF-8");
        POIExcelUtils.createHyperLinkCell(row, 1, linkStyle, curUrl, seq);
        POIExcelUtils.createCell(row, 2, normalStyle, peptide.getCharge());
        POIExcelUtils.createCell(row, 3, normalStyle, peptide.getXcorr());
        POIExcelUtils.createCell(row, 4, normalStyle, getFraction(peptide
            .getPeakListInfo().getExperiment()));
        POIExcelUtils.createCell(row, 5, normalStyle, df
            .format(peptide.getPI()));
      }
    }

    String resultFile = originFile + ".xls";
    FileOutputStream fileOut = new FileOutputStream(resultFile);
    try {
      wb.write(fileOut);
    } finally {
      fileOut.close();
    }
    return Arrays.asList(new String[] { resultFile });
  }

  /**
   * Keep the peptide hit with largest XCorr among the peptide hits whose
   * sequence and charge are same.
   * 
   * @param peptideHits
   * @return
   */
  public static List<BuildSummaryPeptideHit> getNoredundantPeptides(
      List<BuildSummaryPeptideHit> peptideHits) {
    LinkedHashMap<Pair<String, Integer>, BuildSummaryPeptideHit> resultMap = new LinkedHashMap<Pair<String, Integer>, BuildSummaryPeptideHit>();
    for (BuildSummaryPeptideHit hit : peptideHits) {
      Pair<String, Integer> pair = new Pair<String, Integer>(hit.getPeptide(0)
          .getSequence(), hit.getPeptide(0).getCharge());
      if (resultMap.containsKey(pair)) {
        BuildSummaryPeptideHit oldhit = resultMap.get(pair);
        if (oldhit.getPeptide(0).getXcorr() < hit.getPeptide(0).getXcorr()) {
          resultMap.put(pair, hit);
        }
      } else {
        resultMap.put(pair, hit);
      }
    }

    ArrayList<BuildSummaryPeptideHit> result = new ArrayList<BuildSummaryPeptideHit>(
        resultMap.values());

    Collections.sort(result, new Comparator<BuildSummaryPeptideHit>() {

      public int compare(BuildSummaryPeptideHit o1, BuildSummaryPeptideHit o2) {
        return new CompareToBuilder().append(o1.getPeptide(0).getSequence(),
            o2.getPeptide(0).getSequence()).append(
            o1.getPeakListInfo().getLongFilename(),
            o2.getPeakListInfo().getLongFilename()).toComparison();
      }
    });

    return result;
  }
}
