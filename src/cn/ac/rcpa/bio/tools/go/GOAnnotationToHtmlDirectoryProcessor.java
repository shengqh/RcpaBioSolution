package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class GOAnnotationToHtmlDirectoryProcessor implements IFileProcessor {
  public static final String version = "1.0.0";
  public GOAnnotationToHtmlDirectoryProcessor() {
  }

  public List<String> process(String originFile) throws Exception {
    ArrayList<String> result = new ArrayList<String>();
    final File[] pictures = new File(originFile).listFiles(new SpecialIOFileFilter("png",true));
    final File[] treeFiles = new File(originFile).listFiles(new SpecialIOFileFilter("special.tree",true));

    for(File treeFile:treeFiles){
      final String treeFilename = treeFile.getName().toLowerCase();
      boolean bFound = false;
      for(File picture:pictures){
        final String filename = RcpaFileUtils.changeExtension(picture.getName(),"").toLowerCase();
        if (treeFilename.startsWith(filename)){
          bFound = true;
          result.addAll(new GOAnnotationToHtml(picture.getName()).process(treeFile.getAbsolutePath()));
          break;
        }
      }

      if (!bFound){
        result.addAll(new GOAnnotationToHtml().process(treeFile.getAbsolutePath()));
      }
    }
    return result;
  }
}
