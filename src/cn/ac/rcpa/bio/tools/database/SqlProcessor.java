package cn.ac.rcpa.bio.tools.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.database.AbstractDBApplication;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.DatabaseUtils;

public class SqlProcessor
    extends AbstractDBApplication implements IFileProcessor {
  private List<String> sqls;
  public static final String version = "1.0.0";
  private boolean showSql = true;

  public SqlProcessor(Connection connection, String sql) {
    super(connection);
    setSql(sql);
  }

  public void setShowSql(boolean showSql){
    this.showSql = showSql;
  }

  public final void setSql(String sql){
    this.sqls = Arrays.asList(new String[] {sql});
  }

  public SqlProcessor(Connection connection, String[] sqls) {
    super(connection);
    setSql(sqls);
  }

  public final void setSql(String[] sql){
    this.sqls = Arrays.asList(sql);
  }

  public SqlProcessor(Connection connection, List<String> sqls) {
    super(connection);
    setSql(sqls);
  }

  public final void setSql(List<String> sqls){
    this.sqls = new ArrayList<String> (sqls);
  }

  public List<String> process(String originFile) throws Exception {
    sql2txt(sqls, new File(originFile));
    return Arrays.asList(new String[] {originFile});
  }

  protected void sql2txt(List<String> sqls, File resultFile) throws
      SQLException, IOException {
    PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
    try {
      startPrint(pw);
      for (int i = 0; i < sqls.size(); i++) {
        doSql2txt(pw, sqls.get(i));
      }
      endPrint(pw);
    }
    finally {
      pw.close();
    }
  }

  protected void endPrint(PrintWriter pw) {
  }

  protected void startPrint(PrintWriter pw) {
  }

  protected void printResultSet(PrintWriter pw, ResultSet rs) throws
      SQLException {
    printHeader(pw, rs);
    printRecords(pw, rs);
    printFooter(pw,rs);
  }

  protected void doSql2txt(PrintWriter pw, String sql) throws
      SQLException {
    System.out.println(sql);
    ResultSet rs = connection.createStatement().executeQuery(sql);
    try {
      printResultSet(pw, rs);
      if (showSql){
        printSql(pw, sql);
      }
    }
    finally {
      rs.close();
    }
  }

  protected void printHeader(PrintWriter pw, ResultSet rs) throws
      SQLException {
    DatabaseUtils.printHeader(pw, rs);
  }

  protected void printRecords(PrintWriter pw, ResultSet rs) throws SQLException {
    DatabaseUtils.printRecords(pw, rs);
  }

  protected void printFooter(PrintWriter pw, ResultSet rs) {
    return;
  }

  protected void printSql(PrintWriter pw, String sql) {
    pw.println(sql);
    pw.println();
  }
}
