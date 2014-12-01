package cn.ac.rcpa.bio.tools.relex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelexProtein {
  private static Pattern pattern;

  protected static Pattern getPattern() {
    if (null == pattern) {
      pattern = Pattern
          .compile("^(P\\t){0,1}([^\\t]+)\\t([^\\t]+)\\t([\\d\\.]+)\\t([\\d\\.]+)\\t(\\d+)");
    }
    return pattern;
  }

  private List<String> proteins = new ArrayList<String>();

  private List<String> references = new ArrayList<String>();

  private String ratio;

  private String sd;

  private String peptideCount;

  private String directory;

  private List<RelexPeptide> peptides = new ArrayList<RelexPeptide>();

  public List<RelexPeptide> getPeptides() {
    return peptides;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }

  public List<String> getProteins() {
    return Collections.unmodifiableList(proteins);
  }

  public List<String> getReferences() {
    return Collections.unmodifiableList(references);
  }

  public String getPeptideCount() {
    return peptideCount;
  }

  public String getRatio() {
    return ratio;
  }

  public String getSd() {
    return sd;
  }

  public final void setProteinReferences(String proteinNames, String references) {
    this.proteins.clear();
    this.references.clear();

    String[] proteinArray = proteinNames.split(",\\s*");
    int lastPos = 0;
    for (String proteinName : proteinArray) {
      this.proteins.add(proteinName);
      int curPos = references.indexOf(", " + proteinName, lastPos);
      if (-1 != curPos) {
        String lastReference = references.substring(lastPos, curPos);
        this.references.add(lastReference);
        lastPos = curPos + 2;
      }
    }
    String lastReference = references.substring(lastPos,
        references.length());
    this.references.add(lastReference);
  }

  public void setPeptideCount(String peptideCount) {
    this.peptideCount = peptideCount;
  }

  public void setRatio(String ratio) {
    this.ratio = ratio;
  }

  public void setSd(String sd) {
    this.sd = sd;
  }

  public RelexProtein(String proteins, String references, String ratio,
      String sd, String peptideCount) {
    setProteinReferences(proteins, references);
    this.ratio = ratio;
    this.sd = sd;
    this.peptideCount = peptideCount;
  }

  public static boolean isRelexProtein(String line) {
    return getPattern().matcher(line).find();
  }

  public static RelexProtein parse(String line) {
    Matcher matcher = getPattern().matcher(line);
    if (!matcher.find()) {
      throw new IllegalStateException(line + " is not a valid RelexProtein");
    }

    return new RelexProtein(matcher.group(2), matcher.group(3), matcher
        .group(4), matcher.group(5), matcher.group(6));
  }
}
