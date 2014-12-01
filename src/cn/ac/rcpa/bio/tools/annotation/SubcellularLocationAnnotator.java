package cn.ac.rcpa.bio.tools.annotation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.annotation.ISubcellularLocationQuery;
import cn.ac.rcpa.bio.annotation.impl.SubcellularLocationIPIQuery;
import cn.ac.rcpa.bio.annotation.impl.SubcellularLocationSwissProtQuery;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.RcpaDBFactory;
import cn.ac.rcpa.bio.database.RcpaDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class SubcellularLocationAnnotator
    implements IFileProcessor {
  public static final String version = "1.0.3";

  private SequenceDatabaseType dbType;

  public SubcellularLocationAnnotator(SequenceDatabaseType dbType) {
    this.dbType = dbType;
  }

  public List<String> process(String originFile) throws Exception {
    final File fastaFile = new File(originFile);
    final ISubcellularLocationQuery query;
    if (dbType == SequenceDatabaseType.IPI) {
      query = new SubcellularLocationIPIQuery(
          RcpaDBFactory.getInstance().getConnection(RcpaDatabaseType.ANNOTATION));
    }
    else if (dbType == SequenceDatabaseType.SWISSPROT) {
      query = new SubcellularLocationSwissProtQuery(
          RcpaDBFactory.getInstance().getConnection(RcpaDatabaseType.ANNOTATION));
    }
    else {
      throw new IllegalStateException(
          "Cannot get subcellular location of DatabaseType " + dbType);
    }

    List<String> acs = SequenceUtils.getAccessNumbers(fastaFile, dbType);

    Map<String,
        String>
        map = query.getSubcellularLocation(acs.toArray(new String[0]));

    Collections.sort(acs);

    ArrayList<String> result = new ArrayList<String> ();

    final String resultFile = fastaFile.getAbsolutePath() + ".subcellular";
    result.add(resultFile);

    PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
    try {
      pw.println("Accession\tSubLocation");
      for (String accessNumber : acs) {
        String sl = map.get(accessNumber);
        if (sl != null) {
          pw.println(accessNumber + "\t" + sl);
        }
        else {
          pw.println(accessNumber + "\t");
        }
      }
    }
    finally {
      pw.close();
    }
    return result;
  }
}
