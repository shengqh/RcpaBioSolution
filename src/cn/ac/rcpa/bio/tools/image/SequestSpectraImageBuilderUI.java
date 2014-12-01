package cn.ac.rcpa.bio.tools.image;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.image.peptide.ImageType;
import cn.ac.rcpa.bio.proteomics.image.peptide.SequestSpectraImageBuilder;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaCheckBox;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaDoubleField;
import cn.ac.rcpa.component.JRcpaIntegerField;
import cn.ac.rcpa.utils.DirectoryArgument;

public class SequestSpectraImageBuilderUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static String title = "Sequest Spectra Image Builder";

	private static String version = "1.1.3";

	private JRcpaIntegerField scale = new JRcpaIntegerField("Scale", "Scale",
			1, true);

	private JRcpaDoubleField peakTolerance = new JRcpaDoubleField(
			"PeakTolerance", "Peak Tolerance", 0.3, true);

	private JRcpaDoubleField precursorNeutralLossMinIntensityScale = new JRcpaDoubleField(
			"precursorNeutralLossMinIntensityScale",
			"Precursor Neutral Loss Ion Min Intensity Scale", 0.3, true);

	private JRcpaDoubleField byNeutralLossMinIntensityScale = new JRcpaDoubleField(
			"byNeutralLossMinIntensityScale",
			"B/Y Neutral Loss Ion Min Intensity Scale", 0.1, true);

	private JRcpaDoubleField byMinIntensityScale = new JRcpaDoubleField(
			"byMinIntensityScale", "B/Y Ion Min Intensity Scale", 0.05, true);

	private JRcpaCheckBox drawRemoveNeutralLoss = new JRcpaCheckBox(
			"drawRemoveNeutralLoss", "Draw Remove Precursor NeutralLoss Image",
			1);

	private JRcpaComboBox<ImageType> drawImageTypes = new JRcpaComboBox<ImageType>(
			"imageType", "Image Type", new ImageType[] { ImageType.tiff,
					ImageType.png, ImageType.jpg }, ImageType.tiff);

	public SequestSpectraImageBuilderUI() {
		super(Constants.getSQHTitle(title, version), new DirectoryArgument(
				"Dta/Out"), new DirectoryArgument("Target Picture"));
		this.addComponent(scale);
		this.addComponent(peakTolerance);
		this.addComponent(precursorNeutralLossMinIntensityScale);
		this.addComponent(byNeutralLossMinIntensityScale);
		this.addComponent(byMinIntensityScale);
		this.addComponent(drawRemoveNeutralLoss);
		this.addComponent(drawImageTypes);
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new SequestSpectraImageBuilder(getArgument(), scale.getValue(),
				peakTolerance.getValue(), precursorNeutralLossMinIntensityScale
						.getValue(), byNeutralLossMinIntensityScale.getValue(),
				byMinIntensityScale.getValue(), drawRemoveNeutralLoss
						.isSelected(), drawImageTypes.getSelectedItem());
	}

	public static void main(String[] args) {
		new SequestSpectraImageBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Annotation };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			SequestSpectraImageBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
