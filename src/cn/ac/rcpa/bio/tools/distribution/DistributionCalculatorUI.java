package cn.ac.rcpa.bio.tools.distribution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultFileType;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide;
import cn.ac.rcpa.bio.tools.distribution.option.SourceFile;
import cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType;
import cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType;
import cn.ac.rcpa.bio.tools.distribution.option.types.FilterType;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaCheckBox;
import cn.ac.rcpa.component.JRcpaCheckTextField;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaComponentList;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.component.JRcpaLoopField;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.GUIUtils;
import cn.ac.rcpa.utils.OpenFileArgument;
import cn.ac.rcpa.utils.SpecialSwingFileFilter;
import cn.ac.rcpa.utils.XMLFile;

public class DistributionCalculatorUI extends JFrame {
	final static String configFile = "config/DistributionCalculatorUI.conf";

	private DistributionOption option;

	private File optionFile;

	private static String title = "Protein/Peptide Distribution Calculator";

	private static String version = "1.3.5";

	private int currPanelIndex = -1;

	private JPanel[] totalPanels = new JPanel[2];

	private String lastDirectory;

	private JPanel pnlOptionDetails = new JPanel();

	private JRcpaComponentList optionDetailList = new JRcpaComponentList();

	private JPanel pnlModifyClassification = new JPanel();

	private JRcpaComboBox<DistributionType> distributionType = new JRcpaComboBox<DistributionType>(
			"DistributionType", "Distribution Type", new DistributionType[] {
					DistributionType.PROTEIN, DistributionType.PEPTIDE },
			DistributionType.PROTEIN);

	private JRcpaFileField targetFile = new JRcpaFileField("NonredudantFile",
			new OpenFileArgument("Nonredundant", "noredundant"), true);

	private JRcpaComboBox<SequenceDatabaseType> databaseType = new JRcpaComboBox<SequenceDatabaseType>(
			"DatabaseType", "Database Type", SequenceDatabaseType.values(),
			SequenceDatabaseType.IPI);

	private JRcpaComboBox<ClassificationType> classificationType = new JRcpaComboBox<ClassificationType>(
			"ClassificationType", "Classification Type", new ClassificationType[] {
					ClassificationType.ABUNDANCE, ClassificationType.METHOD,
					ClassificationType.MW, ClassificationType.PI,
					ClassificationType.OTHER }, ClassificationType.METHOD);

	private JRcpaTextField classificationTitle = new JRcpaTextField(
			"ClassificationTitle", "Classification Title", "", true);

	private JRcpaComboBox<FilterType> filterType = new JRcpaComboBox<FilterType>(
			"FilterType", "Filter Type", new FilterType[] { FilterType.PEPTIDECOUNT,
					FilterType.UNIQUEPEPTIDECOUNT }, FilterType.UNIQUEPEPTIDECOUNT);

	private JRcpaLoopField<Integer> loopDefinition = JRcpaLoopField
			.createInteger("LoopDefinition", "Loop Definition", 1, 1, 1, true);

	JPanel jPanel1 = new JPanel();

	JButton btnClose = new JButton();

	JButton btnNext = new JButton();

	JButton btnBack = new JButton();

	JScrollPane jScrollPane1 = new JScrollPane();

	JTree tvwClassification = new JTree();

	JButton btnReset = new JButton();

	JButton btnMerge = new JButton();

	JButton btnChangeClassifiedName = new JButton();

	GridBagLayout gridBagLayout2 = new GridBagLayout();

	ButtonGroup buttonGroup1 = new ButtonGroup();

	GridBagLayout gridBagLayout3 = new GridBagLayout();

	JButton btnLoadOption = new JButton("Load Option File");

	JButton btnSaveOption = new JButton("Save Option File");

	JButton btnFastRun = new JButton("Fast Run");

	private JRcpaCheckBox cbExportIndividual = new JRcpaCheckBox(
			"ExportIndividual", "Export Individual", 0);

	private JRcpaCheckBox cbDistributionOnly = new JRcpaCheckBox(
			"DistributionOnly", "Distribution Only", 0);

