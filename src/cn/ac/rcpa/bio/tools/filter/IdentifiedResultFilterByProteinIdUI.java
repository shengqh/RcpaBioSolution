package cn.ac.rcpa.bio.tools.filter;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedResultFilterByProteinIdUI extends
    AbstractFileProcessorWithFileArgumentUI {
  private static String title = "IdentifiedResult Protein Identities Filter";

  public IdentifiedResultFilterByProteinIdUI() {
    super(Constants.getSQHTitle(title,
        IdentifiedResultFilterByProteinId.version), new OpenFileArgument(
        "Access Number", new String[] { "acNumber", "ipiNumber", "spNumber" }),
        new OpenFileArgument("Noredundant", "noredundant"));
  }

  @Override
  protected IFileProcessor getProcessor() {
    return new IdentifiedResultFilterByProteinId(getArgument());
  }

  public static void main(String[] args) {
    new IdentifiedResultFilterByProteinIdUI().showSelf();
  }
}
