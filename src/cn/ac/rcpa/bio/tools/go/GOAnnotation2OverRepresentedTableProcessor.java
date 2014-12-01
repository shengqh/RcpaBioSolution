/*
 * Created on 2005-11-8
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.tools.DotFormat;

public class GOAnnotation2OverRepresentedTableProcessor implements
		IFileProcessor {
	private String pictureFile;

	private boolean drawPngFile;

	public GOAnnotation2OverRepresentedTableProcessor(String pictureFile,
			boolean drawPngFile) {
		super();
		this.pictureFile = pictureFile;
		this.drawPngFile = drawPngFile;
	}

	public GOAnnotation2OverRepresentedTableProcessor() {
		super();
		this.drawPngFile = true;
	}

	public List<String> process(String goaFile) throws Exception {
		File treeFile = new File(goaFile);
		GOAClassificationEntry goa = new GOAClassificationEntry();
		goa.loadFromFile(treeFile.getCanonicalPath());
		Map<String, IGOEntry> entryMap = goa.getGOEntryMap();
		GOAnnotationSignificance goas = new GOAnnotationSignificance(entryMap
				.size());

		removeGoaLessOverRepresented(null, goa, goas);

		File subDir = new File(treeFile.getParent(), "OverRepresented");
		subDir.mkdirs();

		ArrayList<String> result = new ArrayList<String>();
		String orGoaFile = subDir + "/" + treeFile.getName() + ".overRepresented";
		goa.saveToFile(orGoaFile);

		result.add(orGoaFile);

		String htmlFile = orGoaFile + ".html";
		PrintWriter pw = new PrintWriter(new FileWriter(htmlFile));
		try {
			if (pictureFile != null && pictureFile.length() > 0) {
				pw.println("<center>");
				pw.println("<IMG SRC=\"" + pictureFile
						+ "\" border=\"0\" width=\"640\" height=\"480\">");
				pw.println("</center>");
			}

			if (drawPngFile) {
				String dotFile = orGoaFile + ".dot";
				new GOEntry2Graph().goEntry2Graph(dotFile, goa, goas);
				result.add(dotFile);

				String pngFile = orGoaFile + ".png";
				DotFormat.getInstance().png(dotFile, pngFile);
				result.add(pngFile);

				pw.println("<center>");
				pw.println("<a href=\"" + new File(pngFile).getName()
						+ "\" target=\"_blank\"><IMG SRC=\"" + new File(pngFile).getName()
						+ "\" border=\"0\" width=\"1024\"></a>");
				pw.println("</center>");
			}

			pw.println("<table border=\"1\" width=\"100%\">");
			int deepestLevel = goa.getDeepestLevel();
			SignificanceLevel rootLevel = getSignificanceLevel(goa, goas);
			if (goa.getChildren().size() > 0
					|| rootLevel == SignificanceLevel.SIGNIFICANCE_PLUS_PLUS) {
				printGoa(pw, goa, 0, deepestLevel, false, goas);
			}
			pw.println("</table>");
		} finally {
			pw.close();
		}
		result.add(htmlFile);

		return result;
	}

	private void printGoa(PrintWriter pw, GOAClassificationEntry goa, int level,
			int deepestLevel, boolean isFirstChild, GOAnnotationSignificance goas) {
		if (!isFirstChild) {
			pw.println("<tr>");
			for (int i = 0; i < level; i++) {
				pw.println("<td>&nbsp;</td>");
			}
		}

		pw.println("<td bgcolor=\""
				+ GOAnnotationSignificance.getColor(getSignificanceLevel(goa, goas))
				+ "\">" + goa.getName() + "("
				+ goa.getAnnotations().get("OverRepresentedProbability") + ")</td>");

		if (goa.getChildren().size() == 0) {
			for (int i = level + 1; i < deepestLevel; i++) {
				pw.println("<td>&nbsp;</td>");
			}
			pw.println("</tr>");
		} else {
			for (int i = 0; i < goa.getChildren().size(); i++) {
				printGoa(pw, (GOAClassificationEntry) goa.getChildren().get(i),
						level + 1, deepestLevel, i == 0, goas);
			}
		}
	}

	private void removeGoaLessOverRepresented(GOAClassificationEntry parent,
			GOAClassificationEntry goa, GOAnnotationSignificance goas) {
		List<IGOEntry> children = goa.getChildren();
		for (int i = children.size() - 1; i >= 0; i--) {
			removeGoaLessOverRepresented(goa, (GOAClassificationEntry) children
					.get(i), goas);
		}

		if (goa.getChildren().size() == 0) {
			SignificanceLevel level = getSignificanceLevel(goa, goas);
			if (level == SignificanceLevel.NORMAL && parent != null) {
				// if (level != SignificanceLevel.SIGNIFICANCE_PLUS_PLUS && parent !=
				// null) {
				parent.getChildren().remove(goa);
			}
		}
	}

	private SignificanceLevel getSignificanceLevel(GOAClassificationEntry goa,
			GOAnnotationSignificance goas) {
		String overRepresentedProbabilityStr = goa.getAnnotations().get(
				"OverRepresentedProbability");
		double overRepresentedProbability = Double
				.parseDouble(overRepresentedProbabilityStr);
		SignificanceLevel level = goas.getLevel(overRepresentedProbability);
		return level;
	}

	public static void main(String[] args) throws Exception {
		GOAnnotation2OverRepresentedTableProcessor processor = new GOAnnotation2OverRepresentedTableProcessor(
				"../mouse_liver_profile.proteins_12_cluster_2.png", true);
		// processor.doProcessTreeFile(new
		// File("F:\\Science\\Data\\MouseLiver\\collections\\cluster\\unique_2\\12_clusters\\Statistic\\mouse_liver_profile.proteins.unique.2_cluster_1.protein.go_cellular_component.special.tree"));
		processor
				.process("F:\\Science\\Data\\MouseLiver\\collections\\cluster\\unique_2\\12_clusters\\Statistic\\mouse_liver_profile.proteins.unique.2_cluster_2.protein.go_cellular_component.special.tree");
	}
}
