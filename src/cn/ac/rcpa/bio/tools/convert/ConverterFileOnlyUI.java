package cn.ac.rcpa.bio.tools.convert;

import cn.ac.rcpa.bio.IConverter;
import cn.ac.rcpa.utils.XMLFile;

public class ConverterFileOnlyUI
    extends AbstractConverterUI {
  private IConverter converter;

  public ConverterFileOnlyUI(IConverter converter, String title,
                             String filetype, String extension) {
    super(title, filetype, extension);
    this.converter = converter;
  }

  @Override
      protected IConverter getConverter() {
    return converter;
  }

  @Override
      protected int getPerfectHeight() {
    return 150;
  }

  @Override
      protected int getPerfectWidth() {
    return 800;
  }

  @Override
      protected void loadOptionElements(XMLFile option) {
    return;
  }

  @Override
      protected void setOptionElements(XMLFile option) {
    return;
  }

  @Override
      protected int initParameterComponents(int curRow) {
    return curRow;
  }

  @Override
      protected boolean validateOtherParameters() {
    return true;
  }
}
