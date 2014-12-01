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

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.ParserComposite;
import cn.ac.rcpa.bio.processor.IFileProcessor;

public abstract class AbstractReporter<T> implements IFileProcessor {

  private ParserComposite<T> parsers = new ParserComposite<T>("\t");

  public List<String> process(String originFile) throws Exception {
    List<T> values = readFromFile(originFile);
    String resultFile = originFile + ".report";
    PrintWriter pw = new PrintWriter(resultFile);
    pw.println(parsers.getTitle());
    for (T value : values) {
      pw.println(parsers.getValue(value));
    }
    pw.close();

    return Arrays.asList(new String[] { resultFile });
  }

  abstract protected List<T> readFromFile(String originFile) throws Exception;

  public ParserComposite<T> getParsers() {
    return parsers;
  }

}
