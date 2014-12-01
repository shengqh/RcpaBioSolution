package cn.ac.rcpa.bio.tools.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.bio.tools.distribution.option.types.FilterType;

public class CalculationItem<
    E extends IIdentifiedPeptide,
    F extends IIdentifiedPeptideHit<E>> {
  private Object key;
  private List<F> peptides = new ArrayList<F>();
  private double theoreticalValue;
  private double experimentValue;
  FilterType filterType = FilterType.PEPTIDECOUNT;
  private Map<String,PeptideCount> classifications = new HashMap<String,PeptideCount>();

  public CalculationItem() {
  }

  public void setKey(Object key) {
    this.key = key;
  }

  public void setPeptides(List<F> peptides) {
    this.peptides = peptides;
  }

  public void setExperimentValue(double experimentValue) {
    this.experimentValue = experimentValue;
  }

  public void setTheoreticalValue(double theoreticalValue) {
    this.theoreticalValue = theoreticalValue;
  }

  public Object getKey() {
    return key;
  }

  public List<F> getPeptides() {
    return peptides;
  }

  public double getExperimentValue() {
    return experimentValue;
  }

  public double getTheoreticalValue() {
    return theoreticalValue;
  }

  public int getClassifiedCount(String classifiedName){
    if (filterType == FilterType.PEPTIDECOUNT){
      return getClassifiedPeptideCount(classifiedName).getPeptideCount();
    }
    else if (filterType == FilterType.UNIQUEPEPTIDECOUNT){
      return getClassifiedPeptideCount(classifiedName).getUniquePeptideCount();
    }

    throw new IllegalStateException("No such FilterType " + filterType + " defined in getClassifiedCount!");
  }

  public PeptideCount getClassifiedPeptideCount(String classifiedName){
    return classifications.get(classifiedName);
  }

  /**
   * 根据给定的sphc、pephits，以及classifiedNames进行统计各个classifiedNames对应的肽段个数。
   *
   * @param sphc IIdentifiedPeptideClassification
   * @param classifiedNames String[]
   * @param type FilterType
   */
  public void classifyPeptideHit(IClassification<IIdentifiedPeptide> sphc,String[] classifiedNames,FilterType type){
    classifications = new HashMap<String, PeptideCount>();

    this.filterType = type;

    for (int i = 0; i < classifiedNames.length; i++) {
      classifications.put(classifiedNames[i], new PeptideCount());
    }

    classifyPeptideHitByUniquePeptideCount(sphc, classifiedNames);

    classifyPeptideHitByPeptideCount(sphc);
  }

  private void classifyPeptideHitByPeptideCount(
      IClassification<IIdentifiedPeptide> sphc) {
    for(F peptidehit:peptides){
      E peptide = peptidehit.getPeptide (0);
      String name = sphc.getClassification(peptide);
      PeptideCount count = classifications.get(name);
      count.setPeptideCount(count.getPeptideCount() + 1);
    }
  }

  private void classifyPeptideHitByUniquePeptideCount(
      IClassification<IIdentifiedPeptide> sphc, String[] classifiedNames) {
    HashMap<String, HashSet<String>> temp = new HashMap<String, HashSet<String>>();
    for (int i = 0; i < classifiedNames.length; i++) {
      temp.put(classifiedNames[i], new HashSet<String>());
    }

    for(F peptidehit:peptides){
      E peptide = peptidehit.getPeptide (0);
      String name = sphc.getClassification(peptide);
      HashSet<String> uniPeps = temp.get(name);
      uniPeps.add(PeptideUtils.getPurePeptideSequence( peptide.getSequence()));
    }
    for (int i = 0; i < classifiedNames.length; i++) {
      PeptideCount count = (PeptideCount)classifications.get(classifiedNames[i]);
      count.setUniquePeptideCount(((Set)temp.get(classifiedNames[i])).size());
    }
  }

  public void calculateExperimentalValue(Map ExperimentValues) {
    double totalValue = 0.0;
    int totalCount = 0;

    for (String classifiedName: classifications.keySet()) {
      final int pepCount = getClassifiedPeptideCount(classifiedName).getPeptideCount();
      final Double expValue = (Double)ExperimentValues.get(classifiedName);

      totalValue = totalValue + pepCount * expValue.doubleValue();
      totalCount = totalCount + pepCount;
    }

    experimentValue = totalValue / totalCount;
  }


}
