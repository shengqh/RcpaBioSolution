package cn.ac.rcpa.bio.tools.convert;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cn.ac.rcpa.utils.XMLFile;

/**
 * <p>Title: RCPA Package</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: RCPA.SIBS.AC.CN</p>
 * @author not attributable
 * @version 1.0
 */

public abstract class AbstractConverterFileAndArgumentUI
    extends AbstractConverterUI {
  JLabel lblArgument;
  JTextField txtArgument;

  private String argumentName;

  public AbstractConverterFileAndArgumentUI(String title,
                                            String filetype,
                                            String extension,
                                            String argumentName) {
    super(title, filetype, extension);
    this.argumentName = argumentName;
  }

  @Override
      protected void loadOptionElements(XMLFile option) {
    txtArgument.setText(option.getElementValue(getXMLCompartibleName(argumentName), ""));
  }

  @Override
      protected void setOptionElements(XMLFile option) {
    option.setElementValue(getXMLCompartibleName(argumentName), txtArgument.getText());
  }

  @Override
      protected int initParameterComponents(int curRow) {
    int result = curRow + 1;
    lblArgument = new JLabel("Input " + argumentName);
    txtArgument = new JTextField("");
    this.getContentPane().add(lblArgument,
                              new GridBagConstraints(0, result, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(10, 10, 0, 10), 0, 0));
    this.getContentPane().add(txtArgument,
                              new GridBagConstraints(1, result, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(10, 10, 0, 10), 0, 0));

    return result;
  }

  protected String getArgument() {
    return txtArgument.getText();
  }

  @Override
      protected boolean validateOtherParameters() {
    final String modifiedAminoacid = txtArgument.getText().trim();
    if (modifiedAminoacid.length() == 0) {
      JOptionPane.showMessageDialog(this,
                                    "Error : set candidate modified amino acids first!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    txtArgument.setText(modifiedAminoacid);
    return true;
  }

  @Override
      protected int getPerfectWidth() {
    return 800;
  }

  @Override
      protected int getPerfectHeight() {
    return 180;
  }
}
