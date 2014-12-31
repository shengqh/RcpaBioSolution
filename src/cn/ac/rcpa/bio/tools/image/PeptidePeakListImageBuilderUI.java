package cn.ac.rcpa.bio.tools.image;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import slav.boleslawski.NavigableImagePanel;
import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.AbstractUI;
import cn.ac.rcpa.bio.JRcpaModificationTextField;
import cn.ac.rcpa.bio.proteomics.IonType;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.image.peptide.ImageType;
import cn.ac.rcpa.bio.proteomics.image.peptide.MatchedSpectrumImageBuilder;
import cn.ac.rcpa.bio.proteomics.image.peptide.PeakIonSeriesMatcher;
import cn.ac.rcpa.bio.proteomics.image.peptide.SequestPeptideResult;
import cn.ac.rcpa.component.IRcpaComponent;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaComponentProxy;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.component.JRcpaHorizontalComponentList;
import cn.ac.rcpa.component.JRcpaObjectCheckBox;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.OpenFileArgument;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.SaveFileArgument;

public class PeptidePeakListImageBuilderUI extends AbstractUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3456917940116892871L;

	private static String title = "Peptide PeakList Matching Image Builder";

	private static String version = "1.0.3";

	private JLabel ion = new JLabel("Select Ion Type");

	private JRcpaComponentProxy lblIon = new JRcpaComponentProxy(ion, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, 1);

	private JRcpaObjectCheckBox<IonType> bIons = new JRcpaObjectCheckBox<IonType>("B_IONS", "B Ions", 1, IonType.B);

	private JRcpaObjectCheckBox<IonType> b2Ions = new JRcpaObjectCheckBox<IonType>("B2_IONS", "B2 Ions", 2, IonType.B2);

	private JRcpaObjectCheckBox<IonType> b_NH3Ions = new JRcpaObjectCheckBox<IonType>("B_NH3IONS", "B-NH3 Ions", 3, IonType.B_NH3);

	private JRcpaObjectCheckBox<IonType> b_H2OIons = new JRcpaObjectCheckBox<IonType>("B_H2OIONS", "B-H2O Ions", 4, IonType.B_H2O);

	private JRcpaObjectCheckBox<IonType> aIons = new JRcpaObjectCheckBox<IonType>("A_IONS", "A Ions", 5, IonType.A);

	private JRcpaObjectCheckBox<IonType> cIons = new JRcpaObjectCheckBox<IonType>("C_IONS", "C Ions", 6, IonType.C);

	private JRcpaHorizontalComponentList bIonList = new JRcpaHorizontalComponentList();

	private JRcpaObjectCheckBox<IonType> yIons = new JRcpaObjectCheckBox<IonType>("Y_IONS", "Y Ions", 1, IonType.Y);

	private JRcpaObjectCheckBox<IonType> y2Ions = new JRcpaObjectCheckBox<IonType>("Y2_IONS", "Y2 Ions", 2, IonType.Y2);

	private JRcpaObjectCheckBox<IonType> y_NH3Ions = new JRcpaObjectCheckBox<IonType>("Y_NH3IONS", "Y-NH3 Ions", 3, IonType.Y_NH3);

	private JRcpaObjectCheckBox<IonType> y_H2OIons = new JRcpaObjectCheckBox<IonType>("Y_H2OIONS", "Y-H2O Ions", 4, IonType.Y_H2O);

	private JRcpaObjectCheckBox<IonType> zIons = new JRcpaObjectCheckBox<IonType>("Z_IONS", "Z Ions", 6, IonType.Z);

	private JRcpaHorizontalComponentList yIonList = new JRcpaHorizontalComponentList();

	private JRcpaComboBox<IsotopicType> cbIsotopicType = new JRcpaComboBox<IsotopicType>("IsotopicType", "Isotopic Type", IsotopicType.values(), IsotopicType.Monoisotopic);

	private JRcpaTextField txtSequence = new JRcpaTextField("Sequence", "Peptide Sequence", "", true);

	private JRcpaTextField txtPeakTolerance = new JRcpaTextField("PeakTolerance", "Peak Tolerance (da)", "0.3", true);

	private JRcpaModificationTextField txtStaticModification = new JRcpaModificationTextField("StaticModification", "Static Modification (as: C +57.02 STY -17.99)", "", false);

	private JRcpaModificationTextField txtDynamicModification = new JRcpaModificationTextField("DynamicModification", "Dynamic Modification (as: @ +79.99 # +15.99)", "", false);

	private JRcpaFileField txtDtaFile = new JRcpaFileField("DtaFile", new OpenFileArgument("Dta", "dta"), true);

	private JRcpaFileField txtResultFile = new JRcpaFileField("ResultFile", new SaveFileArgument("Result Picture", "jpg"), false);

	private NavigableImagePanel pnlImage = new NavigableImagePanel();

	private JRcpaComponentProxy pnlImageProxy = new JRcpaComponentProxy(pnlImage, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 0);

	public PeptidePeakListImageBuilderUI() {
		super(Constants.getSQHTitle(title, version));

		bIonList.addComponent(lblIon);
		bIonList.addComponent(bIons);
		bIonList.addComponent(b2Ions);
		bIonList.addComponent(b_NH3Ions);
		bIonList.addComponent(b_H2OIons);
		bIonList.addComponent(aIons);
		bIonList.addComponent(cIons);

		yIonList.addComponent(yIons);
		yIonList.addComponent(y2Ions);
		yIonList.addComponent(y_NH3Ions);
		yIonList.addComponent(y_H2OIons);
		yIonList.addComponent(zIons);

		this.addComponent(bIonList);
		this.addComponent(yIonList);

		this.addComponent(cbIsotopicType);
		this.addComponent(txtPeakTolerance);

		this.addComponent(txtSequence);
		this.addComponent(txtStaticModification);
		this.addComponent(txtDynamicModification);
		this.addComponent(txtDtaFile);
		this.addComponent(txtResultFile);
		this.addComponent(pnlImageProxy);
	}

	public static void main(String[] args) {
		PeptidePeakListImageBuilderUI ui = new PeptidePeakListImageBuilderUI();
		ui.setExtendedState(Frame.MAXIMIZED_BOTH);
		ui.showSelf();
	}

	@Override
	protected int getPerfectWidth() {
		return 1024;
	}

	@Override
	protected int getPerfectHeight() {
		return 800;
	}

	@Override
	protected void doRealGo() {
		double peakTolerance;
		try {
			peakTolerance = Double.parseDouble(txtPeakTolerance.getText());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Input Peak Tolerance (da) First", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String dtaFilename = txtDtaFile.getFilename();
		String resultFile = txtResultFile.getFilename();
		if (resultFile.length() == 0) {
			resultFile = dtaFilename + ".jpg";
		}

		String peptide = txtSequence.getText().trim();
		txtSequence.setText(peptide);

		IonType[] ionTypes = getIonTypes();
		try {
			IsotopicType peakMassType = cbIsotopicType.getSelectedItem();
			Map<Character, Double> staticModification = txtStaticModification.getModificationMap(peakMassType);
			Map<Character, Double> dynamicModification = txtDynamicModification.getModificationMap(peakMassType);

			SequestPeptideResult spr = new SequestPeptideResult(peptide, ionTypes);
			boolean peakMonoMass = peakMassType == IsotopicType.Monoisotopic;
			spr.initTheoreticalPeaks(true, peakMonoMass, staticModification, dynamicModification);

			String[] dtaFiles = RcpaFileUtils.readFile(dtaFilename);
			spr.parseDtaFile(dtaFilename, dtaFiles);

			MatchedSpectrumImageBuilder builder = new MatchedSpectrumImageBuilder(ImageType.tiff);
			builder.setDimension(new Dimension(3000, 600));
			builder.setIdentifiedResult(spr);

			for (IonType iType : ionTypes) {
				builder.addMatcher(new PeakIonSeriesMatcher(iType, peakTolerance));
			}

			FileOutputStream fos = new FileOutputStream(resultFile);
			builder.drawImage(fos);
			fos.close();

			pnlImage.setImage(ImageIO.read(new File(resultFile)));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private IonType[] getIonTypes() {
		List<IonType> result = new ArrayList<IonType>();
		result.addAll(getIonTypesFromComponentList(bIonList));
		result.addAll(getIonTypesFromComponentList(yIonList));
		return result.toArray(new IonType[0]);
	}

	@SuppressWarnings("unchecked")
	private List<IonType> getIonTypesFromComponentList(JRcpaHorizontalComponentList lst) {
		List<IonType> result = new ArrayList<IonType>();
		for (IRcpaComponent comp : lst.getComponents()) {
			if (comp instanceof JRcpaObjectCheckBox) {
				JRcpaObjectCheckBox<IonType> ocb = (JRcpaObjectCheckBox<IonType>) comp;
				if (ocb.isSelected()) {
					result.add(ocb.getObject());
				}
			}
		}
		return result;
	}
}