	private JRcpaCheckTextField cbModifiedPeptides = new JRcpaCheckTextField(
			"ModifiedAminoacid", "Modified Peptide Only", false,
			"Modified Aminoacid", "");

	public DistributionCalculatorUI() {
		try {
			initPanels();
			loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadConfig() {
		try {
			XMLFile currentOption = new XMLFile(configFile);
			optionDetailList.loadFromFile(currentOption);
			lastDirectory = currentOption.getElementValue("LastDirectory", "");
		} catch (Exception ex) {
			throw new RuntimeException("Load config file error : " + ex.getMessage());
		}
	}

	private void saveConfig() {
		try {
			XMLFile currentOption = new XMLFile(configFile);
			optionDetailList.saveToFile(currentOption);
			currentOption.setElementValue("LastDirectory", lastDirectory);
			currentOption.saveToFile();
		} catch (Exception ex) {
			throw new RuntimeException("Save config file error : " + ex.getMessage());
		}
	}

	private void initPanels() throws Exception {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		totalPanels[0] = pnlOptionDetails;
		totalPanels[1] = pnlModifyClassification;

		optionDetailList.addComponent(distributionType);

		distributionType.asComboBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (distributionType.getSelectedItem().equals(DistributionType.PEPTIDE)) {
					targetFile.setFileArgument(new OpenFileArgument("Peptides",
							"peptides"));
				} else {
					targetFile.setFileArgument(new OpenFileArgument("Noredundant",
							"noredundant"));
				}
			}
		});

		optionDetailList.addComponent(targetFile);
		optionDetailList.addComponent(databaseType);
		optionDetailList.addComponent(classificationType);
		optionDetailList.addComponent(classificationTitle);
		optionDetailList.addComponent(filterType);
		optionDetailList.addComponent(loopDefinition);
		optionDetailList.addComponent(cbModifiedPeptides);

		this.setTitle(Constants.getSQHTitle(title, version));
		pnlModifyClassification.setLayout(gridBagLayout2);
		pnlModifyClassification.setPreferredSize(new Dimension(0, 0));
		btnClose.setText("Close");
		btnClose.addActionListener(new ComparatorUI_btnClose_actionAdapter(this));
		btnNext.setText("Next");
		btnNext.addActionListener(new ComparatorUI_btnNext_actionAdapter(this));
		btnBack.setText("Back");
		btnBack.addActionListener(new ComparatorUI_btnBack_actionAdapter(this));
		btnReset.setText("Reset");
		btnReset.addActionListener(new ComparatorUI_btnReset_actionAdapter(this));
		btnMerge.setText("Merge");
		btnMerge.addActionListener(new ComparatorUI_btnMerge_actionAdapter(this));
		btnChangeClassifiedName.setText("Change Classified Name");
		btnChangeClassifiedName
				.addActionListener(new ComparatorUI_btnChangeClassifiedName_actionAdapter(
						this));

		jPanel1.setDoubleBuffered(true);
		jPanel1.setMinimumSize(new Dimension(197, 50));
		jPanel1.setPreferredSize(new Dimension(197, 50));
		btnSaveOption
				.addActionListener(new DistributionCalculatorUI_btnSaveOption_actionAdapter(
						this));
		btnLoadOption
				.addActionListener(new DistributionCalculatorUI_btnLoadOption_actionAdapter(
						this));
		btnSaveOption.setEnabled(true);
		btnFastRun
				.addActionListener(new DistributionCalculatorUI_btnFastRun_actionAdapter(
						this));

