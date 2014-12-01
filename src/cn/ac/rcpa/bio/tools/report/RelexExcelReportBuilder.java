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
package cn.ac.rcpa.bio.tools.report;

import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.sequest.SequestFilename;
import cn.ac.rcpa.bio.tools.relex.RelexOutputReader;
import cn.ac.rcpa.bio.tools.relex.RelexPeptide;
import cn.ac.rcpa.bio.tools.relex.RelexProtein;
import cn.ac.rcpa.bio.utils.AccessNumberUtils;
import cn.ac.rcpa.bio.utils.IsoelectricPointCalculator;
import cn.ac.rcpa.utils.POIExcelUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class RelexExcelReportBuilder extends AbstractExcelReportBuilder {
  public static String version = "1.0.0";
  
	private String peptideFile;

  public RelexExcelReportBuilder(String url, String project, SequenceDatabaseType dbType,
      String peptideFile) {
    super(url, project, dbType);
    this.peptideFile = peptideFile;
  }

  public List<String> process(String originFile) throws Exception {
    List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader()
        .read(peptideFile);
    List<RelexProtein> proteins = RelexOutputReader.read(originFile);

    HashMap<String, BuildSummaryPeptideHit> pepMap = new HashMap<String, BuildSummaryPeptideHit>();
    for (BuildSummaryPeptideHit peptide : pephits) {
      String chroFilename = (peptide.getPeakListInfo().getExperiment() + "."
          + peptide.getPeakListInfo().getFirstScan() + "."
          + peptide.getPeakListInfo().getFirstScan() + "."
          + peptide.getPeakListInfo().getCharge() + ".chro").toLowerCase();
      pepMap.put(chroFilename, peptide);
    }

    initExcel("Relex");

    int rowindex = 0;
    HSSFRow row;

    row = sheet.createRow((short) (rowindex++));
    POIExcelUtils.createCell(row, 0, normalStyle, "Protein");
    POIExcelUtils.createCell(row, 1, normalStyle, "R2");
    POIExcelUtils.createCell(row, 2, normalStyle, "Ratio");
    POIExcelUtils.createCell(row, 3, normalStyle, "Peptide");
    POIExcelUtils.createCell(row, 4, normalStyle, "Charge");
    POIExcelUtils.createCell(row, 5, normalStyle, "XCorr");
    POIExcelUtils.createCell(row, 6, normalStyle, "Fraction");
    POIExcelUtils.createCell(row, 7, normalStyle, "PI");
    POIExcelUtils.createCell(row, 8, normalStyle, "Ratio");
    POIExcelUtils.createCell(row, 9, normalStyle, "Notes");

    DecimalFormat df = new DecimalFormat("0.00");
    for (RelexProtein protein : proteins) {
      String proteinNames = AccessNumberUtils.getAccessNumber(protein
          .getProteins(), getDbType());
      row = sheet.createRow((short) (rowindex++));
      POIExcelUtils.createCell(row, 0, normalStyle, proteinNames);
      POIExcelUtils.createCell(row, 1, normalStyle, protein.getSd());
      POIExcelUtils.createCell(row, 2, normalStyle, protein.getRatio());

      for (RelexPeptide peptide : protein.getPeptides()) {
        row = sheet.createRow((short) (rowindex++));

        String outFilename = RcpaFileUtils.changeExtension(peptide
            .getChroFile(), "out");

        BuildSummaryPeptideHit pephit = pepMap.get(peptide.getChroFile()
            .toLowerCase());
        if (null == pephit) {
          throw new IllegalStateException(peptideFile + " doesn't contain "
              + outFilename + " - " + peptide.getSequence());
        }

        String seq = peptide.getSequence();
        String curUrl = getUrl() + "/showdta.do?project=" + getProject()
            + "&outfile=" + outFilename + "&peptide="
            + URLEncoder.encode(seq, "UTF-8");

        POIExcelUtils.createHyperLinkCell(row, 3, linkStyle, curUrl, seq);

        SequestFilename sf = SequestFilename.parse(peptide.getChroFile());
        POIExcelUtils.createCell(row, 4, normalStyle, sf.getCharge());

        POIExcelUtils.createCell(row, 5, normalStyle, pephit.getPeptide(0)
            .getXcorr());

        POIExcelUtils.createCell(row, 6, normalStyle, getFraction(sf
            .getExperiment()));

        POIExcelUtils.createCell(row, 7, normalStyle, df
            .format(IsoelectricPointCalculator.getPI(seq)));

        POIExcelUtils.createCell(row, 8, normalStyle, df.format(peptide
            .getRatio()));

        POIExcelUtils.createCell(row, 9, normalStyle, peptide.getNote());
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

  public static void main(String[] args) throws Exception {
    RelexExcelReportBuilder reporter = new RelexExcelReportBuilder(
        "http://192.168.22.100:8080/msms",
        "Test",
        SequenceDatabaseType.SWISSPROT,
        "\\\\192.168.88.249\\work\\MaDJ\\2d_icat_pn_cn\\all summary\\all summary.Labeled.peptides");
    System.out
        .println(reporter
            .process("\\\\192.168.88.249\\work\\MaDJ\\2d_icat_pn_cn\\RelEx-Output_2.2.txt"));
  }

}
