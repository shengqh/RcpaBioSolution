package cn.ac.rcpa.tools;

import java.io.File;

import cn.ac.rcpa.utils.ShellUtils;

/**
 * <p>
 * Title: RCPA Package
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: RCPA.SIBS.AC.CN
 * </p>
 * 
 * @author Sheng Quan-Hu
 * @version 1.0
 */
public class DotFormat {
  private static String dotLocation = "extends/graphviz/dot.exe";
  private static DotFormat instance;

  public static DotFormat getInstance() {
    if (instance == null) {
      instance = new DotFormat();
    }
    return instance;
  }

  private DotFormat() {
    File dot = new File(dotLocation);
    if (!dot.exists()) {
      throw new IllegalStateException("Program " + dot.getAbsolutePath()
          + " is not exists!");
    }
  }

  public void format(String inputFile, String outputFile, String format) {
    String command = dotLocation + " \"" + inputFile + "\" -o\"" + outputFile + "\" -T" + format;
    System.out.println(command);
    if (!ShellUtils.execute(command, true)){
      throw new IllegalStateException("Shell error : dot-format from " + inputFile + " to " + format + "-format" + outputFile);
    }
  } 

  public void dot(String inputFile, String outputFile) {
    format(inputFile, outputFile, "dot");
  } 

  public void png(String dotFile, String pngFile) {
    format(dotFile, pngFile, "png");
  } 

  public static void main(String[] args) throws Exception {
    String inputFile = "data/sample.dot";
    String outputFile = "data/sample-format.dot";
    String pngFile = "data/sample-format.png";
    DotFormat.getInstance().dot(inputFile, outputFile);
    DotFormat.getInstance().png(outputFile, pngFile);
  }
}
