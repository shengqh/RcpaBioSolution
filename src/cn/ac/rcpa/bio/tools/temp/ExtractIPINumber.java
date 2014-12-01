package cn.ac.rcpa.bio.tools.temp;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class ExtractIPINumber {
  public ExtractIPINumber() {
  }

  public static void main(String[] args) throws Exception {
    List<String> ipis = SequenceUtils.getAccessNumbers(new File("F:\\Science\\Data\\MouseLiver\\all\\mouse_liver_all.proteins.fasta"), SequenceDatabaseType.IPI);
    PrintWriter pw = new PrintWriter(new FileWriter("F:\\Science\\Data\\MouseLiver\\all\\mouse_liver_all.proteins.IPI"));
    try{
      for(String ipi:ipis){
        pw.println(ipi);
      }
    }
    finally{
      pw.close();
    }
  }
}
