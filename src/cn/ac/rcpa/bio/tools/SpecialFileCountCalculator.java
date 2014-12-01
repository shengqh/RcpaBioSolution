package cn.ac.rcpa.bio.tools;

import java.io.File;

import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class SpecialFileCountCalculator {
  public SpecialFileCountCalculator() {
  }

  public static int getSpecialFileCount(File directory, String fileExtension){
    File[] files = directory.listFiles(new SpecialIOFileFilter(fileExtension, true));
    return files.length;
  }

  public static int getSpecialFileCountInSubDirectories(File directory, String fileExtension){
    int result = 0;
    File[] dirs = RcpaFileUtils.getSubDirectories(directory);
    for (int i = 0; i < dirs.length; i++) {
      System.out.print(dirs[i]);

      int count = getSpecialFileCount(dirs[i], fileExtension);

      System.out.println("\t" + count);

      result += count;
    }
    return result;
  }

  public static void main(String[] args) {
    int lcqCount = getSpecialFileCountInSubDirectories(new File("\\\\192.168.0.102\\service2\\Daijie\\HPPP\\IPI\\2D_LCQ"), "out");
    System.out.println("LCQ_OUT_FILE Count = " + lcqCount);
    int ltqCount = getSpecialFileCountInSubDirectories(new File("\\\\192.168.0.102\\service2\\Daijie\\HPPP\\IPI\\2D_LTQ"), "out");
    System.out.println("LTQ_OUT_FILE Count = " + ltqCount);
  }
}
