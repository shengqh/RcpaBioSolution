package cn.ac.rcpa.bio.tools.image;

import java.io.BufferedReader;
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

public class MascotSpectraImageBuilderUI extends AbstractUI {

	private static String title = "Mascot Spectra Image Builder";

	private static String version = "1.1.4";

	private static String MODIFICATION_CHAR = " *#@&^%$~1234567890";

	private JRcpaModificationTextField txtStaticModification = new JRcpaModificationTextField(
			"StaticModification",
			"Static Modification (as: C +57.02 STY -17.99)", "C +57.02", false);

	private JRcpaModificationTextField txtDynamicModification = new JRcpaModificationTextField(
			"DynamicModification",
			"Dynamic Modification (as: @ +79.99 # +15.99)", "", false);

	private JRcpaFileField txtMgfFile = new JRcpaFileField("MgfFile",
			new OpenFileArgument("Mascot Generic Format", new String[] { "mgf",
					"msm" }), true);

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

	public MascotSpectraImageBuilderUI() {
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
		new MascotSpectraImageBuilderUI().showSelf();
	}

	@Override
	protected void doRealGo() {
		IdentifiedPeptideResultImageBuilder builder = new IdentifiedPeptideResultImageBuilder(
				txtScale.getValue(), txtPeakTolerance.getValue(),
				txtPrecursorNeutralLossMinIntensityScale.getValue(),
				txtByNeutralLossMinIntensityScale.getValue(),
				txtByMinIntensityScale.getValue(), drawRemoveNeutralLoss
						.isSelected(), drawImageTypes.getSelectedItem());

		String mgfFilename = txtMgfFile.getFilename();
		String mascotResultFile = txtMascotResultFile.getFilename();

		double precursorTolerance = txtPrecursorTolerance.getValue();
		try {
			Map<String, PeakList<Peak>> mgfScanPklMap = new HashMap<String, PeakList<Peak>>();
			BufferedReader br = new BufferedReader(new FileReader(mgfFilename));
			try {
				MascotGenericFormatIterator mgfi = new MascotGenericFormatIterator(
						br);
				while (mgfi.hasNext()) {
					PeakList<Peak> pkl = mgfi.next();
					mgfScanPklMap.put(pkl.getExperimental() + "_"
							+ pkl.getFirstScan(), pkl);
				}
			} finally {
				br.close();
			}

			String[] scores;
			if (cbShowScore.isSelected()) {
				scores = new String[] { "Score", "ExpectValue" };
			} else {
				scores = new String[] { "Score" };
			}
			List<SlimIdentifiedPeptide> peptides = new SlimIdentifiedPeptideReader(
					scores).read(mascotResultFile);

			IonType[] ionTypes = new IonType[] { IonType.B, IonType.B2,
					IonType.Y, IonType.Y2 };

			Map<Character, Double> staticModification = txtStaticModification
					.getModificationMap(IsotopicType.Monoisotopic);
			Map<Character, Double> dynamicModification = txtDynamicModification
					.getModificationMap(IsotopicType.Monoisotopic);

			checkSequenceForAcetylProteinNtermModification(dynamicModification,
					peptides);

			for (int i = 0; i < peptides.size(); i++) {
				SlimIdentifiedPeptide peptide = peptides.get(i);

				String experimental = peptide.getFileName().getExperiment();
				String scan = experimental + "_"
						+ peptide.getFileName().getFirstScan();

				if (!mgfScanPklMap.containsKey(scan)) {
					System.out.println("Cannot find "
							+ peptide.getFileName().getFirstScan() + " of "
							+ experimental + " in " + mgfFilename);
					continue;
				}

				PeakList<Peak> pkl = mgfScanPklMap.get(scan);
				pkl.setCharge(peptide.getCharge());

				SequestPeptideResult spr = new SequestPeptideResult(peptide
						.getSequence(), ionTypes);

				spr.getScoreMap().putAll(peptide.getScoreMap());

				spr.initTheoreticalPeaks(cbPrecursorMonoisotopic.isSelected(),
						true, staticModification, dynamicModification);

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
						+ "." + pkl.getLastScan() + "." + peptide.getCharge()+ "."
						+ drawImageTypes.getSelectedItem().toString();

				builder.drawImage(resultFile, spr);
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
			MascotSpectraImageBuilderUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

	public static void checkSequenceForAcetylProteinNtermModification(
			Map<Character, Double> dynamicModification,
			List<SlimIdentifiedPeptide> peptides) {
		char acetylProteinNtermDynamicModificationChar = ' ';
		for (SlimIdentifiedPeptide pep : peptides) {
			if (pep.getModification() != null
					&& pep.getModification()
							.contains("Acetyl (Protein N-term)")) {
				String matchedSeq = pep.getSequence();
				int startIndex = 0;
				if (matchedSeq.charAt(1) == '.') {
					startIndex = 2;
				}

				if (Character.isLetter(matchedSeq.charAt(startIndex))) {
					if (acetylProteinNtermDynamicModificationChar == ' ') {
						// no n-terminal modification defined.
						for (int i = 1; i < MODIFICATION_CHAR.length(); i++) {
							if (!dynamicModification
									.containsKey(MODIFICATION_CHAR.charAt(i))) {
								acetylProteinNtermDynamicModificationChar = MODIFICATION_CHAR
										.charAt(i);
								dynamicModification
										.put(
												acetylProteinNtermDynamicModificationChar,
												42.010559);
								break;
							}
						}
					}

					String newSeq = matchedSeq.substring(0, startIndex)
							+ acetylProteinNtermDynamicModificationChar
							+ matchedSeq.substring(startIndex);
					pep.setSequence(newSeq);
				}
			}
		}
	}
}
