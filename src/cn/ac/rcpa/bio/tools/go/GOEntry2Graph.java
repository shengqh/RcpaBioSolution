package cn.ac.rcpa.bio.tools.go;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import att.grappa.Edge;
import att.grappa.Graph;
import att.grappa.GrappaAdapter;
import att.grappa.GrappaConstants;
import att.grappa.GrappaPanel;
import att.grappa.Node;
import att.grappa.Parser;
import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.tools.DotFormat;

public class GOEntry2Graph implements GrappaConstants {
	public final static String INSTANCES = "instances";

	private void removeGoaLessOverRepresented(GOAClassificationEntry parent,
			GOAClassificationEntry goa, GOAnnotationSignificance goas) {
		List<IGOEntry> children = goa.getChildren();
		for (int i = children.size() - 1; i >= 0; i--) {
			removeGoaLessOverRepresented(goa, (GOAClassificationEntry) children
					.get(i), goas);
		}

		if (goa.getChildren().size() == 0) {
			SignificanceLevel level = getSignificanceLevel(goa, goas);
			if (level != SignificanceLevel.SIGNIFICANCE_PLUS_PLUS && parent != null) {
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

	public String goEntry2Graph(String goEntryFile) throws IOException {
		File treeFile = new File(goEntryFile);
		GOAClassificationEntry goa = new GOAClassificationEntry();
		goa.loadFromFile(treeFile.getAbsolutePath());
		Map<String, IGOEntry> entryMap = goa.getGOEntryMap();
		GOAnnotationSignificance goas = new GOAnnotationSignificance(entryMap
				.size());

		removeGoaLessOverRepresented(null, goa, goas);

		File subDir = new File(treeFile.getParent(), "OverRepresented");

		String result = subDir + "/" + treeFile.getName() + ".dot";

		return goEntry2Graph(result, goa, goas);
	}

	public String goEntry2Graph(String resultFile, GOAClassificationEntry goa,
			GOAnnotationSignificance goas) throws IOException {
		Graph graph = new Graph("GOEntry");
		graph.setAttribute("rankdir", "LR");
		graph.setAttribute("label", "\\n");
		graph.setAttribute("font", "Times New Roman");

		graph.setNodeAttribute("shape", "ellipse");
		graph.setNodeAttribute("style", "filled");
		graph.setNodeAttribute("color", "beige");

		graph.setEdgeAttribute("color", "darkgreen");
		graph.setEdgeAttribute(INSTANCES, "1");

		File result = new File(resultFile);
		result.getParentFile().mkdirs();

		HashMap<String, Node> nodes = new HashMap<String, Node>();

		addSignificanceNodeAndEdge(graph, goas);

		addNode(nodes, graph, goa, goas);

		HashMap<String, Edge> edges = new HashMap<String, Edge>();
		addEdge(nodes, edges, graph, goa);
		try {
			graph.printGraph(new FileOutputStream("temp.dot"));
			DotFormat.getInstance().dot("temp.dot", result.getAbsolutePath());
		} finally {
			new File("temp.dot").delete();
		}

		return result.getAbsolutePath();
	}

	private void addSignificanceNodeAndEdge(Graph graph,
			GOAnnotationSignificance goas) {
		DecimalFormat scienceDf = new DecimalFormat("0.###E0");

		for (SignificanceLevel level : SignificanceLevel.values()) {
			Node node = new Node(graph, level.name());
			node.setAttribute("label", level.name() + "\\n"
					+ scienceDf.format(goas.getProbability(level)));
			node.setAttribute("color", getGraphColor(level));
			node.setAttribute("shape", "box");
		}

		new Edge(graph, graph.findNodeByName(SignificanceLevel.NORMAL.name()),
				graph.findNodeByName(SignificanceLevel.SIGNIFICANCE.name()),
				"Normal->Significance");

		new Edge(graph,
				graph.findNodeByName(SignificanceLevel.SIGNIFICANCE.name()), graph
						.findNodeByName(SignificanceLevel.SIGNIFICANCE_PLUS.name()),
				"Significance->Plus");

		new Edge(graph, graph.findNodeByName(SignificanceLevel.SIGNIFICANCE_PLUS
				.name()), graph.findNodeByName(SignificanceLevel.SIGNIFICANCE_PLUS_PLUS
				.name()), "Plus->PlusPlus");
	}

	private void addEdge(HashMap<String, Node> nodes,
			HashMap<String, Edge> edges, Graph graph, GOAClassificationEntry goa) {
		for (IGOEntry subEntry : goa.getChildren()) {
			String edge = goa.getAccession() + "->" + subEntry.getAccession();
			if (edges.containsKey(edge)) {
				continue;
			}
			Edge curEdge = new Edge(graph, nodes.get(goa.getAccession()), nodes
					.get(subEntry.getAccession()), edge);
			edges.put(edge, curEdge);
		}

		for (IGOEntry subEntry : goa.getChildren()) {
			addEdge(nodes, edges, graph, (GOAClassificationEntry) subEntry);
		}
	}

	private void addNode(HashMap<String, Node> nodes, Graph graph,
			GOAClassificationEntry goa, GOAnnotationSignificance goas) {
		if (nodes.containsKey(goa.getAccession())) {
			return;
		}

		Node rootNode = new Node(graph, goa.getAccession());

		String probability = goa.getAnnotations().get("OverRepresentedProbability");
		if (null != probability) {
			rootNode.setAttribute("label", goa.getAccession() + "\\n" + goa.getName()
					+ "\\n" + probability);

			String color = getGraphColor(getSignificanceLevel(goa, goas));
			rootNode.setAttribute("color", color);
		} else {
			rootNode
					.setAttribute("label", goa.getAccession() + "\\n" + goa.getName());
		}

		nodes.put(goa.getAccession(), rootNode);

		for (IGOEntry subEntry : goa.getChildren()) {
			addNode(nodes, graph, (GOAClassificationEntry) subEntry, goas);
		}
	}

	private String getGraphColor(SignificanceLevel level) {
		String color;
		if (level == SignificanceLevel.SIGNIFICANCE) {
			color = "paleturquoise1";
		} else if (level == SignificanceLevel.SIGNIFICANCE_PLUS) {
			color = "skyblue2";
		} else if (level == SignificanceLevel.SIGNIFICANCE_PLUS_PLUS) {
			color = "royalblue";
		} else {
			color = "beige";
		}
		return color;
	}

	private static Graph readFromFile(String filename)
			throws FileNotFoundException {
		InputStream input = new FileInputStream(filename);
		Parser program = new Parser(input);
		try {
			program.parse();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		return program.getGraph();
	}

	private static DemoFrame frame;

	public static void main(String[] args) throws Exception {
		String dotFile = new GOEntry2Graph()
				.goEntry2Graph("F:\\Science\\Data\\MouseLiver\\collections\\cluster\\unique_2\\12_clusters\\Statistic\\mouse_liver_profile.proteins.unique.2_cluster_1.protein.go_cellular_component.special.tree");

		Graph graph = readFromFile(dotFile);

		frame = new DemoFrame(graph);

		frame.setVisible(true);
	}

}

class DemoFrame extends JFrame implements ActionListener {
	GrappaPanel gp;

	Graph graph = null;

	JButton layout = null;

	JButton printer = null;

	JButton draw = null;

	JButton quit = null;

	JPanel panel = null;

	public DemoFrame(Graph graph) {
		super("DemoFrame");
		this.graph = graph;

		setSize(600, 400);
		setLocation(100, 100);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent wev) {
				Window w = wev.getWindow();
				w.setVisible(false);
				w.dispose();
				System.exit(0);
			}
		});

		JScrollPane jsp = new JScrollPane();
		jsp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

		gp = new GrappaPanel(graph);
		gp.addGrappaListener(new GrappaAdapter());
		gp.setScaleToFit(false);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;

		panel = new JPanel();
		panel.setLayout(gbl);

		draw = new JButton("Draw");
		gbl.setConstraints(draw, gbc);
		panel.add(draw);
		draw.addActionListener(this);

		layout = new JButton("Layout");
		gbl.setConstraints(layout, gbc);
		panel.add(layout);
		layout.addActionListener(this);

		printer = new JButton("Print");
		gbl.setConstraints(printer, gbc);
		panel.add(printer);
		printer.addActionListener(this);

		quit = new JButton("Quit");
		gbl.setConstraints(quit, gbc);
		panel.add(quit);
		quit.addActionListener(this);

		getContentPane().add("Center", jsp);
		getContentPane().add("West", panel);

		setVisible(true);
		jsp.setViewportView(gp);
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() instanceof JButton) {
			JButton tgt = (JButton) evt.getSource();
			if (tgt == draw) {
				graph.repaint();
			} else if (tgt == quit) {
				System.exit(0);
			} else if (tgt == printer) {
				graph.printGraph(System.out);
				System.out.flush();
			} else if (tgt == layout) {
				Dimension dim = gp.getPreferredScrollableViewportSize();
				BufferedImage image = new BufferedImage((int) dim.getWidth(), (int) dim
						.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = image.createGraphics();
				gp.paint(g2);
				FileOutputStream os;
				try {
					os = new FileOutputStream("data/sample.png");
					ImageIO.write(image, "png", os);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
