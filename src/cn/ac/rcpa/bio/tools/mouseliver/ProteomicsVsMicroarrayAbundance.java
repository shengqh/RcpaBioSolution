package cn.ac.rcpa.bio.tools.mouseliver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.proteomics.ProteinInfoType;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultProteinInfoReader;

public class ProteomicsVsMicroarrayAbundance {
  public ProteomicsVsMicroarrayAbundance() {
  }

  public static void main(String[] args) throws Exception {
    final String microarrayFile =
        "F:\\Science\\Data\\MouseLiver\\microarray\\all_probes_ipi_a.TXT";

    final Map<String,
        MicroArrayAbundance>
        microAbundance = getMicroArrayAbundanceMap(microarrayFile);

    final String proteinFile =
        "X:\\summary\\mouse_liver\\all\\mouse_liver_all.proteins";
    final Map<String, Map<ProteinInfoType, String>> proteomicsAbundance = new
        BuildSummaryResultProteinInfoReader(SequenceDatabaseType.IPI,
                                            Arrays.asList(new ProteinInfoType[] {
        ProteinInfoType.PepCount, ProteinInfoType.UniquePepCount,
        ProteinInfoType.CoverPercent})).read(proteinFile);

    final String resultFilename = microarrayFile + ".proteomics";
    writeResultFile(microAbundance, proteomicsAbundance, resultFilename);
  }

  private static void writeResultFile(Map<String,
                                      MicroArrayAbundance> microAbundance,
                                      Map<String, Map<ProteinInfoType,
                                      String>> proteomicsAbundance,
                                      String resultFilename) throws IOException {
    PrintWriter pw = new PrintWriter(new FileWriter(resultFilename));
    pw.println("probe\tIPI_NUMBER\tAverageFold\t" +
               ProteinInfoType.PepCount + "\t" +
               ProteinInfoType.UniquePepCount + "\t" +
               ProteinInfoType.CoverPercent);
    for (String ipi : microAbundance.keySet()) {
      final MicroArrayAbundance curr = microAbundance.get(ipi);

      Map<ProteinInfoType, String> infoMap = proteomicsAbundance.get(ipi);
      if (infoMap != null) {
        pw.print(curr.getProbe() + "\t" + curr.getProtein() + "\t" +
                 curr.getAverageAbundance());
        pw.print("\t" + infoMap.get(ProteinInfoType.PepCount));
        pw.print("\t" + infoMap.get(ProteinInfoType.UniquePepCount));
        pw.print("\t" + infoMap.get(ProteinInfoType.CoverPercent));
        pw.println();
      }
    }
    pw.close();
  }

  private static Map<String,
      MicroArrayAbundance> getMicroArrayAbundanceMap(String microarrayFile) throws
      FileNotFoundException, IOException {
    Map<String, MicroArrayAbundance> microAbundance = new LinkedHashMap<String,
        MicroArrayAbundance> ();

    PrintWriter pw = new PrintWriter(new FileWriter(microarrayFile + ".ambigious"));
    BufferedReader br = new BufferedReader(new FileReader(microarrayFile));

    String line = br.readLine();
    while ( (line = br.readLine()) != null) {
      if (line.trim().length() == 0) {
        break;
      }

      MicroArrayAbundance curr = MicroArrayAbundance.parse(line);

      if (microAbundance.containsKey(curr.getProtein())) {
        microAbundance.get(curr.getProtein()).addAbundance(curr);

        pw.println(microAbundance.get(curr.getProtein()));
        pw.println(line);
        pw.println();
      }
      else {
        microAbundance.put(curr.getProtein(), curr);
      }
    }
    pw.close();

    br.close();
    return microAbundance;
  }
}
