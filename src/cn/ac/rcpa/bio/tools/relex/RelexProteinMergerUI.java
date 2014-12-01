package cn.ac.rcpa.bio.tools.relex;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdom.JDOMException;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.utils.GUIUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.XMLFile;

public class RelexProteinMergerUI extends JFrame {
  private static String title = "Multiple Relex Result Comparator";

  public static String version = "1.0.1";

  private String optionFileName = RcpaFileUtils.getConfigFile(this.getClass());

  JButton btnRootDirectory = new JButton("Browse root directory...");

  JTextField txtRootDirectory = new JTextField();

  JPanel pnlButton = new JPanel();

  JButton btnMerge = new JButton("Merge");

  JButton btnClose = new JButton("Close");

  GridBagLayout gridBagLayout1 = new GridBagLayout();

  JLabel labelDatabaseType = new JLabel("Database type : ");

  JComboBox cbxDatabaseType = new JComboBox(SequenceDatabaseType.values());

  JButton btnStandardDir = new JButton("Browse standard directory...");

  JTextField txtStandardDirectory = new JTextField();

  public RelexProteinMergerUI() {
    try {
      jbInit();
      loadOption();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void loadOption() {
    try {
      XMLFile option = new XMLFile(optionFileName);
      txtRootDirectory.setText(option.getElementValue("RootDirectory", ""));
      txtStandardDirectory.setText(option.getElementValue("StandardDirectory",
          ""));
      SequenceDatabaseType dbType;
      try {
        dbType = SequenceDatabaseType.valueOf(option.getElementValue("DatabaseType",
            "IPI"));
      } catch (IllegalArgumentException ex) {
        dbType = SequenceDatabaseType.IPI;
      }
      cbxDatabaseType.setSelectedItem(dbType);
    } catch (Exception ex) {
      throw new RuntimeException("Load option error : " + ex.getMessage());
    }
  }

  private void saveOption() {
    try {
      XMLFile option = new XMLFile(optionFileName);
      option.setElementValue("RootDirectory", txtRootDirectory.getText());
      option.setElementValue("StandardDirectory", txtStandardDirectory
          .getText());
      option.setElementValue("DatabaseType", cbxDatabaseType.getSelectedItem()
          .toString());
      option.saveToFile();
    } catch (Exception ex) {
      throw new RuntimeException("Save option error : " + ex.getMessage());
    }
  }

  private void jbInit() throws Exception {
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.getContentPane().setLayout(gridBagLayout1);

    this.setTitle(Constants.getSQHTitle(title, version));
    btnClose.addActionListener(new RelexProteinMergerUI_btnClose_actionAdapter(
        this));
    txtRootDirectory.setText("");
    btnRootDirectory
        .addActionListener(new RelexProteinMergerUI_btnDirectory_actionAdapter(
            this));
    btnStandardDir
        .addActionListener(new RelexProteinMergerUI_btnStandardDir_actionAdapter(
            this));
    btnMerge.addActionListener(new RelexProteinMergerUI_btnMerge_actionAdapter(
        this));
    pnlButton.add(btnMerge, null);
    pnlButton.add(btnClose, null);

    this.getContentPane().add(
        btnRootDirectory,
        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(
        txtRootDirectory,
        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(
        btnStandardDir,
        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
            GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(
        txtStandardDirectory,
        new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(
        labelDatabaseType,
        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
            GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(
        cbxDatabaseType,
        new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(
        pnlButton,
        new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
  }

  void btnClose_actionPerformed(ActionEvent e) {
    dispose();
  }

  public static void main(String[] args) {
    RelexProteinMergerUI frame = new RelexProteinMergerUI();
    frame.setSize(800, 220);
    GUIUtils.setFrameDesktopCentre(frame);
    frame.setVisible(true);
  }

  public void btnDirectory_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (txtRootDirectory.getText().trim().length() != 0) {
      chooser.setSelectedFile(new File(txtRootDirectory.getText().trim()));
    }
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      txtRootDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
    }
  }

  public void btnStandardDir_actionPerformed(ActionEvent e) {
    if (!rootDirectoryVisible()) {
      return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setSelectedFile(new File(txtRootDirectory.getText().trim(),
        txtStandardDirectory.getText().trim()));

    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      txtStandardDirectory.setText(chooser.getSelectedFile().getName());
    }
  }

  private boolean rootDirectoryVisible() {
    txtRootDirectory.setText(txtRootDirectory.getText().trim());

    if (!new File(txtRootDirectory.getText().trim()).isDirectory()) {
      JOptionPane.showMessageDialog(this,
          "Root directory empty or not exists, input root directory first!",
          "Error!", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return true;
  }

  private boolean standardDirectoryVisible() {
    if (!rootDirectoryVisible()) {
      return false;
    }

    txtStandardDirectory.setText(txtStandardDirectory.getText().trim());

    File standardDirectory = new File(txtRootDirectory.getText(),
        txtStandardDirectory.getText());

    if (!standardDirectory.isDirectory()) {
      JOptionPane.showMessageDialog(this,
          "Standard directory not exists, reinput again!", "Error!",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return true;
  }

  public void btnMerge_actionPerformed(ActionEvent e) {
    if (!rootDirectoryVisible() || !standardDirectoryVisible()) {
      return;
    }

    saveOption();

    File rootDir = new File(txtRootDirectory.getText());
    File resultFile = new File(rootDir, rootDir.getName() + ".xls");
    try {
      RelexProteinMerger.merge(rootDir, resultFile, txtStandardDirectory
          .getText(), (SequenceDatabaseType) cbxDatabaseType.getSelectedItem());
      JOptionPane.showMessageDialog(this, "Finished!", "Congratulation",
          JOptionPane.PLAIN_MESSAGE);
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Error : " + ex.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}

class RelexProteinMergerUI_btnStandardDir_actionAdapter implements
    ActionListener {
  private RelexProteinMergerUI adaptee;

  RelexProteinMergerUI_btnStandardDir_actionAdapter(RelexProteinMergerUI adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnStandardDir_actionPerformed(e);
  }
}

class RelexProteinMergerUI_btnDirectory_actionAdapter implements ActionListener {
  private RelexProteinMergerUI adaptee;

  RelexProteinMergerUI_btnDirectory_actionAdapter(RelexProteinMergerUI adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnDirectory_actionPerformed(e);
  }
}

class RelexProteinMergerUI_btnClose_actionAdapter implements
    java.awt.event.ActionListener {
  RelexProteinMergerUI adaptee;

  RelexProteinMergerUI_btnClose_actionAdapter(RelexProteinMergerUI adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnClose_actionPerformed(e);
  }
}

class RelexProteinMergerUI_btnMerge_actionAdapter implements ActionListener {
  private RelexProteinMergerUI adaptee;

  RelexProteinMergerUI_btnMerge_actionAdapter(RelexProteinMergerUI adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnMerge_actionPerformed(e);
  }
}
