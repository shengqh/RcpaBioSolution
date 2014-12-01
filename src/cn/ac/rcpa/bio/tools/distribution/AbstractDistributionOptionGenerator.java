package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationSet;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide;
import cn.ac.rcpa.bio.tools.distribution.option.SourceFile;
import cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType;

abstract public class AbstractDistributionOptionGenerator {
  public AbstractDistributionOptionGenerator() {
  }

  public File createOptionFile(SourceFile sourceFile, SequenceDatabaseType dbType,
      DistributionType dtbType, double[] ExperimentValues,
      ClassificationInfo classificationInfo, FilterByPeptide filterByPeptide,
      IClassification<IIdentifiedPeptide> sphc) throws IOException,
      ValidationException, MarshalException, RcpaParseException {
    DistributionOption option = DistributionOptionUtils
        .createDistributionOption(classificationInfo, filterByPeptide);

    option.setDatabaseType(dbType.toString());

    option.setDistributionType(dtbType);

    option.setSourceFile(sourceFile);

    option.setClassificationSet(getClassificationSet(sourceFile, sphc));

    putExperimentValues(option, ExperimentValues);

    File result = getOptionFilename(option);

    option.marshal(new FileWriter(result));

    return result;
  }

  /**
   * 给定源文件和分类的类型，得到保存option的文件
   * 
   * @param sourceFile
   *          File
   * @param principle
   *          ClassificationPrincipleType
   * @return File
   */
  public File getOptionFilename(DistributionOption option) {
    final File sourceFile = new File(option.getSourceFile().getFileName());
    final String title = option.getDistributionType().toString() + "_"
        + option.getClassificationInfo().getClassificationPrinciple();
    File resultDir = new File(sourceFile.getParent(), title + "_CLASSIFICATION");
    resultDir.mkdir();
    return new File(resultDir, sourceFile.getName() + "." + title
        + ".statistic.xml");
  }

  /**
   * 将给定的experimentValues填充到DistributionOption的ClassificationSet中
   * 
   * @param option
   *          DistributionOption
   * @param ExperimentValues
   *          double[]
   */
  private void putExperimentValues(DistributionOption option,
      double[] ExperimentValues) {
    if (ExperimentValues == null) {
      return;
    }

    final ClassificationSet cs = option.getClassificationSet();

    if (cs.getClassificationItemCount() != ExperimentValues.length) {
      throw new IllegalArgumentException(
          "Count of ClassificationItem is not equals to length of ExperimentValues!");
    }

    for (int i = 0; i < cs.getClassificationItemCount(); i++) {
      cs.getClassificationItem(i).setExperimentValue(ExperimentValues[i]);
    }
  }

  /**
   * 给定sourceFile和classification方式，得到ClassificationSet
   * 
   * @param sourceFile
   *          SourceFile
   * @param sphc
   *          IIdentifiedPeptideClassification
   * @throws IOException
   * @throws RcpaParseException
   * @return ClassificationSet
   */
  private ClassificationSet getClassificationSet(SourceFile sourceFile,
      IClassification<IIdentifiedPeptide> sphc) throws IOException,
      RcpaParseException {
    ClassificationSet result = new ClassificationSet();

    final List pephits = getPeptideHits(sourceFile);

    final Map<String, HashSet<String>> experiment = getClassifiedExperimentalMap(
        sphc, pephits);

    fillClassifiedExperimentalMap(result, experiment);

    return result;
  }

  /**
   * 将classifiedName与experimental的对应表填充到ClassificationSet中
   * 
   * @param cs
   *          ClassificationSet
   * @param experiment
   *          Map
   * @throws IndexOutOfBoundsException
   */
  private void fillClassifiedExperimentalMap(ClassificationSet cs,
      Map<String, HashSet<String>> experiment) throws IndexOutOfBoundsException {
    cs.clearClassificationItem();

    List<String> classifiedNameList = new ArrayList<String>(experiment.keySet());
    Collections.sort(classifiedNameList);
    for (int i = 0; i < classifiedNameList.size(); i++) {
      ClassificationItem item = new ClassificationItem();
      item.setClassifiedName((String) classifiedNameList.get(i));

      HashSet<String> experimentSet = experiment.get(classifiedNameList.get(i));
      List<String> experimentList = new ArrayList<String>(experimentSet);
      Collections.sort(experimentList);

      for (Iterator iter = experimentList.iterator(); iter.hasNext();) {
        item.addExperimentName((String) iter.next());
      }
      cs.addClassificationItem(item);
    }
  }

  /**
   * 根据给定的分类方式，从pephits中，得到classifiedName与experimental的对应关系。返回值map的key是classifiedName，value是一个set，内容是相对应的experimental。
   * 
   * @param sphc
   *          IIdentifiedPeptideClassification
   * @param pephits
   *          List
   * @return Map
   */
  private Map<String, HashSet<String>> getClassifiedExperimentalMap(
      IClassification<IIdentifiedPeptide> sphc, List pephits) {
    final Map<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();

    for (Iterator iter = pephits.iterator(); iter.hasNext();) {
      IIdentifiedPeptide peptide = (IIdentifiedPeptide) iter.next();

      final String name = sphc.getClassification(peptide);
      if (!result.containsKey(name)) {
        result.put(name, new HashSet<String>());
      }
      HashSet<String> experimentSet = result.get(name);
      experimentSet.add(peptide.getPeakListInfo().getExperiment());
    }
    return result;
  }

  abstract public List getPeptideHits(SourceFile sourceFile)
      throws RcpaParseException, IOException;
}
