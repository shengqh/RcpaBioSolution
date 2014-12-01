package cn.ac.rcpa.bio.tools.image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.AccessException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.AbstractUI;
import cn.ac.rcpa.bio.JRcpaModificationTextField;
import cn.ac.rcpa.bio.proteomics.IonType;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.image.peptide.IdentifiedPeptideResultImageBuilder;
import cn.ac.rcpa.bio.proteomics.image.peptide.ImageType;
import cn.ac.rcpa.bio.proteomics.image.peptide.MatchedPeak;
import cn.ac.rcpa.bio.proteomics.image.peptide.SequestPeptideResult;
import cn.ac.rcpa.bio.proteomics.mascot.MascotGenericFormatIterator;
import cn.ac.rcpa.bio.proteomics.utils.SlimIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.utils.SlimIdentifiedPeptideReader;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.chem.Atom;
import cn.ac.rcpa.component.JRcpaCheckBox;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaDoubleField;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.component.JRcpaIntegerField;
import cn.ac.rcpa.utils.DirectoryArgument;
import cn.ac.rcpa.utils.OpenFileArgument;

public class MascotSpectraMultipleFileImageBuilderUI extends AbstractUI {

	private static String title = "Mascot Spectra Image Multiple File Builder";

	private static String version = "1.0.4";

	private JRcpaModificationTextField txtStaticModification = new JRcpaModificationTextField(
			"StaticModification",
			"Static Modification (as: C +57.02 STY -17.99)", "C +57.02", false);

	private JRcpaModificationTextField txtDynamicModification = new JRcpaModificationTextField(
			"DynamicModification",
			"Dynamic Modification (as: @ +79.99 # +15.99)", "", false);

	private JRcpaFileField txtMgfFile = new JRcpaFileField("MgfDirectory",
			new DirectoryArgument("Mascot Generic Format File"), true);

	private JRcpaFileField txtMascotResultFile = new JRcpaFileField(
			"IdentifiedPeptides", new OpenFileArgument("Identified Peptides",
					"peptides"), true);

	private JRcpaFileField txtTarget = new JRcpaFileField("TargetDir",
			new DirectoryArgument("Target"), true);

	private JRcpaIntegerField txtScale = new JRcpaIntegerField("Scale",
			"Scale", 1, true);

	private JRcpaCheckBox cbPrecursorMonoisotopic = new JRcpaCheckBox(
			"PrecursorMonoisotopic", "Is precursor monoisotopic?", 1);

	private JRcpaDoubleField txtPrecursorTolerance = new JRcpaDoubleField(
			"PrecursorTolerance", "Precursor Tolerance", 0.5, true);

	private JRcpaDoubleField txtPeakTolerance = new JRcpaDoubleField(
			"PeakTolerance", "Peak Tolerance", 0.3, true);

	private JRcpaDoubleField txtPrecursorNeutralLossMinIntensityScale = new JRcpaDoubleField(
			"precursorNeutralLossMinIntensityScale",
			"Precursor Neutral Loss Ion Min Intensity Scale", 0.3, true);

	private JRcpaDoubleField txtByNeutralLossMinIntensityScale = new JRcpaDoubleField(
			"byNeutralLossMinIntensityScale",
			"B/Y Neutral Loss Ion Min Intensity Scale", 0.1, true);

	private JRcpaDoubleField txtByMinIntensityScale = new JRcpaDoubleField(
			"byMinIntensityScale", "B/Y Ion Min Intensity Scale", 0.05, true);

	private JRcpaCheckBox cbShowScore = new JRcpaCheckBox("ShowScore",
			"Display Expect Value?", 1);

	private JRcpaCheckBox drawRemoveNeutralLoss = new JRcpaCheckBox(
			"drawRemoveNeutralLoss", "Draw Remove Precursor NeutralLoss Image",
			1);

	private JRcpaComboBox<ImageType> drawImageTypes = new JRcpaComboBox<ImageType>(
			"imageType", "Image Type", new ImageType[] { ImageType.tiff,
					ImageType.png, ImageType.jpg }, ImageType.tiff);

	public MascotSpectraMultipleFileImageBuilderUI() {
		super(Constants.getSQHTitle(title, version));

		this.addComponent(txtStaticModification);
		this.addComponent(txtDynamicModification);
		this.addComponent(txtMgfFile);
		this.addComponent(txtMascotResultFile);
		this.addComponent(txtTarget);
		this.addComponent(txtScale);

		cbPrecursorMonoisotopic.setSelected(true);
		this.addComponent(cbPrecursorMonoisotopic);
		this.addComponent(cbShowScore);

		this.addComponent(txtPrecursorTolerance);
		this.addComponent(txtPeakTolerance);
		this.addComponent(txtPrecursorNeutralLossMinIntensityScale);
		this.addComponent(txtByNeutralLossMinIntensityScale);
		this.addComponent(txtByMinIntensityScale);
		this.addComponent(drawRemoveNeutralLoss);
		this.addComponent(drawImageTypes);
	}

	public static void main(String[] args) {
		new MascotSpectraMultipleFileImageBuilderUI().showSelf();
	}

