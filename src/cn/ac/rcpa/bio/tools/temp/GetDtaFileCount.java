package cn.ac.rcpa.bio.tools.temp;

import java.io.File;

import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class GetDtaFileCount {
  public GetDtaFileCount() {
  }

  public static void main(String[] args) {
    File rootDir = new File("\\\\192.168.88.249\\work\\sqh\\hippocampi_3.04");
    File[] subDirs = rootDir.listFiles();
    int itotalDtaCount = 0;
    for(File dir:subDirs){
      if (dir.isFile()){
        continue;
      }
      File[] dtaFiles = dir.listFiles(new SpecialIOFileFilter ("dta",true));
      itotalDtaCount += dtaFiles.length;
      System.out.println(dir.getName() + "\t" + dtaFiles.length);
    }

    System.out.println("Total dta count=" + itotalDtaCount);
  }
}