		pnlModifyClassification.add(jScrollPane1, new GridBagConstraints(0, 0, 1,
				4, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		pnlModifyClassification.add(btnMerge, new GridBagConstraints(1, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 0, 10), 0, 0));
		pnlModifyClassification.add(btnChangeClassifiedName,
				new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
		pnlModifyClassification.add(btnReset, new GridBagConstraints(1, 2, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 0, 10), 0, 0));
		cbExportIndividual.addTo(pnlModifyClassification, 5, 2);
		cbDistributionOnly.addTo(pnlModifyClassification, 6, 2);

		jScrollPane1.getViewport().add(tvwClassification, null);

		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(btnLoadOption, null);
		jPanel1.add(btnSaveOption, null);
		jPanel1.add(btnBack, null);
		jPanel1.add(btnNext, null);
		jPanel1.add(btnFastRun, null);
		jPanel1.add(btnClose, null);

		classificationType.asComboBox().addItemListener(
				new DistributionCalculatorUI_cbxClassificationType_itemAdapter(this));

		cbxClassificationType_itemStateChanged(null);

		clearTree();
		getRootNode().setUserObject("ClassificationSet");

		pnlOptionDetails.setLayout(gridBagLayout3);
		optionDetailList.addTo(pnlOptionDetails, 0, optionDetailList
				.columnCountNeeded());
		setCurrentPanel(0);
	}

	private void fillClassificationOption() {
		if (option != null) {
			targetFile.setFilename(option.getSourceFile().getFileName());

			filterType.setSelectedItem(option.getFilterByPeptide().getFilterType());
			loopDefinition.setFrom(option.getFilterByPeptide().getFrom());
			loopDefinition.setTo(option.getFilterByPeptide().getTo());
			loopDefinition.setStep(option.getFilterByPeptide().getStep());

			classificationType.setSelectedItem(option.getClassificationInfo()
					.getClassificationType());
			classificationTitle.setText(option.getClassificationInfo()
					.getClassificationPrinciple());
			databaseType.setSelectedItem(SequenceDatabaseType.valueOf(option
					.getDatabaseType()));
			distributionType.setSelectedItem(option.getDistributionType());
			cbModifiedPeptides.setSelected(option.getModifiedPeptideOnly());
			cbModifiedPeptides.setText(option.getModifiedAminoacid());

			loadClassificationSetFromOption();
		} else {
			targetFile.setFilename("");
			filterType.setSelectedItem(FilterType.UNIQUEPEPTIDECOUNT);
			loopDefinition.setFrom(1);
			loopDefinition.setTo(1);
			loopDefinition.setStep(1);
			classificationType.setSelectedItem(ClassificationType.METHOD);
			classificationTitle.setText("METHOD");
			databaseType.setSelectedItem(SequenceDatabaseType.IPI);
			distributionType.setSelectedItem(DistributionType.PROTEIN);
			cbModifiedPeptides.setSelected(false);
			cbModifiedPeptides.setText("");
			clearTree();
		}
	}

	private void checkOptionDetails() throws IllegalAccessException {
		optionDetailList.validate();

		try {
			final ClassificationInfo classificationInfo = DistributionOptionUtils
					.createClassificationInfo((ClassificationType) classificationType
							.getSelectedItem(), classificationTitle.getText());
			final FilterByPeptide filterByPeptide = DistributionOptionUtils
					.createFilterByPeptide((FilterType) filterType.getSelectedItem(),
							loopDefinition.getFrom(), loopDefinition.getTo(), loopDefinition
									.getStep());

			if (option == null
					|| !option.getSourceFile().getFileName().equals(
							targetFile.getFilename())) {
				SourceFile sf = new SourceFile();
				sf.setFileName(targetFile.getFilename());
				sf.setFileType(IdentifiedResultFileType.BUILD_SUMMARY.toString());

				optionFile = new DistributionOptionGenerator().createOptionFile(sf,
						databaseType.getSelectedItem(), distributionType.getSelectedItem(),
						classificationInfo, filterByPeptide, cbModifiedPeptides
								.isSelected(), cbModifiedPeptides.getText());

				option = (DistributionOption) DistributionOption
						.unmarshal(new FileReader(optionFile));

				loadClassificationSetFromOption();
			} else {
				option.setDistributionType(distributionType.getSelectedItem());
				option.setDatabaseType(databaseType.getSelectedItem().toString());
				option.setClassificationInfo(classificationInfo);
				option.setFilterByPeptide(filterByPeptide);
				option.setModifiedPeptideOnly(cbModifiedPeptides.isSelected());
				option.setModifiedAminoacid(cbModifiedPeptides.getText());

				optionFile = new DistributionOptionGenerator()
						.getOptionFilename(option);
				option.marshal(new FileWriter(optionFile));
			}

		} catch (Exception ex) {
			throw new IllegalAccessException("Initialize option file error : "
					+ ex.getMessage());
		}
	}

