package cn.ac.rcpa.bio.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import cn.ac.iped.xml.objects.DBSearch;
import cn.ac.iped.xml.objects.DBSearchParameters;
import cn.ac.iped.xml.objects.DataContainer;
import cn.ac.iped.xml.objects.MSMSDBSearchResult;
import cn.ac.iped.xml.objects.OntologyEntry;
import cn.ac.iped.xml.objects.PeakList;
import cn.ac.iped.xml.objects.PeptideHit;
import cn.ac.iped.xml.objects.Protein;
import cn.ac.iped.xml.objects.ProteinHit;
import cn.ac.iped.xml.objects.RawFile;
import cn.ac.iped.xml.objects.types.PeakListType;
import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.DatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IIdentifiedProtein;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.options.BuildSummaryOption;
import cn.ac.rcpa.bio.sequest.SequestFilename;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class BuildSummaryToIPED {
  static String dbsearch_id;

  public BuildSummaryToIPED() {
  }

  public static void main(String[] args) throws Exception {
    convertByParamFile(new File("F:\\Science\\Data\\HLPP\\20041012\\1D_LC_Shotgun\\1D_LC_Shotgun.param"), DatabaseType.IPI );
    convertByParamFile(new File("F:\\Science\\Data\\HLPP\\20041012\\2D_LC_salt_offline\\2D_LC_salt_offline.param"), DatabaseType.IPI );
    convertByParamFile(new File("F:\\Science\\Data\\HLPP\\20041012\\2D_LC_salt_online\\2D_LC_salt_online.param"), DatabaseType.IPI );
    convertByParamFile(new File("F:\\Science\\Data\\HLPP\\20041012\\2D_nano_shotgun\\2D_nano_shotgun.param"), DatabaseType.IPI );
  }

  /**
   * convertByParamFile
   *
   * @param file File
   */
  private static void convertByParamFile(File file, DatabaseType dbType) throws
      FileNotFoundException, ValidationException, MarshalException,
      IOException,  RcpaParseException {
    BuildSummaryOption bso = (BuildSummaryOption) BuildSummaryOption.unmarshal(new
        FileReader(file));

    String noredundantFile = RcpaFileUtils.changeExtension(file.getAbsolutePath(),
        "noredundant");
    System.out.println("Reading BuildSummary noredundant file " + noredundantFile + "...");
    BuildSummaryResult sr = IdentifiedResultIOFactory.readBuildSummaryResult(
        noredundantFile);
    System.out.println("ok!");

    DataContainer dc = new DataContainer();

    System.out.println("Parsing database searching parameters...");
    dc.setDBSearch(getDBSearch(bso));
    System.out.println("ok!");

    System.out.println("Parsing identified protein informations...");
    dc.setProteinHit(getProteinHit(bso, sr, dbType));
    System.out.println("ok!");

    System.out.println("Writing DataContainer file...");
    final String dataContainerFile = RcpaFileUtils.changeExtension(file.getAbsolutePath(),
        "DataContainer");
    dc.marshal(new FileWriter(dataContainerFile));
    System.out.println("ok!");

    System.out.println("Parsing raw file informations...");
    Map rawFiles = getRawFiles(bso, sr, dbType);
    System.out.println("ok!");

    System.out.println("Writing RawFile file...");
    for (Iterator iter = rawFiles.keySet().iterator(); iter.hasNext(); ) {
      String experimental = (String)iter.next();
      RawFile rawFile = (RawFile)rawFiles.get(experimental);
      final String rawFileName = RcpaFileUtils.changeExtension(file.getAbsolutePath(),
          experimental + ".RawFile");
      rawFile.marshal(new FileWriter(rawFileName));
    }
    System.out.println("ok!");
  }

  private static Map getRawFiles(BuildSummaryOption bso,
                                    BuildSummaryResult sr,
                                    DatabaseType dbType)
       {
    IAccessNumberParser parser = AccessNumberParserFactory.getParser(dbType);

    //保留所有可能的proids
    List prohits = sr.getProteins();
    HashSet<String> proids = new HashSet<String>();
    for (Iterator iter = prohits.iterator(); iter.hasNext(); ) {
      IIdentifiedProtein protein = (IIdentifiedProtein)iter.next();
      proids.add(getProteinHitID(parser, protein.getProteinName()));
    }

    System.out.println("There are " + sr.getPeptideHits().size() + " peak lists!");

    BuildSummaryPeptide[] pephits = sr.getDuplicatedPeptideHit();
    HashMap<String, PeakList> peakLists = new HashMap<String, PeakList>();
    HashMap<String, RawFile> rawFiles = new HashMap<String, RawFile>();
    for(int i = 0;i < pephits.length;i++){
      if (i % 1000 == 0){
        System.out.print(".");
      }

      final BuildSummaryPeptide sph = pephits[i];
      final SequestFilename sname = (SequestFilename)sph.getPeakListInfo();

      //根据Experimental决定RawFile
      if (!rawFiles.containsKey(sname.getExperiment())){
        rawFiles.put(sname.getExperiment(), new RawFile());
      }
      RawFile rawFile = rawFiles.get(sname.getExperiment());

      //根据Filename决定PeakList
      if (!peakLists.containsKey(sname.getLongFilename())){
        PeakList pl = new PeakList();
        peakLists.put(sname.getLongFilename(), pl);
        rawFile.addPeakList(pl);
      }
      PeakList pl = (PeakList)peakLists.get(sname.getLongFilename());

      pl.setList_type(PeakListType.MSMS_RESULT);
      pl.setMass_value_type("Monoisotopic");
      pl.setTitle(sname.getLongFilename() + "dta");

      MSMSDBSearchResult dbsr = new MSMSDBSearchResult();
      dbsr.setDbsearch_id(dbsearch_id);
      pl.addMSMSDBSearchResult(dbsr);

      //设置dta文件和out文件路径
      String[] dirs = bso.getRunOption().getDirectorySet().getDirectory();
      for(int j = 0;j < dirs.length;j++){
        final File dtaFile = new File(dirs[j],sname.getLongFilename() + "dta");
        if (dtaFile.exists()){
          pl.setPeak_file(dtaFile.getAbsolutePath());

          final File outFile = new File(dirs[j],sname.getLongFilename() + "out");
          dbsr.setResult_file(outFile.getAbsolutePath());
          break;
        }
      }

      PeptideHit pephit = new PeptideHit();
      pephit.setRank(1);
      pephit.setScore(sph.getXcorr());
      pephit.setDelta_score(sph.getDeltacn());
      pephit.setScore_type("SEQUEST");
      pephit.setSequence(sph.getSequence());
      for(int j = 0;j < sph.getProteinNameCount();j++){
        final String proid = getProteinHitID(parser, sph.getProteinName(j));
        //如果proid在当前proteinid列表中，保存。
        if (proids.contains(proid)){
          pephit.addProtein_hit_id(proid);
        }
      }
      dbsr.addPeptideHit(pephit);
    }
    System.out.println(".");

    return rawFiles;
  }

  /**
   * getProteinHitID
   *
   * @param string String
   * @return int
   */
  private static String getProteinHitID(IAccessNumberParser parser, String proteinName)
       {
    final String accessNumber = parser.getValue(proteinName);
    return "proteinhit_" + accessNumber;
  }

  /**
   * getProteinHitSet
   *
   * @param bso BuildSummaryOption
   * @param sr BuildSummaryResult
   * @return ProteinHitSet
   */
  private static ProteinHit[] getProteinHit(BuildSummaryOption bso,
                                                BuildSummaryResult sr,
                                                DatabaseType dbType)  {
    IAccessNumberParser parser = AccessNumberParserFactory.getParser(dbType);

    List prohits = sr.getProteins();
    System.out.println("There are " + prohits.size() + " identified proteins!");

    ArrayList<ProteinHit> result = new ArrayList<ProteinHit>(prohits.size());
    for (Iterator iter = prohits.iterator(); iter.hasNext(); ) {
      ProteinHit ph = new ProteinHit();
      BuildSummaryProtein sph = (BuildSummaryProtein)iter.next();
      String accessNumber = getProteinHitID(parser, sph.getProteinName());
      ph.setIdentity(accessNumber);
      ph.setScore(0.0);
      ph.setCoverage(sph.getCoverage());

      Protein pro = new Protein();
      pro.setAccession_number(accessNumber);
      pro.setDescription(sph.getReference());
      pro.setPredicted_mass(sph.getMW());
      pro.setPredicted_pi(sph.getPI());
      pro.setSequence(sph.getSequence());

      ph.setProtein(pro);
      result.add(ph);
    }


    return (ProteinHit[])result.toArray(new ProteinHit[0]);
  }

  private static DBSearch[] getDBSearch(BuildSummaryOption bso) {
    DBSearch result = new DBSearch();

    result.setSearching_date(bso.getDateTime());
    result.setExperimenter("Experimenter");
    final String database = RcpaFileUtils.changeExtension(new File(bso.getRunOption().getDatabase()).getName(), "");

    final DateFormat df = DateFormat.getDateInstance();
    dbsearch_id = "dbsearch_" + database + "_" + df.format(bso.getDateTime());
    result.setIdentity(dbsearch_id);

    result.setDBSearchParameters(getDBSearchParameters(bso));

    return new DBSearch[]{result};
  }

  private static DBSearchParameters getDBSearchParameters(BuildSummaryOption bso) {
    DBSearchParameters result = new DBSearchParameters();

    result.setDescription("Detail of DBSearch Parameters");
    result.setProgram("Sequest");
    result.setDatabase_name(new File(bso.getRunOption().getDatabase()).getName());
    result.setDatabase_version(new File(bso.getRunOption().getDatabase()).getName());

    final String sequestParamFile = bso.getRunOption().getDirectorySet().getDirectory(0) + File.separator + "sequest.params";
    result.setParameters_file(sequestParamFile);
    result.setPeptide_mass_tolerance(3.0);
    result.setMax_missed_cleavages(2);
    result.setFixed_modifications("C=57.0215");

    if (bso.getPeptideFilter().getXCorr().getFiltered()){
      OntologyEntry oe = new OntologyEntry();
      oe.setCategory("Min_XCorr_Charge_1");
      oe.setEntry_value(Double.toString(bso.getPeptideFilter().getXCorr().getMin1XCorr()));
      result.addOntologyEntry(oe);

      oe = new OntologyEntry();
      oe.setCategory("Min_XCorr_Charge_2");
      oe.setEntry_value(Double.toString(bso.getPeptideFilter().getXCorr().getMin2XCorr()));
      result.addOntologyEntry(oe);

      oe = new OntologyEntry();
      oe.setCategory("Min_XCorr_Charge_3");
      oe.setEntry_value(Double.toString(bso.getPeptideFilter().getXCorr().getMin3XCorr()));
      result.addOntologyEntry(oe);
    }

    if (bso.getPeptideFilter().getDeltaCN().getFiltered()){
      OntologyEntry oe = new OntologyEntry();
      oe.setCategory("Min_DeltaCn");
      oe.setEntry_value(Double.toString(bso.getPeptideFilter().getDeltaCN().getMinDeltaCN()));
      result.addOntologyEntry(oe);
    }

    if (bso.getPeptideFilter().getSpRank().getFiltered()){
      OntologyEntry oe = new OntologyEntry();
      oe.setCategory("Max_SpRank");
      oe.setEntry_value(Double.toString(bso.getPeptideFilter().getSpRank().getMaxSpRank()));
      result.addOntologyEntry(oe);
    }

    return result;
  }
}
