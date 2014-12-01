package cn.ac.rcpa.bio.tools.temp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import cn.ac.rcpa.utils.RcpaFileUtils;

public class GetColumnFromFile {
  public GetColumnFromFile() {
  }

  public static void main(String[] args) throws FileNotFoundException,
      IOException {
    String peptideFilename = "F:\\Science\\Data\\HLPP\\HLPP_RCPA\\Shotgun_Merged\\HLPP_total.peptides";
    BufferedReader br = new BufferedReader(new FileReader(peptideFilename));
    String line;
    br.readLine();
    HashSet<String> peptides = new HashSet<String>();
    while((line = br.readLine()) != null){
      String[] parts = line.split("\t");
      if (parts.length > 5){
        peptides.add(parts[2]);
      }
    }
    br.close();

    ArrayList<String> seqs = new ArrayList<String>(peptides);
    Collections.sort(seqs);
    PrintStream ps = new PrintStream(RcpaFileUtils.changeExtension(peptideFilename, ".peptides.unique2"));
    for(String seq:seqs){
      ps.println(seq);
    }
    ps.close();
  }
}
