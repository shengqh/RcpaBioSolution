package cn.ac.rcpa.bio.tools;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorByDatabaseTypeUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.OpenFileArgument;

public class ProteinPeptideMapCreatorUI extends
    AbstractFileProcessorByDatabaseTypeUI {
  private static String title = "Get Protein Peptide Map From BuildSummary Result";

  public ProteinPeptideMapCreatorUI() {
    super(Constants.getSQHTitle(title, ProteinPeptideMapCreator.version),
        new OpenFileArgument("Proteins", new String[] { "proteins",
            "noredundant" }));
  }

  @Override
  protected IFileProcessor getProcessor() {
    return new ProteinPeptideMapCreator(getDatabaseType());
  }

  public static void main(String[] args) {
    new ProteinPeptideMapCreatorUI().showSelf();
  }

}
