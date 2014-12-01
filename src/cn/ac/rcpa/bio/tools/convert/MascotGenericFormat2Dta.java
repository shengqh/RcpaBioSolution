package cn.ac.rcpa.bio.tools.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.mascot.MascotGenericFormatIterator;
import cn.ac.rcpa.bio.proteomics.sequest.DTAWriter;

/**
 * Company: RCPA.SIBS.AC.CN
 * @author Sheng Quan-Hu
 * @version 1.0.1
 */
public class MascotGenericFormat2Dta implements IFileProcessor {
  public static String version = "1.0.1";

  public MascotGenericFormat2Dta() {
  }

  public List<String> process(String originFile) throws Exception {
    File mgf = new File(originFile);

    MascotGenericFormatIterator iter = new MascotGenericFormatIterator(
        new BufferedReader(new FileReader(mgf)));
    DTAWriter writer = new DTAWriter();

    int ipos = mgf.getName().indexOf('.');
    String experiment = ipos == -1 ? mgf.getName() : mgf.getName().substring(0,
        ipos);
    File dtaDir = new File(mgf.getParentFile(), experiment + ".dta");
    dtaDir.mkdir();
    while (iter.hasNext()) {
      PeakList<Peak> pl = iter.next();

      for (int charge = 2; charge <= 3; charge++) {
        pl.setCharge(charge);
        String dtaFilename = experiment + "." + pl.getFirstScan() + "."
            + pl.getLastScan() + "." + pl.getCharge() + ".dta";
        File dtaFile = new File(dtaDir, dtaFilename);
        PrintStream ps = new PrintStream(new FileOutputStream(dtaFile));
        writer.write(ps, pl);
        ps.close();
      }
    }

    return Arrays.asList(new String[] { dtaDir.getAbsolutePath() });
  }
}
