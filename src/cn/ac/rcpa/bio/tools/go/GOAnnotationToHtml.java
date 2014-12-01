package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.bio.processor.IFileProcessor;

public class GOAnnotationToHtml implements IFileProcessor {
  public GOAnnotationToHtml() {
  }

  private String pictureFile;

  public GOAnnotationToHtml(String pictureFile) {
    this.pictureFile = pictureFile;
  }

  private Set<String> printedEntry = new HashSet<String>();

  public static final String version = "1.0.0";

  public List<String> process(String originFile) throws Exception {
    GOAClassificationEntry entry = new GOAClassificationEntry();
    entry.loadFromFile(originFile);
    GOAnnotationSignificance significance = new GOAnnotationSignificance(entry
        .getGOEntryMap().size());

    DecimalFormat df = new DecimalFormat("0.000E0");

    final String resultFile = originFile + ".html";
    PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
    try {
      pw.println("<html>");
      pw.println("<title>" + new File(originFile).getName() + "</title>");
      pw.println("<body>");

      if (pictureFile != null) {
        pw.println("<center>");
        pw.println("<IMG SRC=\"" + pictureFile
            + "\" border=\"0\" width=\"640\" height=\"480\">");
        pw.println("</center>");
      }

      pw.println("<table border=\"1\" width=\"100%\">");
      pw.println("<tr>");
      pw.println("<td bgcolor=\""
          + GOAnnotationSignificance.getColor(SignificanceLevel.SIGNIFICANCE)
          + "\">Significance   =" + df.format(significance.getSignificance())
          + "</td>");
      pw.println("<td bgcolor=\""
          + GOAnnotationSignificance
              .getColor(SignificanceLevel.SIGNIFICANCE_PLUS)
          + "\">Significance+  ="
          + df.format(significance.getSignificance_plus()) + "</td>");
      pw.println("<td bgcolor=\""
          + GOAnnotationSignificance
              .getColor(SignificanceLevel.SIGNIFICANCE_PLUS_PLUS)
          + "\">Significance++ ="
          + df.format(significance.getSignificance_plus_plus()) + "</td>");
      pw.println("</tr>");
      pw.println("</table>");
      pw.println("<hr></hr>");

      pw.println("<table border=\"1\" width=\"100%\">");
      pw.println("<tr>");
      pw.println("<td>Accession</td>");
      pw.println("<td>Name</td>");
      pw.println("<td>Count(Percent)</td>");
      // pw.println("<td>Definition</td>");
      pw.println("<td>OverRepresentedProbability</td>");
      // pw.println("<td>Proteins</td>");
      pw.println("</tr>");

      printedEntry.clear();
      printEntry(pw, entry, 0, significance);

      pw.println("</table>");
      pw.println("</body>");
      pw.println("</html>");
    } finally {
      pw.close();
    }
    return Arrays.asList(new String[] { resultFile });
  }

  private void printEntry(PrintWriter pw, GOAClassificationEntry entry,
      int indent, GOAnnotationSignificance significance) {
    /*
     * if (printedEntry.contains(entry.getAccession())) { return; }
     * printedEntry.add(entry.getAccession());
     */
    double overRepresentedProbability = Double.parseDouble(entry
        .getAnnotations().get("OverRepresentedProbability"));
    String color = significance.getColor(overRepresentedProbability);
    pw.println("<tr bgcolor=\"" + color + "\">");
    pw.print("<td>");
    for (int i = 0; i < indent; i++) {
      pw.print("->");
    }
    pw.print(entry.getAccession());
    pw.println("</td>");

    pw.println("<td>" + entry.getName() + "</td>");
    pw.println("<td>" + entry.getProteins().size() + "("
        + entry.getAnnotations().get("Percent") + ")</td>");

    pw.println("<td>"
        + entry.getAnnotations().get("OverRepresentedProbability") + "</td>");

    pw.println("</tr>");

    for (IGOEntry subentry : entry.getChildren()) {
      printEntry(pw, (GOAClassificationEntry) subentry, indent + 1,
          significance);
    }
  }

  public static void main(String[] args) throws Exception {
    new GOAnnotationToHtml(
        "mouse_liver_profile.proteins_12_cluster_2.png")
        .process("F:\\Science\\Data\\MouseLiver\\collections\\cluster\\unique_2\\12_clusters\\Statistic\\mouse_liver_profile.proteins.unique.2_cluster_2.protein.go_cellular_component.special.tree");
  }
}