	@Override
	protected void doRealGo() {
		IdentifiedPeptideResultImageBuilder builder = new IdentifiedPeptideResultImageBuilder(
				txtScale.getValue(), txtPeakTolerance.getValue(),
				txtPrecursorNeutralLossMinIntensityScale.getValue(),
				txtByNeutralLossMinIntensityScale.getValue(),
				txtByMinIntensityScale.getValue(), drawRemoveNeutralLoss
						.isSelected(), drawImageTypes.getSelectedItem());

		String mgfDir = txtMgfFile.getFilename();
		String mascotResultFile = txtMascotResultFile.getFilename();

		String[] scores;
		if (cbShowScore.isSelected()) {
			scores = new String[] { "Score", "ExpectValue" };
		} else {
			scores = new String[] {"Score"};
		}

		double precursorTolerance = txtPrecursorTolerance.getValue();

		IonType[] ionTypes = new IonType[] { IonType.B, IonType.B2, IonType.Y,
				IonType.Y2 };

		Map<Character, Double> staticModification = txtStaticModification
				.getModificationMap(IsotopicType.Monoisotopic);

		Map<Character, Double> dynamicModification = txtDynamicModification
				.getModificationMap(IsotopicType.Monoisotopic);

		try {
			List<SlimIdentifiedPeptide> peptides = new SlimIdentifiedPeptideReader(
					scores).read(mascotResultFile);

			MascotSpectraImageBuilderUI.checkSequenceForAcetylProteinNtermModification(dynamicModification,
					peptides);

			Map<String, Map<Integer, SlimIdentifiedPeptide>> expPepMap = new HashMap<String, Map<Integer, SlimIdentifiedPeptide>>();
			for (SlimIdentifiedPeptide pep : peptides) {
				String exp = pep.getFileName().getExperiment();
				if (!expPepMap.containsKey(exp)) {
					expPepMap.put(exp,
							new HashMap<Integer, SlimIdentifiedPeptide>());
				}
				expPepMap.get(exp).put(pep.getFileName().getFirstScan(), pep);
			}

			for (String key : expPepMap.keySet()) {
				String mgfFile = mgfDir + "/" + key + ".mgf";
				if (!new File(mgfFile).exists()) {
					mgfFile = mgfDir + "/" + key + ".raw.mgf";
					if (!new File(mgfFile).exists()) {
						throw new FileNotFoundException(mgfFile);
					}
				}

				Map<Integer, SlimIdentifiedPeptide> pepMap = expPepMap.get(key);

				Map<Integer, PeakList<Peak>> mgfScanPklMap = new HashMap<Integer, PeakList<Peak>>();

				BufferedReader br = new BufferedReader(new FileReader(mgfFile));
				try {
					MascotGenericFormatIterator mgfi = new MascotGenericFormatIterator(
							br);
					while (mgfi.hasNext()) {
						PeakList<Peak> pkl = mgfi.next();
						if (pepMap.containsKey(pkl.getFirstScan())) {
							mgfScanPklMap.put(pkl.getFirstScan(), pkl);
						}
					}
				} finally {
					br.close();
				}

				for (Integer scan : pepMap.keySet()) {
					SlimIdentifiedPeptide peptide = pepMap.get(scan);

					String experimental = peptide.getFileName().getExperiment();

					if (!mgfScanPklMap.containsKey(scan)) {
						System.out.println("Cannot find "
								+ peptide.getFileName().getFirstScan() + " of "
								+ experimental + " in " + mgfFile);
						continue;
					}

					PeakList<Peak> pkl = mgfScanPklMap.get(scan);
					pkl.setCharge(peptide.getCharge());

					SequestPeptideResult spr = new SequestPeptideResult(peptide
							.getSequence(), ionTypes);

					spr.getScoreMap().putAll(peptide.getScoreMap());

					spr.initTheoreticalPeaks(cbPrecursorMonoisotopic
							.isSelected(), true, staticModification,
							dynamicModification);

					double theoreticalMz = (spr.getPrecursorMass() + Atom.H
							.getMono_isotopic().getMass()
							* (peptide.getCharge() - 1))
							/ peptide.getCharge();
					if (Math.abs(pkl.getPrecursorMZ() - theoreticalMz) > precursorTolerance) {
						throw new AccessException(
								peptide.getFileName().getLongFilename()
										+ ","
										+ "Precursor m/z are not matched. Check modification setting please.\nReal precursor m/z = "
										+ pkl.getPrecursorMZ()
										+ " ; calculated precursor m/z = "
										+ theoreticalMz);
					}

					PeakList<MatchedPeak> mpkl = getMatchedPkl(pkl);
					mpkl.setCharge(peptide.getCharge());
					spr.setExperimentalPeakList(mpkl);

					String resultFile = txtTarget.getFilename() + "/"
							+ pkl.getExperimental() + "." + pkl.getFirstScan()
							+ "." + pkl.getLastScan() + "."
							+ peptide.getCharge() + "." + drawImageTypes.getSelectedItem().toString();

					builder.drawImage(resultFile, spr);
				}
			}

			JOptionPane.showMessageDialog(this, "Succeed", "Congradulation",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private PeakList<MatchedPeak> getMatchedPkl(PeakList<Peak> pkl) {
		PeakList<MatchedPeak> result = new PeakList<MatchedPeak>();
		for (Peak peak : pkl.getPeaks()) {
			result.getPeaks().add(
					new MatchedPeak(peak.getMz(), peak.getIntensity(), peak
							.getCharge()));
		}

		result.setFirstScan(pkl.getFirstScan());
		result.setLastScan(pkl.getLastScan());
		result.setCharge(pkl.getCharge());
		result.setExperimental(pkl.getExperimental());
		result.setPrecursorMZ(pkl.getPrecursorMZ());
		result.setIntensity(pkl.getIntensity());
		return result;
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
			MascotSpectraMultipleFileImageBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}
}
