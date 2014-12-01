package cn.ac.rcpa.bio.tools.convert;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.OpenFileArgument;

public class Dtas2MGFUI extends AbstractFileProcessorUI{
	private static String title = "Convert Merged Dta Format (Dtas) To MGF Format";
	public Dtas2MGFUI() {
		super(Constants.getSQHTitle(title, Dtas2MGF.version),
        new OpenFileArgument("Merged Dta", "dtas"));
	}

	public static void main(String[] args) {
		new Dtas2MGFUI().showSelf();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new Dtas2MGF();
	}

}