	private DefaultTreeModel getTreeModel() {
		return (DefaultTreeModel) tvwClassification.getModel();
	}

	private DefaultMutableTreeNode getRootNode() {
		return (DefaultMutableTreeNode) getTreeModel().getRoot();
	}

	private void deleteNodeChildren(DefaultMutableTreeNode node) {
		int numberOfChildren = node.getChildCount();

		ArrayList<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();

		for (int i = 0; i < numberOfChildren; i++) {
			DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) node
					.getChildAt(i);
			children.add(currentChild);
		}

		for (int i = 0; i < numberOfChildren; i++) {
			DefaultMutableTreeNode currentNode = children.get(i);
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

	private DefaultMutableTreeNode addObjectToTree(Object obj,
			DefaultMutableTreeNode parentNode) {
		DefaultMutableTreeNode result = new DefaultMutableTreeNode(obj);
		getTreeModel().insertNodeInto(result, parentNode,
				parentNode.getChildCount());
		return result;
	}

	private void loadClassificationSetFromOption() {
		clearTree();
		for (int i = 0; i < option.getClassificationSet()
				.getClassificationItemCount(); i++) {
			DefaultMutableTreeNode node = addObjectToTree(option
					.getClassificationSet().getClassificationItem(i).getClassifiedName(),
					getRootNode());
			for (int j = 0; j < option.getClassificationSet()
					.getClassificationItem(i).getExperimentNameCount(); j++) {
				addObjectToTree(option.getClassificationSet().getClassificationItem(i)
						.getExperimentName(j), node);
			}
		}
		TreePath visiblePath = new TreePath(getTreeModel().getPathToRoot(
				getRootNode().getFirstChild()));
		tvwClassification.makeVisible(visiblePath);
		tvwClassification.setSelectionPath(visiblePath);
	}

	private void goToNextPage() {
		setCurrentPanel(currPanelIndex + 1);
	}

	private boolean isLastPage() {
		return currPanelIndex == totalPanels.length - 1;
	}

	private boolean isFirstPage() {
		return currPanelIndex == 0;
	}

	private void setCurrentPanel(int panelIndex) {
		if (panelIndex >= totalPanels.length || panelIndex < 0) {
			return;
		}

		if (currPanelIndex >= 0 && currPanelIndex < totalPanels.length) {
			this.getContentPane().remove(totalPanels[currPanelIndex]);
		}

		this.getContentPane().add(totalPanels[panelIndex], BorderLayout.CENTER);
		currPanelIndex = panelIndex;

		btnBack.setEnabled(!isFirstPage());

		btnSaveOption.setEnabled(isLastPage());

		btnFastRun.setEnabled(isLastPage());

		btnNext.setText(isLastPage() ? "Run" : "Next");

		this.validate();
		this.repaint();
	}

	private void goToPrevPage() {
		setCurrentPanel(currPanelIndex - 1);
	}

	void btnClose_actionPerformed(ActionEvent e) {
		dispose();
	}

	public static void main(String[] args) {
		DistributionCalculatorUI frame = new DistributionCalculatorUI();
		frame.setSize(800, 420);
		GUIUtils.setFrameDesktopCentre(frame);
		frame.setVisible(true);
	}

	void btnBack_actionPerformed(ActionEvent e) {
		goToPrevPage();
	}

	void btnNext_actionPerformed(ActionEvent e) {
		try {
			if (currPanelIndex == 0) {
				checkOptionDetails();
			} else {
				saveOptionFileAndRun();
			}
			goToNextPage();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean saveOptionFile() {
		try {
			checkOptionDetails();

			option.getClassificationSet().clearClassificationItem();
			ArrayList<ClassificationItem> childItems = new ArrayList<ClassificationItem>();

			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) getRootNode()
					.getFirstChild();
			while (childNode != null) {
				ClassificationItem item = new ClassificationItem();
				item.setClassifiedName(childNode.toString());
				for (int i = 0; i < childNode.getChildCount(); i++) {
					item.addExperimentName(childNode.getChildAt(i).toString());
				}
				childNode = childNode.getNextSibling();
				childItems.add(item);
			}

			Collections.sort(childItems, new ClassificationItemComparator());

			for (Iterator iter = childItems.iterator(); iter.hasNext();) {
				ClassificationItem item = (ClassificationItem) iter.next();
				option.getClassificationSet().addClassificationItem(item);
			}

			option.marshal(new FileWriter(optionFile));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Save option file error :"
					+ ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private void saveOptionFileAndRun() {
		saveConfig();

		saveOptionFile();

		try {
			IFileProcessor processor;
			if (DistributionType.PROTEIN.equals(option.getDistributionType())) {
				processor = DistributionCalculatorFactory
						.getProteinDistributionCalculator(option.getClassificationInfo()
								.getClassificationType(), cbExportIndividual.isSelected());
			} else {
				processor = DistributionCalculatorFactory
						.getPeptideDistributionCalculator(option.getClassificationInfo()
								.getClassificationType(), cbExportIndividual.isSelected(),
								option.getModifiedPeptideOnly(), option.getModifiedAminoacid());
			}
			processor.process(optionFile.getAbsolutePath());

			JOptionPane.showMessageDialog(this, "Distribution calculate succeed!",
					"Congratulation", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	void btnReset_actionPerformed(ActionEvent e) {
		loadClassificationSetFromOption();
	}

	private TreeSelectionModel getTreeSelectionModel() {
		return tvwClassification.getSelectionModel();
	}

	void btnMerge_actionPerformed(ActionEvent e) {
		TreePath[] paths = getTreeSelectionModel().getSelectionPaths();

		ArrayList<String> experimentNames = new ArrayList<String>();
		String classifiedName = "";
		DefaultMutableTreeNode firstNode = null;
		for (int i = 0; i < paths.length; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
					.getLastPathComponent();
			if (node.getLevel() == 2) {
				continue;
			}
			if (node.getLevel() == 1) {
				if (classifiedName.length() == 0) {
					classifiedName = node.toString();
					firstNode = node;
				} else {
					for (int j = 0; j < node.getChildCount(); j++) {
						experimentNames.add(node.getChildAt(j).toString());
					}
				}
			}
		}

		for (int i = 0; i < paths.length; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
					.getLastPathComponent();
			if (node.getLevel() == 1 && node != firstNode) {
				getTreeModel().removeNodeFromParent(node);
			}
		}

		for (int i = 0; i < experimentNames.size(); i++) {
			addObjectToTree(experimentNames.get(i), firstNode);
		}

		btnChangeClassifiedName_actionPerformed(null);
	}

	void btnChangeClassifiedName_actionPerformed(ActionEvent e) {
		if (getTreeSelectionModel().getSelectionCount() > 1) {
			JOptionPane.showMessageDialog(this,
					"Select ONLY ONE classified name and click button again!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		TreePath path = getTreeSelectionModel().getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		if (node.getLevel() != 1) {
			JOptionPane.showMessageDialog(this,
					"Select ONLY ONE classified name and click button again!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String newName = JOptionPane.showInputDialog(this,
				"Input new classified name : ", node.toString());
		if (newName == null || newName.equals(node.toString())) {
			return;
		}

		DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) getRootNode()
				.getFirstChild();
		while (childNode != null) {
			if (newName.equals(childNode.toString())) {
				JOptionPane.showMessageDialog(this, newName + " has exists!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			childNode = childNode.getNextSibling();
		}

		node.setUserObject(newName);
		getTreeModel().nodeChanged(node);
	}

	void cbxClassificationType_itemStateChanged(ItemEvent e) {
		if (classificationType.getSelectedItem() == ClassificationType.OTHER) {
			classificationTitle.setText("");
		} else {
			classificationTitle.setText(classificationType.getSelectedItem()
					.toString());
		}
	}

	public void btnSaveOption_actionPerformed(ActionEvent e) {
		saveOptionFile();
	}

	public void btnLoadOption_actionPerformed(ActionEvent e) {
		File f = new File(lastDirectory);
		JFileChooser filechooser = new JFileChooser();
		filechooser.setFileFilter(new SpecialSwingFileFilter("statistic.xml",
				"Statistic option file", false));
		filechooser.setSelectedFile(f);
		filechooser.setDialogTitle("Browse classification option file :");
		if (filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			optionFile = filechooser.getSelectedFile();
			lastDirectory = optionFile.getParent();
			saveConfig();
			try {
				option = (DistributionOption) DistributionOption
						.unmarshal(new FileReader(optionFile.getAbsolutePath()));
				fillClassificationOption();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(),
						"Load option file error", JOptionPane.ERROR_MESSAGE);
			}
			goToPrevPage();
		}
	}

	public void btnFastRun_actionPerformed(ActionEvent e) {
		saveOptionFile();
		try {
			IFileProcessor processor = option.getDistributionType() == DistributionType.PROTEIN ? new FastProteinDistributionCalculator()
					: new FastPeptideDistributionCalculator();

			List<String> resultFiles = processor
					.process(optionFile.getAbsolutePath());
			JOptionPane.showMessageDialog(this,
					"Distribution calculate succeed! Result saved to:\n"
							+ StringUtils.join(resultFiles.iterator(), "\n"),
					"Congratulation", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Run Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Statistic };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			DistributionCalculatorUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}

class DistributionCalculatorUI_btnFastRun_actionAdapter implements
		ActionListener {
	private DistributionCalculatorUI adaptee;

	DistributionCalculatorUI_btnFastRun_actionAdapter(
			DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnFastRun_actionPerformed(e);
	}
}

class DistributionCalculatorUI_btnLoadOption_actionAdapter implements
		ActionListener {
	private DistributionCalculatorUI adaptee;

	DistributionCalculatorUI_btnLoadOption_actionAdapter(
			DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnLoadOption_actionPerformed(e);
	}
}

class DistributionCalculatorUI_btnSaveOption_actionAdapter implements
		ActionListener {
	private DistributionCalculatorUI adaptee;

	DistributionCalculatorUI_btnSaveOption_actionAdapter(
			DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {

		adaptee.btnSaveOption_actionPerformed(e);
	}
}

class ComparatorUI_btnClose_actionAdapter implements
		java.awt.event.ActionListener {
	DistributionCalculatorUI adaptee;

	ComparatorUI_btnClose_actionAdapter(DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnClose_actionPerformed(e);
	}
}

class ComparatorUI_btnBack_actionAdapter implements
		java.awt.event.ActionListener {
	DistributionCalculatorUI adaptee;

	ComparatorUI_btnBack_actionAdapter(DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnBack_actionPerformed(e);
	}
}

class ComparatorUI_btnNext_actionAdapter implements
		java.awt.event.ActionListener {
	DistributionCalculatorUI adaptee;

	ComparatorUI_btnNext_actionAdapter(DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnNext_actionPerformed(e);
	}
}

class ComparatorUI_btnReset_actionAdapter implements
		java.awt.event.ActionListener {
	DistributionCalculatorUI adaptee;

	ComparatorUI_btnReset_actionAdapter(DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnReset_actionPerformed(e);
	}
}

class ComparatorUI_btnMerge_actionAdapter implements
		java.awt.event.ActionListener {
	DistributionCalculatorUI adaptee;

	ComparatorUI_btnMerge_actionAdapter(DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnMerge_actionPerformed(e);
	}
}

class ComparatorUI_btnChangeClassifiedName_actionAdapter implements
		java.awt.event.ActionListener {
	DistributionCalculatorUI adaptee;

	ComparatorUI_btnChangeClassifiedName_actionAdapter(
			DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnChangeClassifiedName_actionPerformed(e);
	}
}

class DistributionCalculatorUI_cbxClassificationType_itemAdapter implements
		java.awt.event.ItemListener {
	DistributionCalculatorUI adaptee;

	DistributionCalculatorUI_cbxClassificationType_itemAdapter(
			DistributionCalculatorUI adaptee) {
		this.adaptee = adaptee;
	}

	public void itemStateChanged(ItemEvent e) {
		adaptee.cbxClassificationType_itemStateChanged(e);
	}
}
