package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.GOEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.Pair;
import cn.ac.rcpa.utils.SpecialIOFileFilter;

public class GOAnnotation2FileTable implements IFileProcessor {
	private List<IGOEntry> entries;

	private String extension;

	private Pattern pattern;

	private Comparator<String> comp;

	private String beforeHeader;

	public GOAnnotation2FileTable(IGOEntry[] entries, String extension,
			Pattern pattern, Comparator<String> comp, String beforeHeader) {
		this.entries = new ArrayList<IGOEntry>(Arrays.asList(entries));
		this.extension = extension;
		this.pattern = pattern;
		this.comp = comp;
		this.beforeHeader = beforeHeader;
	}

	Map<String, List<Pair<String, String>>> getMap(File[] treeFiles) {
		Map<String, List<Pair<String, String>>> result = new HashMap<String, List<Pair<String, String>>>();

		for (File treeFile : treeFiles) {
			Matcher match = pattern.matcher(treeFile.getName());
			match.find();
			GOAClassificationEntry goa = new GOAClassificationEntry();
			goa.loadFromFile(treeFile.getAbsolutePath());
			Map<String, IGOEntry> entryMap = goa.getGOEntryMap();
			GOAnnotationSignificance goas = new GOAnnotationSignificance(entryMap
					.size());

			List<Pair<String, String>> entryResult = new ArrayList<Pair<String, String>>();
			for (IGOEntry entry : entries) {
				String overRepresentedProbabilityStr = "1E0";
				if (entryMap.containsKey(entry.getAccession())) {
					GOAClassificationEntry goac = (GOAClassificationEntry) entryMap
							.get(entry.getAccession());
					overRepresentedProbabilityStr = goac.getAnnotations().get(
							"OverRepresentedProbability");
				}
				double overRepresentedProbability = Double
						.parseDouble(overRepresentedProbabilityStr);
				String significance_color = goas.getColor(overRepresentedProbability);
				entryResult.add(new Pair<String, String>(significance_color,
						overRepresentedProbabilityStr));
			}
			result.put(match.group(1), entryResult);
		}

		return result;
	}

	public List<String> getSortedKeys(Set<String> keys) {
		ArrayList<String> result = new ArrayList<String>(keys);
		Collections.sort(result, comp);
		return result;
	}

	public List<String> process(String treeDirectory) throws Exception {
		File[] treeFiles = new File(treeDirectory)
				.listFiles(new SpecialIOFileFilter(extension, true));

		Map<String, List<Pair<String, String>>> map = getMap(treeFiles);

		String resultFile = treeDirectory + "/" + new File(treeDirectory).getName()
				+ ".html";
		PrintWriter pw = new PrintWriter(new FileWriter(resultFile));
		try {
			pw.println(beforeHeader);
			pw.println("<table border=\"1\" width=\"100%\">");
			pw.println("<tr>");
			pw.println("<td>File</td>");
			for (IGOEntry entry : entries) {
				pw.println("<td>" + entry.getName() + "</td>");
			}
			pw.println("</tr>");

			List<String> keys = getSortedKeys(map.keySet());
			for (String key : keys) {
				pw.println("<tr>");
				pw.println("<td>" + key + "</td>");
				List<Pair<String, String>> probabilityList = map.get(key);
				for (Pair<String, String> probability : probabilityList) {
					pw.println("<td bgcolor=\"" + probability.fst + "\">"
							+ probability.snd + "</td>");
				}
				pw.println("<tr>");
			}
			pw.println("</table>");
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

	public static void main(String[] args) throws Exception {
		IGOEntry[] entries = new IGOEntry[] {
				new GOEntry("GO:0005634", "nucleus", ""),
				new GOEntry("GO:0005739", "mitochondrion", ""),
				new GOEntry("GO:0005783", "endoplasmic reticulum", ""),
				new GOEntry("GO:0005794", "Golgi apparatus", ""),
				new GOEntry("GO:0005737", "cytoplasm", ""),
				new GOEntry("GO:0016020", "membrane", "") };

		// doClusterAnalysis(entries);
		doExperimentalAnalysis(entries);
	}

	private static void doExperimentalAnalysis(IGOEntry[] entries)
			throws NumberFormatException, Exception {
		new GOAnnotation2FileTable(entries, ".go_cellular_component.special.tree",
				Pattern.compile("(\\S+)\\.protein"), null, "")
				.process("F:/Science/Data/MouseLiver/collections/identifications/unique_2/STATISTIC");
	}

	protected static void doClusterAnalysis(IGOEntry[] entries)
			throws NumberFormatException, Exception {
		Comparator<String> comp = new Comparator<String>() {
			public int compare(String o1, String o2) {
				return Integer.parseInt(o1) - Integer.parseInt(o2);
			}
		};
		StringBuffer sb = new StringBuffer();
		sb.append("<center>");
		sb.append("<IMG SRC=\""
				+ "mouse_liver_profile.proteins.unique.2_12_cluster.png"
				+ "\" border=\"0\" width=\"1024\">");
		sb.append("</center>");
		new GOAnnotation2FileTable(entries, ".go_cellular_component.special.tree",
				Pattern.compile("cluster_(\\d+)\\.protein"), comp, sb.toString())
				.process("F:/Science/Data/MouseLiver/collections/cluster/unique2/clusters12/STATISTIC");
	}

}
