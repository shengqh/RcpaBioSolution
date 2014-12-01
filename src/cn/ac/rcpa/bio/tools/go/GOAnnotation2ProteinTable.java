package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class GOAnnotation2ProteinTable
    implements IFileProcessor {
  private List<IGOEntry> entries;
  private String treeFile;
  private SequenceDatabaseType dbType;

  public GOAnnotation2ProteinTable(IGOEntry[] entries, String treeFile,
                                   SequenceDatabaseType dbType) {
    this.entries = new ArrayList<IGOEntry> (Arrays.asList(entries));
    this.treeFile = treeFile;
    this.dbType = dbType;
  }

  private List<Boolean> getList(Map<String, IGOEntry> map, String protein) {
    List<Boolean> result = new ArrayList<Boolean> ();
    for (IGOEntry entry : entries) {
      if (!map.containsKey(entry.getAccession())) {
        result.add(Boolean.FALSE);
      }
      else {
        GOAClassificationEntry goac = (GOAClassificationEntry) map.get(
            entry.getAccession());
        result.add(goac.getProteins().contains(protein));
      }
    }

    return result;
  }

  public List<String> process(String fastaFile) throws Exception {
    GOAClassificationEntry goa = new GOAClassificationEntry();
    goa.loadFromFile(treeFile);
    Map<String, IGOEntry> entryMap = goa.getGOEntryMap();
    List<String>
        acs = SequenceUtils.getAccessNumbers(new File(fastaFile), dbType);

    String resultFile = fastaFile + ".goa.txt";
    PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
    try {
      pw.print("Accession");
      for (IGOEntry entry : entries) {
//        pw.print("\t" + entry.getAccession());
        pw.print("\t" + entry.getName());
      }
      pw.println();

      for (String ac : acs) {
        pw.print(ac);
        List<Boolean> acGoa = getList(entryMap, ac);
        for (Boolean exist : acGoa) {
          pw.print("\t" + exist);
        }
        pw.println();
      }
    }
    finally {
      pw.close();
    }

    return Arrays.asList(new String[] {resultFile});
  }
}
