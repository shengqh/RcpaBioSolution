package cn.ac.rcpa.bio.tools.go;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.bio.models.IObjectRemoveEvent;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.GUIUtils;
import cn.ac.rcpa.utils.SpecialSwingFileFilter;

public class GOEntryTreeViewerUI extends JFrame {
	private static String title = "GO Annotation - GOEntry Tree Viewer";

	private static String version = "1.0.0";

	IGOEntry goEntry = new GOAClassificationEntry();

	private String currentFilename;

	JToolBar barTools = new JToolBar();

	JButton btnLoad = new JButton("Load From File...");

	JButton btnClose = new JButton("Close");

	JFileChooser chooser = new JFileChooser();

	JScrollPane pnlTree = new JScrollPane();

	JTree tvwTree = new JTree(new DefaultMutableTreeNode());

	GOEntryTreeViewer_tvwTree_treeSelectionAdapter treeSelectionAdapter;

	IObjectRemoveEvent removeEvent = new IObjectRemoveEvent() {
		public void objectRemoved(Object fromObject, Object removedObject) {
			if (fromObject == this) {
				return;
			}
			DefaultMutableTreeNode node = findNode(getRootNode(), removedObject);

			if (node != null) {
				deleteNode(node);
			}
		}
	};

	JPopupMenu pmResult = new JPopupMenu();

	JMenuItem mniRemove = new JMenuItem("Remove Current GOEntry");

	JButton btnSave = new JButton("Save To File...");

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	JTextArea txtDefinition = new JTextArea();

	JScrollPane pnlDefinition = new JScrollPane();

	JTextArea txtAnnotation = new JTextArea();

	JScrollPane pnlAnnotation = new JScrollPane();

	DefaultMutableTreeNode findNode(DefaultMutableTreeNode node, Object obj) {
		if (!node.isRoot() && obj == node.getUserObject()) {
			return node;
		}

		for (Enumeration nodes = node.children(); nodes.hasMoreElements();) {
			DefaultMutableTreeNode result = findNode((DefaultMutableTreeNode) nodes
					.nextElement(), obj);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public GOEntryTreeViewerUI() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(new Dimension(800, 600));
		this.setTitle(Constants.getSQHTitle(title, version));
		this.getContentPane().setLayout(gridBagLayout1);

		chooser.setSelectedFile(new File("."));

		btnLoad.setMaximumSize(new Dimension(100, 27));
		btnLoad.setMinimumSize(new Dimension(100, 27));
		btnLoad.setPreferredSize(new Dimension(100, 27));
		btnLoad
				.addActionListener(new GOEntryTreeViewer_btnLoad_actionAdapter(this));
		btnClose.setMaximumSize(new Dimension(100, 27));
		btnClose.setMinimumSize(new Dimension(100, 27));
		btnClose.setPreferredSize(new Dimension(100, 27));
		btnClose.addActionListener(new GOEntryTreeViewer_btnClose_actionAdapter(
				this));
		pnlTree.setMinimumSize(new Dimension(200, 16));
		pnlTree.setPreferredSize(new Dimension(200, 16));
		treeSelectionAdapter = new GOEntryTreeViewer_tvwTree_treeSelectionAdapter(
				this);
		tvwTree.addTreeSelectionListener(treeSelectionAdapter);
		mniRemove.addActionListener(new GOEntryTreeViewer_mniRemove_actionAdapter(
				this));
		btnSave.setMaximumSize(new Dimension(100, 27));
		btnSave.setMinimumSize(new Dimension(100, 27));
		btnSave.setPreferredSize(new Dimension(100, 27));
		btnSave
				.addActionListener(new GOEntryTreeViewer_btnSave_actionAdapter(this));
		pnlDefinition.setAutoscrolls(true);
		barTools.add(btnLoad, null);
		barTools.add(btnSave, null);
		barTools.add(btnClose, null);

		pnlTree.getViewport().add(tvwTree);
		pmResult.add(mniRemove);
		txtDefinition.setLineWrap(true);
		txtDefinition.setWrapStyleWord(true);
		pnlDefinition.getViewport().add(txtDefinition);
		this.getContentPane().add(
				pnlDefinition,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 40));
		txtAnnotation.setLineWrap(true);
		txtAnnotation.setWrapStyleWord(true);
		pnlAnnotation.getViewport().add(txtAnnotation);
		this.getContentPane().add(
				pnlAnnotation,
				new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 40));
		this.getContentPane().add(
				pnlTree,
				new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(
				barTools,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		tvwTree.addMouseListener(new SequestResultPopupListener(this));
	}

	void btnClose_actionPerformed(ActionEvent e) {
		dispose();
	}

	private DefaultTreeModel getTreeModel() {
		return (DefaultTreeModel) tvwTree.getModel();
	}

	private DefaultMutableTreeNode getRootNode() {
		return (DefaultMutableTreeNode) getTreeModel().getRoot();
	}

	private void focusNode(TreeNode node) {
		if (node != null) {
			tvwTree.removeTreeSelectionListener(treeSelectionAdapter);
			TreePath visiblePath = new TreePath(getTreeModel().getPathToRoot(node));
			tvwTree.makeVisible(visiblePath);
			tvwTree.setSelectionPath(visiblePath);
			tvwTree.addTreeSelectionListener(treeSelectionAdapter);

			IGOEntry entry = (IGOEntry) ((DefaultMutableTreeNode) node)
					.getUserObject();
			txtDefinition.setText(entry.getDefinition());

			txtAnnotation.setText("");
			if (entry instanceof GOAClassificationEntry) {
				Map<String, String> annotations = ((GOAClassificationEntry) entry)
						.getAnnotations();
				StringBuffer annotation = new StringBuffer();
				for (String key : annotations.keySet()) {
					annotation.append(key + "\t" + annotations.get(key) + "\n");
				}
				txtAnnotation.setText(annotation.toString());
			}
		}
	}

	void loadFromFile(String filename) {
		try {
			filename = filename.trim();
			goEntry.loadFromFile(filename);
			setCurrentFilename(filename);
			buildTree();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buildTree() {
		clearTree();
		addEntryToNode(goEntry, getRootNode());
	}

	private void addEntryToNode(IGOEntry goEntry, DefaultMutableTreeNode node) {
		node.setUserObject(goEntry);
		for (IGOEntry child : goEntry.getChildren()) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
			getTreeModel().insertNodeInto(childNode, node, node.getChildCount());
			addEntryToNode(child, childNode);
		}
	}

	private void deleteNodeChildren(DefaultMutableTreeNode node) {
		if (node == null) {
			return;
		}

		int numberOfChildren = node.getChildCount();

		ArrayList<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();

		for (int i = 0; i < numberOfChildren; i++) {
			DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) node
					.getChildAt(i);
			children.add(currentChild);
		}

		for (int i = 0; i < numberOfChildren; i++) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) children
					.get(i);
			deleteNode(currentNode);
		}
	}

	private void deleteNode(DefaultMutableTreeNode node) {
		deleteNodeChildren(node);
		getTreeModel().removeNodeFromParent(node);
	}

	private void clearTree() {
		deleteNodeChildren(getRootNode());
	}

	void btnLoad_actionPerformed(ActionEvent e) {
		if (currentFilename != null) {
			chooser.setSelectedFile(new File(currentFilename));
		}

		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new SpecialSwingFileFilter("tree",
				"GOEntry Tree File", false));

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			loadFromFile(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	void tvwTree_valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tvwTree
				.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}
		focusNode(node);
	}

	public static void main(String[] args) {
		GOEntryTreeViewerUI viewer = new GOEntryTreeViewerUI();
		GUIUtils.setFrameDesktopCentre(viewer);
		viewer.setVisible(true);
	}

	void mniRemove_actionPerformed(ActionEvent e) {
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,
				"Are you sure to delete current GOEntry?", "Confirm",
				JOptionPane.YES_NO_OPTION)) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tvwTree
					.getLastSelectedPathComponent();
			DefaultMutableTreeNode focus = node.getNextSibling();
			if (focus == null) {
				focus = node.getPreviousSibling();
			}
			deleteNode(node);
			focusNode(focus);
		}
	}

	void btnSave_actionPerformed(ActionEvent e) {
		chooser.setSelectedFile(new File(currentFilename));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				saveToFile(chooser.getSelectedFile().getAbsolutePath());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Save to file error : "
						+ ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveToFile(String filename) throws Exception {
		goEntry = getGOEntryFromTree();
		goEntry.saveToFile(filename);
	}

	private IGOEntry getGOEntryFromTree() {
		IGOEntry result = (IGOEntry) getRootNode().getUserObject();
		fillEntry(result, getRootNode());
		return result;
	}

	private void fillEntry(IGOEntry entry, DefaultMutableTreeNode node) {
		entry.getChildren().clear();
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node
					.getChildAt(i);
			IGOEntry childEntry = (IGOEntry) childNode.getUserObject();
			entry.getChildren().add(childEntry);
			fillEntry(childEntry, childNode);
		}
	}

	private void setCurrentFilename(String filename) {
		setTitle(title + " : " + filename);
		currentFilename = filename;
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation, "GO Annotation Viewer" };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			GOEntryTreeViewerUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}
}

