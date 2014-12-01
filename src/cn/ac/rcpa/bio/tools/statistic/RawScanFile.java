package cn.ac.rcpa.bio.tools.statistic;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;

import cn.ac.rcpa.utils.XMLFile;

public class RawScanFile {
  private RawScanFile() {
  }

  /**
   * Return Map<RawFileName,
   *            Map<ScanNumber,
   *                ScanIndexAfterFullMS>>
   *
   * @param scanFilename String
   * @return Map
   * @throws NumberFormatException
   * @throws JDOMException
   */
  public static Map<String, Map<Integer,
      Integer>> getRawScanMap(String scanFilename) throws Exception {
    XMLFile scanFile = new XMLFile(scanFilename);
    Element[] rawFiles = scanFile.getElements("","RawFile");

    Map<String, Map<Integer, Integer>> rawfileMap = new HashMap<String, Map<Integer, Integer>>();
    for(Element rawfile:rawFiles){
      final String rawfilename = rawfile.getAttributeValue("filename");
      Map<Integer, Integer> scanNumberMap = new HashMap<Integer, Integer>();
      Element[] scans = scanFile.getElements(rawfile,"","Scan");
      for(Element scan:scans){
        scanNumberMap.put(Integer.parseInt(scan.getAttributeValue("id")), Integer.parseInt(scan.getAttributeValue("index") ));
      }
      rawfileMap.put(rawfilename, scanNumberMap);
    }
    return rawfileMap;
  }

}
