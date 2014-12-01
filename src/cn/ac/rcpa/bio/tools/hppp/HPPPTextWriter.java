package cn.ac.rcpa.bio.tools.hppp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.ac.rcpa.bio.proteomics.IIdentifiedProtein;
import cn.ac.rcpa.bio.proteomics.IIdentifiedResult;
import cn.ac.rcpa.bio.proteomics.io.IIdentifiedResultWriter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;

public class HPPPTextWriter implements IIdentifiedResultWriter {
	private static HPPPTextWriter instance = null;

	private HPPPTextWriter() {
	}

	public static HPPPTextWriter getInstance() {
		if (instance == null) {
			instance = new HPPPTextWriter();
		}
		return instance;
	}

	/**
	 * write
	 * 
	 * @param filename
	 *          String
	 * @param BuildSummaryResult
	 *          BuildSummaryResult
	 */
	public void write(String filename, IIdentifiedResult buildSummaryResult)
			throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(filename));
		pw.println("\tReference");
		pw.println("\tGender\tHigh/Low\tTime\tSequence\tXC\tDeltaCn\tSp");
		for (int i = 0; i < buildSummaryResult.getProteinGroupCount(); i++) {
			IIdentifiedProtein prohit = buildSummaryResult.getProteinGroup(i)
					.getProtein(0);
			pw.println("$" + (i + 1) + "-1\t" + prohit.getReference());
			for (int j = 0; j < prohit.getPeptideCount(); j++) {
				BuildSummaryPeptide pephit = (BuildSummaryPeptide) prohit.getPeptide(j);
				pw.print("\t");
				if (pephit.getPeakListInfo().getExperiment().indexOf("_SM") != -1) {
					pw.print("Male");
				} else {
					pw.print("Female");
				}
				pw.print("\t");
				if (pephit.getPeakListInfo().getExperiment().indexOf("Flow_through") != -1) {
					pw.print("L");
				} else {
					pw.print("H");
				}
				pw.print("\t");
				if (pephit.getPeakListInfo().getExperiment().indexOf("_2") != -1) {
					pw.print("2");
				} else {
					pw.print("1");
				}
				pw.println("\t" + pephit.getSequence() + "\t" + pephit.getXcorr()
						+ "\t" + pephit.getDeltacn() + "\t" + pephit.getSpRank());
			}
		}
		pw.close();
	}

	/**
	 * getDefaultExtension
	 * 
	 * @return String
	 */
	public String getDefaultExtension() {
		return ".hppp";
	}

	/**
	 * write
	 * 
	 * @param writer
	 *          Writer
	 * @param identifiedResult
	 *          IIdentifiedResult
	 */
	public void write(PrintWriter pw, IIdentifiedResult buildSummaryResult)
			throws IOException {
		pw.println("\tReference");
		pw.println("\tGender\tHigh/Low\tTime\tSequence\tXC\tDeltaCn\tSp");
		for (int i = 0; i < buildSummaryResult.getProteinGroupCount(); i++) {
			IIdentifiedProtein prohit = buildSummaryResult.getProteinGroup(i)
					.getProtein(0);
			pw.println("$" + (i + 1) + "-1\t" + prohit.getReference());
			for (int j = 0; j < prohit.getPeptideCount(); j++) {
				BuildSummaryPeptide pephit = (BuildSummaryPeptide) prohit.getPeptide(j);
				pw.print("\t");
				if (pephit.getPeakListInfo().getExperiment().indexOf("_SM") != -1) {
					pw.print("Male");
				} else {
					pw.print("Female");
				}
				pw.print("\t");
				if (pephit.getPeakListInfo().getExperiment().indexOf("Flow_through") != -1) {
					pw.print("L");
				} else {
					pw.print("H");
				}
				pw.print("\t");
				if (pephit.getPeakListInfo().getExperiment().indexOf("_2") != -1) {
					pw.print("2");
				} else {
					pw.print("1");
				}
				pw.print("\t" + pephit.getSequence() + "\t" + pephit.getXcorr() + "\t"
						+ pephit.getDeltacn() + "\t" + pephit.getSpRank());
				pw.println();
			}
		}
	}

}