class GOEntryTreeViewer_btnClose_actionAdapter implements
		java.awt.event.ActionListener {
	GOEntryTreeViewerUI adaptee;

	GOEntryTreeViewer_btnClose_actionAdapter(GOEntryTreeViewerUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnClose_actionPerformed(e);
	}
}

class GOEntryTreeViewer_btnLoad_actionAdapter implements
		java.awt.event.ActionListener {
	GOEntryTreeViewerUI adaptee;

	GOEntryTreeViewer_btnLoad_actionAdapter(GOEntryTreeViewerUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnLoad_actionPerformed(e);
	}
}

class GOEntryTreeViewer_tvwTree_treeSelectionAdapter implements
		javax.swing.event.TreeSelectionListener {
	GOEntryTreeViewerUI adaptee;

	GOEntryTreeViewer_tvwTree_treeSelectionAdapter(GOEntryTreeViewerUI adaptee) {
		this.adaptee = adaptee;
	}

	public void valueChanged(TreeSelectionEvent e) {
		adaptee.tvwTree_valueChanged(e);
	}
}

class SequestResultPopupListener extends MouseAdapter {
	GOEntryTreeViewerUI viewer;

	SequestResultPopupListener(GOEntryTreeViewerUI viewer) {
		this.viewer = viewer;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			viewer.pmResult.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}

class GOEntryTreeViewer_mniRemove_actionAdapter implements
		java.awt.event.ActionListener {
	GOEntryTreeViewerUI adaptee;

	GOEntryTreeViewer_mniRemove_actionAdapter(GOEntryTreeViewerUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.mniRemove_actionPerformed(e);
	}
}

class GOEntryTreeViewer_btnSave_actionAdapter implements
		java.awt.event.ActionListener {
	GOEntryTreeViewerUI adaptee;

	GOEntryTreeViewer_btnSave_actionAdapter(GOEntryTreeViewerUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnSave_actionPerformed(e);
	}
}
