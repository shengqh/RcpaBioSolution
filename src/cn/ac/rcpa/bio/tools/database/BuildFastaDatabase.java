package cn.ac.rcpa.bio.tools.database;

/**
 * <p>Title: BuildFastaDatabase</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: cn.ac.rcpa</p>
 * <p>Create: 2003-12-9</p>
 * @author Sheng Quanhu(shengqh@gmail.com/qhsheng@sibs.ac.cn)
 * @version 1.0.2
 * 通过创建临时表方法解决TaxonomyID过多导致SQL语句无法运行的问题。
 * 调用的是buildDatabaseByTempTable->getDataSet(String tblName)
 * @version 1.0.1
 * 将级联得到TaxonomyID和根据TaxonomyID列表构建数据库分离。
 * @version 1.0.0
 * 级联得到所有TaxonomyID，从数据库中得到相关的蛋白。
 */

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import cn.ac.rcpa.bio.database.AbstractDBApplication;
import cn.ac.rcpa.bio.database.RcpaDBFactory;
import cn.ac.rcpa.bio.database.RcpaDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.models.IInterruptable;
import cn.ac.rcpa.utils.RcpaStringUtils;

public class BuildFastaDatabase
    extends AbstractDBApplication implements IFileProcessor, IInterruptable {
  public static final String version = "1.0.3";

  private String taxonomyID;
  public BuildFastaDatabase(String taxonomyID) {
    super(RcpaDBFactory.getInstance().getConnection(RcpaDatabaseType.ANNOTATION));
    this.taxonomyID = taxonomyID;
  }

  public void buildDatabase(String taxonomyID, String[] removeTaxonomyID,
                            String resultFileName) throws
      SQLException, InterruptedException, IOException {
    List<String> taxonomyIds = getTaxonomyIds(taxonomyID, removeTaxonomyID);
    buildDatabase( (String[]) taxonomyIds.toArray(new String[0]),
                  resultFileName);
  }

  public void buildDatabase(String[] taxonomyIDs, String resultFileName) throws
      SQLException, InterruptedException, IOException {
    List taxonomyIdList = Arrays.asList(taxonomyIDs);

    System.out.println("Total taxonomy ID list :");
    System.out.println(taxonomyIdList);

    buildDatabaseByTempTable(taxonomyIdList, resultFileName);
  }

  private void buildDatabaseByTempTable(List taxonomyIdList,
                                        String resultFileName) throws
      SQLException, IOException, InterruptedException {
    final String tblName = "TEMP.TAXONOMYIDS_" +
        new SimpleDateFormat("yyyyMMddhhmmss").format(new java.util.Date());
    final String sql = "CREATE TABLE " + tblName +
        " (TAXONOMY INTEGER PRIMARY KEY)";
    connection.createStatement().execute(sql);

    PreparedStatement ps = connection.prepareStatement("INSERT INTO " +
        tblName + " VALUES(?)");
    for (int i = 0; i < taxonomyIdList.size(); i++) {
      checkInterrupted();
      ps.setString(1, (String) taxonomyIdList.get(i));
      ps.execute();
    }

    checkInterrupted();

    System.out.println("Getting protein sequence");
    ResultSet rs = getDataSet(tblName);

    PrintWriter out = new PrintWriter(new FileWriter(resultFileName), true);
    String comment = "";
    String sequence = "";
    int iLastSequenceID = 0;
    while (rs.next()) {

      checkInterrupted();

      if (iLastSequenceID == 0) {
        iLastSequenceID = rs.getInt(1);
        comment =
            rs.getString(2)
            + "|"
            + (new BufferedReader(rs.getCharacterStream(3))).readLine();
        sequence = (new BufferedReader(rs.getCharacterStream(4))).readLine();
        continue;
      }

      int iCurSequenceID = rs.getInt(1);
      String curComment =
          rs.getString(2)
          + "|"
          + (new BufferedReader(rs.getCharacterStream(3))).readLine();

      if (iCurSequenceID == iLastSequenceID) {
        comment = comment + " ! " + curComment;
        continue;
      }

      out.println(">" + comment);
      out.println(RcpaStringUtils.warpString(sequence, 70));

      iLastSequenceID = iCurSequenceID;
      comment = curComment;
      sequence = (new BufferedReader(rs.getCharacterStream(4))).readLine();
    }

    if (0 != comment.length()) {
      out.println(">" + comment);
      out.println(RcpaStringUtils.warpString(sequence, 70));
    }
    out.close();

    System.out.println("Writing to " + resultFileName + " finished!");
  }

  private ResultSet getDataSet(String taxonomyIdTableName) throws SQLException {
    String sql = "SELECT "
        + "seq.ID,"
        + "tog.GI_NUMBER,"
        + "uni_id.COMMENTS,"
        + "seq.SEQUENCE,"
        + "uni_id.DEFAULT "
        + "FROM "
        + "PROTEIN.PROTEINSEQUENCE as seq, "
        + "PROTEIN.PROTEINUNIQUEIDS as uni_id,"
        + "ANNOTATION.TAXONOMY_OTM_GI as tog,"
        + taxonomyIdTableName + " as tmpid "
        + "WHERE "
        + "tog.TAXONOMY = tmpid.TAXONOMY "
        + "AND uni_id.PROTEIN_UNIQUE_ID = tog.GI_NUMBER "
        + "AND seq.ID = uni_id.PROTEIN_SEQUENCE "
        + "ORDER BY "
        + "seq.ID, uni_id.DEFAULT";

    return connection.createStatement().executeQuery(sql);
  }

  private List<String> getTaxonomyIds(String tid, String[] removeIds) throws
      SQLException, InterruptedException {
    System.out.print("Getting taxonomy list of " + tid + " ... ");

    List<String> result = new ArrayList<String> ();
    List<String> temp = new ArrayList<String> ();

    List<String> removeIdList = Arrays.asList(removeIds);

    temp.add(tid);
    while (temp.size() > 0) {
      final String id = (String) temp.get(0);
      temp.remove(0);
      if (removeIdList.contains(id)) {
        continue;
      }

      result.add(id);
      System.out.print(".");

      final Statement stmt = connection.createStatement();
      final ResultSet rs =
          stmt.executeQuery("select id from taxonomy where parent_id=" + id);
      while (rs.next()) {
        checkInterrupted();
        temp.add(rs.getString(1));
      }
    }

    System.out.println("finished");
    return result;
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      throw new IllegalArgumentException(
          "BuildFastaDatabase taxonomyid resultfilename");
    }

    BuildFastaDatabase bfd = new BuildFastaDatabase(args[0]);
    try {
      bfd.process(args[1]);
    }
    finally {
      bfd.disconnect();
    }
  }

  public List<String> process(String resultFileName) throws Exception {
    interrupted = false;

    HashSet<String> taxonomyIds = new HashSet<String> ();

    String[] rootTaxonomyIds = taxonomyID.split("[\\s,;]+");
    for (String rootTaxonomyId : rootTaxonomyIds) {
      taxonomyIds.addAll(getTaxonomyIds(rootTaxonomyId, new String[0]));
    }

    buildDatabase( (String[]) taxonomyIds.toArray(new String[0]),
                  resultFileName);

    return Arrays.asList(new String[] {resultFileName});
  }

  protected boolean interrupted = false;

  public void interrupt() {
    interrupted = true;
  }

  protected void checkInterrupted() throws InterruptedException {
    if (interrupted) {
      throw new InterruptedException("User interrupted");
    }
  }
}
