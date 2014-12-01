package cn.ac.rcpa.bio.tools.modification;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.StringPatternParser;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.proteomics.modification.ModificationInfo;
import cn.ac.rcpa.bio.proteomics.modification.SequenceModificationSitePair;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.bio.tools.report.AbstractExcelReportBuilder;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.utils.POIExcelUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ModifiedPeptidePairFractionAndPhValueReportBuilder extends
		AbstractExcelReportBuilder {
	public static final String version = "1.0.2";

	class FractionMap extends HashMap<String, List<BuildSummaryPeptideHit>> {
		public FractionMap(List<BuildSummaryPeptideHit> pephits) {
			for (BuildSummaryPeptideHit hit : pephits) {
				String fraction = getFraction(hit.getPeakListInfo().getExperiment());
				if (!this.containsKey(fraction)) {
					this.put(fraction, new ArrayList<BuildSummaryPeptideHit>());
				}
				this.get(fraction).add(hit);
			}
		}

		public String getPhValues(String fraction) {
			if (!this.containsKey(fraction)) {
				return "";
			}

			Set<Double> phValues = new HashSet<Double>();
			List<BuildSummaryPeptideHit> pephits = this.get(fraction);
			for (BuildSummaryPeptideHit hit : pephits) {
				phValues.add(getPhValue(hit.getPeakListInfo().getExperiment()));
			}

			List<Double> phValueList = new ArrayList<Double>(phValues);
			Collections.sort(phValueList);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < phValueList.size(); i++) {
				if (i != 0) {
					sb.append("\n");
				}
				if (phValueList.get(i) == phValueList.get(i).intValue()) {
					sb.append(phValueList.get(i).intValue());
				} else {
					sb.append(phValueList.get(i));
				}
			}
			return sb.toString();
		}

		public List<String> getProteinNames() {
			return this.get(this.keySet().iterator().next()).get(0).getPeptide(0)
					.getProteinNames();
		}

		public int getPeptideHitCount() {
			int result = 0;
			for (List<BuildSummaryPeptideHit> hits : this.values()) {
				result += hits.size();
			}
			return result;
		}
	}

	class ModifiedPeptideMap extends HashMap<ModificationInfo, FractionMap> {
		public ModifiedPeptideMap(List<BuildSummaryPeptideHit> pephits) {
			HashMap<ModificationInfo, List<BuildSummaryPeptideHit>> map = new HashMap<ModificationInfo, List<BuildSummaryPeptideHit>>();
			for (BuildSummaryPeptideHit hit : pephits) {
				ModificationInfo key = new ModificationInfo(modifiedAminoacids, hit);

				if (!map.containsKey(key)) {
					map.put(key, new ArrayList<BuildSummaryPeptideHit>());
				}
				map.get(key).add(hit);
			}

			for (ModificationInfo key : map.keySet()) {
				this.put(key, new FractionMap(map.get(key)));
			}
		}

		public List<ModificationInfo> getModificationInfos() {
			List<ModificationInfo> result = new ArrayList<ModificationInfo>(this
					.keySet());
			Collections.sort(result, ModificationInfo.MODIFIED_COUNT_ORDER);
			return result;
		}

		public boolean isMultipleState() {
			int modifiedCount = -1;
			for (ModificationInfo mi : this.keySet()) {
				if (-1 == modifiedCount) {
					modifiedCount = mi.asPair().getModifiedCount();
				} else if (modifiedCount != mi.asPair().getModifiedCount()) {
					return true;
				}
			}

			return false;
		}

		public List<String> getProteinNames() {
			return this.get(this.keySet().iterator().next()).getProteinNames();
		}

		public BuildSummaryPeptideHit getHighestXCorrPeptideHit() {
			BuildSummaryPeptideHit result = null;
			for (FractionMap fmap : this.values()) {
				for (List<BuildSummaryPeptideHit> phitlist : fmap.values()) {
					for (BuildSummaryPeptideHit phit : phitlist) {
						if (result == null) {
							result = phit;
						} else if (result.getPeptide(0).getCharge() != 2
								&& phit.getPeptide(0).getCharge() == 2) {
							result = phit;
						} else if (result.getPeptide(0).getXcorr() < phit.getPeptide(0)
								.getXcorr()) {
							result = phit;
						}
					}
				}
			}
			return result;
		}
	}

	class PeptideMap extends HashMap<String, ModifiedPeptideMap> {
		public PeptideMap(List<BuildSummaryPeptideHit> pephits) {
			HashMap<String, List<BuildSummaryPeptideHit>> map = new HashMap<String, List<BuildSummaryPeptideHit>>();
			for (BuildSummaryPeptideHit hit : pephits) {
				String pureSeq = PeptideUtils.getPurePeptideSequence(hit.getPeptide(0)
						.getSequence());
				if (!map.containsKey(pureSeq)) {
					map.put(pureSeq, new ArrayList<BuildSummaryPeptideHit>());
				}
				map.get(pureSeq).add(hit);
			}

			for (String pureSeq : map.keySet()) {
				this.put(pureSeq, new ModifiedPeptideMap(map.get(pureSeq)));
			}
		}

		public List<String> getFractions() {
			Set<String> fractionSet = new HashSet<String>();
			for (ModifiedPeptideMap mMap : this.values()) {
				for (FractionMap fMap : mMap.values()) {
					fractionSet.addAll(fMap.keySet());
				}
			}

			List<String> result = new ArrayList<String>(fractionSet);
			Collections.sort(result);
			return result;
		}

		public List<String> getPureSequences() {
			ArrayList<String> result = new ArrayList<String>(this.keySet());
			Collections.sort(result);
			return result;
		}
	}

	private String modifiedAminoacids;

	private Pattern phValuePattern;

	private Map<String, String> proteinNameDescriptionMap;

	public ModifiedPeptidePairFractionAndPhValueReportBuilder(String url,
			String project, SequenceDatabaseType dbType, String modifiedAminoacids,
			String fractionPatternStr, final String phValuePatternStr,
			String proteinFastaFile) throws NoSuchElementException, IOException,
			BioException {
		super(url, project, dbType);

		setFractionParser(new StringPatternParser("Fraction", "Fraction",
				fractionPatternStr));

		this.modifiedAminoacids = modifiedAminoacids;
		this.phValuePattern = Pattern.compile(phValuePatternStr);

		readProteinNameDescriptionMap(proteinFastaFile);
	}

	private void readProteinNameDescriptionMap(String proteinFastaFile)
			throws IOException, NoSuchElementException, BioException {
		proteinNameDescriptionMap = new HashMap<String, String>();
		SequenceIterator seqi = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(proteinFastaFile)));

		while (seqi.hasNext()) {
			Sequence seq = seqi.nextSequence();
			if (seq.getAnnotation().containsProperty("description")) {
				String description = (String) seq.getAnnotation().getProperty(
						"description");
				proteinNameDescriptionMap.put(getAccessNumber(seq.getName()),
						description.trim());
			} else {
				proteinNameDescriptionMap.put(getAccessNumber(seq.getName()), "");
			}
		}
	}

	private Double getPhValue(String experimental) {
		Matcher matcher = phValuePattern.matcher(experimental);
		if (!matcher.find()) {
			throw new IllegalArgumentException("Cannot get ph value from "
					+ experimental + " based on pattern " + phValuePattern.pattern());
		}

		double result = Double.parseDouble(matcher.group(1));
		if (result > 15) {
			result /= 10;
		}

		return result;
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader()
				.read(originFile);

		PeptideMap pepMap = new PeptideMap(pephits);
		List<String> pureSeqs = pepMap.getPureSequences();
		List<String> fractions = pepMap.getFractions();

		initExcel("ModifiedPeptides");

		int rowindex = 0;
		HSSFRow row;

		row = sheet.createRow((short) (rowindex++));

		int colindex = 0;
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Index");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Sequence");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Specific Peptide");
		for (String fraction : fractions) {
			POIExcelUtils.createCell(row, colindex++, normalStyle, fraction);
		}
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Hits");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "ModifiedSite");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "PositiveSite");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "AmbigiousSite");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Note");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Access Number");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Reference");

		int index = 0;
		for (String pureSeq : pureSeqs) {
			ModifiedPeptideMap modMap = pepMap.get(pureSeq);
			if (modMap.size() == 1 || !modMap.isMultipleState()) {
				continue;
			}

			index++;

			List<String> proteinNames = modMap.getProteinNames();

			List<String> proteinAccessNumbers = new ArrayList<String>();
			List<String> proteinDescriptions = new ArrayList<String>();
			for (int pIndex = 0; pIndex < proteinNames.size(); pIndex++) {
				String accNumber = getAccessNumber(proteinNames.get(pIndex));
				String description = proteinNameDescriptionMap.get(accNumber);
				if (description == null) {
					throw new IllegalArgumentException("Cannot find protein " + accNumber
							+ " in database!");
				}
				proteinAccessNumbers.add(accNumber);
				proteinDescriptions.add(description);
			}

			row = sheet.createRow((short) (rowindex++));
			colindex = 0;
			POIExcelUtils.createCell(row, colindex++, normalStyle, index);
			POIExcelUtils.createCell(row, colindex++, normalStyle, pureSeq);
			colindex += 6 + fractions.size();
			POIExcelUtils.createCell(row, colindex++, normalWrapStyle, StringUtils
					.join(proteinAccessNumbers.toArray(new String[0]), "\n"));
			POIExcelUtils.createCell(row, colindex++, normalWrapStyle, StringUtils
					.join(proteinDescriptions.toArray(new String[0]), "\n"));

			List<ModificationInfo> miList = modMap.getModificationInfos();
			for (ModificationInfo mi : miList) {
				row = sheet.createRow((short) (rowindex++));
				colindex = 2;

				BuildSummaryPeptideHit hit = modMap.getHighestXCorrPeptideHit();
				String seq = hit.getPeptide(0).getSequence();
				String curUrl = getUrl() + "/showdta.do?project=" + getProject()
						+ "&outfile=" + hit.getPeakListInfo().getLongFilename() + "out"
						+ "&peptide=" + URLEncoder.encode(seq, "UTF-8");
				POIExcelUtils.createHyperLinkCell(row, colindex++, linkStyle, curUrl,
						mi.getModifiedFullSequence());

				FractionMap fmap = modMap.get(mi);
				for (String fraction : fractions) {
					POIExcelUtils.createCell(row, colindex++, normalWrapStyle, fmap
							.getPhValues(fraction));
				}
				POIExcelUtils.createCell(row, colindex++, normalStyle, fmap
						.getPeptideHitCount());

				SequenceModificationSitePair pair = mi.asPair();
				if (pair.getModifiedCount() != 0) {
					POIExcelUtils.createCell(row, colindex++, normalStyle, pair
							.getModifiedCount());
					POIExcelUtils.createCell(row, colindex++, normalStyle, pair
							.getTrueModifiedCount());
					POIExcelUtils.createCell(row, colindex++, normalStyle, pair
							.getAmbiguousModifiedCount());
					if (pair.getAmbiguousModifiedCount() == 0) {
						POIExcelUtils.createCell(row, colindex++, normalStyle, "Unique");
					} else {
						POIExcelUtils.createCell(row, colindex++, normalStyle, "Ambigious");
					}
				}
			}
		}

		String resultFile = RcpaFileUtils.changeExtension(originFile, ".Modified_"
				+ modifiedAminoacids + ".peptides.report");
		FileOutputStream fileOut = new FileOutputStream(resultFile);
		try {
			wb.write(fileOut);
		} finally {
			fileOut.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}

	public static void main(String[] args) throws Exception {
		new ModifiedPeptidePairFractionAndPhValueReportBuilder(
				"http://localhost:8080/msms", "JDAI2", SequenceDatabaseType.IPI, "STY",
				"JWH_([^_]+)_", "JWH_[^_]+_(\\d+)_",
				"D:\\Database\\ipi.MOUSE.v3.00.1.fasta")
				.process("F:\\sqh\\Project\\phospho\\jwh\\summary\\2.0_2.5_3.3_0.1\\SAX_0906_Total_2.0_2.5_3.3_0.1.peptides");

		/*
		 * new ModifiedPeptideFractionAndPhValueReportBuilder(
		 * "http://localhost:8080/msms", "JDAI2", DatabaseType.IPI, "STY", "(SAX)",
		 * "_(\\d+)$", "D:\\Database\\ipi.MOUSE.v3.04.fasta")
		 * .process("F:\\sqh\\Project\\phospho\\jdai\\Final_Data_Checked.peptides");
		 */
	}

}
