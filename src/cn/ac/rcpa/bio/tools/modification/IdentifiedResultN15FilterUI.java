package cn.ac.rcpa.bio.tools.modification;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.OpenFileArgument;

public class IdentifiedResultN15FilterUI extends AbstractFileProcessorWithFileArgumentUI {
  private static String title = "IdentifiedResult N14/N15 Filter";

  public static String version = "1.0.2";

  public IdentifiedResultN15FilterUI() {
		super(Constants.getSQHTitle(title, version),
				new OpenFileArgument("Noredundant", "noredundant"),
				new OpenFileArgument("Sequest Parameter","params"));
  }

  public static void main(String[] args) {
    new IdentifiedResultN15FilterUI().showSelf();
  }

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new IdentifiedResultN15Filter(getArgument());
	}
}

