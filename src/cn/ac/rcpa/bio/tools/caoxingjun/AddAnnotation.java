/*
 * Created on 2005-6-20
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.caoxingjun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class AddAnnotation {

  public static void main(String[] args) throws IOException {
    AddAnnotation add = new AddAnnotation();
    add.append("F:\\Science\\Data\\caoxj\\lep\\ser_gene_anno.xls",
        "F:\\Science\\Data\\caoxj\\lep\\ser.xls");
    add.append("F:\\Science\\Data\\caoxj\\lep\\lep_gene_anno.xls",
        "F:\\Science\\Data\\caoxj\\lep\\Lep_28_37.xls");
    add.append("F:\\Science\\Data\\caoxj\\lep\\lep_gene_anno.xls",
        "F:\\Science\\Data\\caoxj\\lep\\lep_CF_protein.xls");
  }

  public void append(String annotationFilename, String targetFilename)
      throws IOException {
    final Map<String, List<String>> annotations = mergeAnnotation(
        annotationFilename, targetFilename);
    final String resultFilename = targetFilename + ".anno";
    saveResult(resultFilename, annotations);
  }

  public void saveResult(String resultFilename,
      Map<String, List<String>> annotationMap) throws IOException {
    PrintWriter pw = new PrintWriter(new FileWriter(resultFilename));
    try {
      for (String protein : annotationMap.keySet()) {
        pw.print(protein);

        final List<String> annotations = annotationMap.get(protein);
        for (String annotation : annotations) {
          pw.print("\t" + annotation);
        }

        pw.println();
      }
    } finally {
      pw.close();
    }
  }

  /**
   * The annotation file should be in such format: first column is the protein
   * id other columns are annotation information
   *
   * The target file should be in such format: first column is the protein id
   * other columns are other information
   *
   * The annotation information will be append to the origin informations of
   * protein in target file.
   *
   * @param annotationFilename
   *          The file including annotation information
   * @param targetFilename
   *          The file including the protein ids which need toe add annotation
   * @throws IOException
   */
  public Map<String, List<String>> mergeAnnotation(String annotationFilename,
      String targetFilename) throws IOException {
    Map<String, List<String>> annotations = getAnnotation(annotationFilename);
    Map<String, List<String>> result = getAnnotation(targetFilename);
    for (String protein : result.keySet()) {
      if (annotations.containsKey(protein)) {
        result.get(protein).addAll(annotations.get(protein));
      }
    }

    return result;
  }

  private Map<String, List<String>> getAnnotation(String annotationFilename)
      throws FileNotFoundException, IOException {
    try {
      return getExcelAnnotation(annotationFilename);
    } catch (IOException e) {
      return getTextAnnotation(annotationFilename);
    }
  }

  private Map<String, List<String>> getExcelAnnotation(String filename)
      throws FileNotFoundException, IOException {
    System.out.println(filename);
    HSSFSheet annotationSheet = new HSSFWorkbook(new POIFSFileSystem(
        new FileInputStream(new File(filename)))).getSheetAt(0);

    Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
    for (int i = 0; i <= annotationSheet.getLastRowNum(); i++) {
      final HSSFRow curRow = annotationSheet.getRow(i);
      if (curRow.getLastCellNum() == 0) {
        continue;
      }

      List<String> annotations = new ArrayList<String>();
      final String protein = curRow.getCell((short) 0).getStringCellValue();
      result.put(protein, annotations);

      for (int j = 1; j <= curRow.getLastCellNum(); j++) {
        final HSSFCell curCell = curRow.getCell((short) j);
        if (curCell == null) {
          annotations.add("");
        } else {
          annotations.add(curRow.getCell((short) j).getStringCellValue());
        }
      }
    }

    int maxAnnotationCount = 0;
    for (List<String> annotation : result.values()) {
      if (maxAnnotationCount < annotation.size()) {
        maxAnnotationCount = annotation.size();
      }
    }

    for (String protein : result.keySet()) {
      List<String> annotation = result.get(protein);
      for (int i = annotation.size(); i < maxAnnotationCount; i++) {
        annotation.add("");
      }
    }

    return result;
  }

  private Map<String, List<String>> getTextAnnotation(String filename)
      throws FileNotFoundException, IOException {
    System.out.println(filename);
    BufferedReader br = new BufferedReader(new FileReader(filename));

    Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
    String line;
    while ((line = br.readLine()) != null) {
      String[] lines = line.split("\t");
      List<String> annotations = new ArrayList<String>();
      final String protein = lines[0];
      result.put(protein, annotations);

      for (int j = 1; j < lines.length; j++) {
        annotations.add(lines[j]);
      }
    }

    int maxAnnotationCount = 0;
    for (List<String> annotation : result.values()) {
      if (maxAnnotationCount < annotation.size()) {
        maxAnnotationCount = annotation.size();
      }
    }

    for (String protein : result.keySet()) {
      List<String> annotation = result.get(protein);
      for (int i = annotation.size(); i < maxAnnotationCount; i++) {
        annotation.add("");
      }
    }

    return result;
  }

}
