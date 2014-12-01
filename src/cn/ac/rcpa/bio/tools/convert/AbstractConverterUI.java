package cn.ac.rcpa.bio.tools.convert;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdom.JDOMException;

import cn.ac.rcpa.bio.IConverter;
import cn.ac.rcpa.utils.GUIUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.SpecialSwingFileFilter;
import cn.ac.rcpa.utils.XMLFile;

/**
 * <p>Title: RCPA Package</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: RCPA.SIBS.AC.CN</p>
 * @author not attributable
 * @version 1.0
 */

public abstract class AbstractConverterUI
    extends JFrame {
  private String title;
  private String filetype;
  private String extension;

  private String optionFileName = RcpaFileUtils.getConfigFile(this.getClass());

  JPanel pnlButton = new JPanel();
  JButton btnGo = new JButton("Go");
  JButton btnClose = new JButton("Close");

  JButton btnFile = new JButton("Select File ...");

  JTextField txtFile = new JTextField("");
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public AbstractConverterUI(String title, String filetype, String extension) {
    super();

    this.title = title;
    this.filetype = filetype;
    this.extension = extension;
  }

  protected String getXMLCompartibleName(String oldname){
    return oldname.replaceAll("\\s","_");
  }

  private void loadOption() {
    try {
      XMLFile option = new XMLFile(optionFileName);
      txtFile.setText(option.getElementValue(getXMLCompartibleName(filetype), ""));
      loadOptionElements(option);
    }
    catch (Exception ex) {
      throw new RuntimeException("Load option error : " + ex.getMessage());
    }
  }

  private void saveOption() {
    try {
      XMLFile option = new XMLFile(optionFileName);
      option.setElementValue(getXMLCompartibleName(filetype), txtFile.getText());
      setOptionElements(option);
      option.saveToFile();
    }
    catch (Exception ex) {
      throw new RuntimeException("Save option error : " + ex.getMessage());
    }
  }

  void jbInit() throws Exception {
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setTitle(title);
    this.getContentPane().setLayout(gridBagLayout1);

    btnFile.setText("Select " + filetype + " File...");
    btnFile.addActionListener(new
                              AbstractConverterUI_btnFile_actionAdapter(this));

    this.getContentPane().add(btnFile,
                              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(txtFile,
                              new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(10, 10, 0, 10), 10, 0));

    int iCurRow = initParameterComponents(0);

    this.getContentPane().add(pnlButton,
                              new GridBagConstraints(0, iCurRow + 1, 2, 1, 1.0,
        1.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(10, 10, 10, 10), 0, 0));

    btnClose.setMaximumSize(new Dimension(80, 25));
    btnClose.setMinimumSize(new Dimension(80, 25));
    btnClose.setPreferredSize(new Dimension(80, 25));
    btnClose.setRolloverEnabled(false);
    btnClose.addActionListener(new
                               AbstractConverterUI_btnClose_actionAdapter(this));

    btnGo.setMaximumSize(new Dimension(80, 25));
    btnGo.setMinimumSize(new Dimension(80, 25));
    btnGo.setPreferredSize(new Dimension(80, 25));
    btnGo.setRolloverEnabled(false);
    btnGo.addActionListener(new
                            AbstractConverterUI_btnGo_actionAdapter(this));

    pnlButton.add(btnGo, null);
    pnlButton.add(btnClose, null);
  }

  void btnClose_actionPerformed(ActionEvent e) {
    dispose();
  }

  void btnFile_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setFileFilter(new SpecialSwingFileFilter(extension,
        filetype + " File", false));
    if (txtFile.getText() != null &&
        txtFile.getText().trim().length() != 0) {
      chooser.setSelectedFile(new File(txtFile.getText().trim()));
    }
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      txtFile.setText(chooser.getSelectedFile().getAbsolutePath());
    }
  }

  private boolean validateParameters() {
    final File originFile = new File(txtFile.getText().trim());
    if (! (originFile.isFile())) {
      JOptionPane.showMessageDialog(this,
                                    "Error : input " + filetype + " file first",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return validateOtherParameters();
  }

  void btnGo_actionPerformed(ActionEvent e) {
    if (!validateParameters()) {
      return;
    }

    saveOption();

    try {
      List<String> resultFile = getConverter().convert(txtFile.getText().trim());

      JOptionPane.showMessageDialog(this, "Result has saved to : " + resultFile,
                                    "Congratulation", JOptionPane.PLAIN_MESSAGE);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Error : " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void showSelf() {
    try {
      jbInit();
      loadOption();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return;
    }
    this.setSize(getPerfectWidth(), getPerfectHeight());
    GUIUtils.setFrameDesktopCentre(this);
    this.setVisible(true);
  }

  protected abstract void loadOptionElements(XMLFile option);

  protected abstract void setOptionElements(XMLFile option);

  protected abstract int initParameterComponents(int curRow);

  protected abstract boolean validateOtherParameters();

  protected abstract IConverter getConverter();

  protected abstract int getPerfectWidth();

  protected abstract int getPerfectHeight();
}

class AbstractConverterUI_btnFile_actionAdapter
    implements java.awt.event.ActionListener {
  AbstractConverterUI adaptee;

  AbstractConverterUI_btnFile_actionAdapter(
      AbstractConverterUI adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnFile_actionPerformed(e);
  }
}

class AbstractConverterUI_btnClose_actionAdapter
    implements java.awt.event.ActionListener {
  AbstractConverterUI adaptee;

  AbstractConverterUI_btnClose_actionAdapter(
      AbstractConverterUI adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnClose_actionPerformed(e);
  }
}

class AbstractConverterUI_btnGo_actionAdapter
    implements java.awt.event.ActionListener {
  AbstractConverterUI adaptee;

  AbstractConverterUI_btnGo_actionAdapter(
      AbstractConverterUI adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnGo_actionPerformed(e);
  }
}
