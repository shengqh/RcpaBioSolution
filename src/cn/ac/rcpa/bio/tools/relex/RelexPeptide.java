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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelexPeptide {
  private static Pattern pattern;

  protected static Pattern getPattern() {
    if (null == pattern) {
      pattern = Pattern
          .compile("([R|S])\\t(\\S)\\t([^\\t]+)\\t([\\d\\.]+)\\t([^\\t]+)\\t([^\\t]+)\\t([\\d\\.]+)\\t(.*)");
    }
    return pattern;
  }

  private char unknown1;

  private char unknown2;

  private String sequence;

  private double ratio;

  private String chroFile;

  private String chroDir;

  private double unknown3;

  private String note;

  public RelexPeptide() {
  }

  public static RelexPeptide parse(String line) {
    Matcher matcher = getPattern().matcher(line);
    if (!matcher.find()) {
      throw new IllegalStateException(line + " is not a valid RelexPeptide");
    }

    RelexPeptide result = new RelexPeptide();
    result.setUnknown1(matcher.group(1).charAt(0));
    result.setUnknown2(matcher.group(2).charAt(0));
    result.setSequence(matcher.group(3));
    result.setRatio(Double.parseDouble(matcher.group(4)));
    result.setChroFile(matcher.group(5));
    result.setChroDir(matcher.group(6));
    result.setUnknown3(Double.parseDouble(matcher.group(7)));
    result.setNote(matcher.group(8));

    return result;
  }

  public String getChroDir() {
    return chroDir;
  }

  public void setChroDir(String chroDir) {
    this.chroDir = chroDir;
  }

  public String getChroFile() {
    return chroFile;
  }

  public void setChroFile(String chroFile) {
    this.chroFile = chroFile;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public double getRatio() {
    return ratio;
  }

  public void setRatio(double ratio) {
    this.ratio = ratio;
  }

  public String getSequence() {
    return sequence;
  }

  public void setSequence(String sequence) {
    this.sequence = sequence;
  }

  public char getUnknown1() {
    return unknown1;
  }

  public void setUnknown1(char unknown1) {
    this.unknown1 = unknown1;
  }

  public char getUnknown2() {
    return unknown2;
  }

  public void setUnknown2(char unknown2) {
    this.unknown2 = unknown2;
  }

  public double getUnknown3() {
    return unknown3;
  }

  public void setUnknown3(double unknown3) {
    this.unknown3 = unknown3;
  }

  public static boolean isRelexPeptide(String line) {
    return getPattern().matcher(line).find();
  }
}
