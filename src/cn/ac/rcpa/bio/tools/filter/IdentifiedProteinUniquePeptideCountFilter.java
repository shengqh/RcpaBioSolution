package cn.ac.rcpa.bio.tools.filter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.ProteinInfoType;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultProteinInfoReader;

public class IdentifiedProteinUniquePeptideCountFilter implements IFileProcessor{
  public final static String version = "1.0.0";

  private int uniquePeptideCount;

  private BuildSummaryResultProteinInfoReader reader;
  private static Collection<ProteinInfoType> requiredTypes = Arrays.asList(new ProteinInfoType[]{ProteinInfoType.UniquePepCount});

  public IdentifiedProteinUniquePeptideCountFilter(SequenceDatabaseType dbType, int uniquePeptideCount) {
    this.uniquePeptideCount = uniquePeptideCount;
    reader = new BuildSummaryResultProteinInfoReader(dbType, requiredTypes);
  }

  public List<String> process(String originFile) throws Exception {
    final Map<String, Map<ProteinInfoType, String>> proteinMap = reader.read(originFile);

    final String fileMore = originFile + ".unique" + uniquePeptideCount + "+";
    final PrintWriter pwMore = new PrintWriter(new FileWriter(fileMore));

    final String fileLess = originFile + ".unique" + uniquePeptideCount + "-";
    final PrintWriter pwLess = new PrintWriter(new FileWriter(fileLess));
    try {
      for (String proteinId : proteinMap.keySet()) {
        final String uniquePepCount = proteinMap.get(proteinId).get(ProteinInfoType.
            UniquePepCount);
        if (Integer.parseInt(uniquePepCount) >= uniquePeptideCount) {
          pwMore.println(proteinId);
        }
        else {
          pwLess.println(proteinId);
        }
      }
    }
    finally{
      pwMore.close();
      pwLess.close();
    }
    return Arrays.asList(new String[]{fileMore, fileLess});
  }
}
