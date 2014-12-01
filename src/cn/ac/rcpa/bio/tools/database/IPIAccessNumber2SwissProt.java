package cn.ac.rcpa.bio.tools.database;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.database.IPIAccessNumber2SwissProtQuery;
import cn.ac.rcpa.bio.database.RcpaDBFactory;
import cn.ac.rcpa.bio.database.RcpaDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.AccessNumberUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class IPIAccessNumber2SwissProt
    implements IFileProcessor {
  public static final String version = "1.0.0";

  public IPIAccessNumber2SwissProt() {
  }

  public List<String> process(String originFile) throws Exception {
    final IPIAccessNumber2SwissProtQuery query = new
        IPIAccessNumber2SwissProtQuery(RcpaDBFactory.getInstance().
                                       getConnection(RcpaDatabaseType.
        ANNOTATION));

    final String[] ipiAcs = AccessNumberUtils.loadFromFile(originFile, false);

    final Map<String, String> ipi2spMap = query.getIPI2SwissProtMap(ipiAcs);

    final String resultFile = RcpaFileUtils.changeExtension(originFile, ".ipi2sp");

    final PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
    try {
      pw.println("IPI\tSwissProt");
      for (String ipi : ipi2spMap.keySet()) {
        pw.println(ipi + "\t" + ipi2spMap.get(ipi));
      }
    }
    finally {
      pw.close();
    }
    return Arrays.asList(new String[] {resultFile});
  }
}
