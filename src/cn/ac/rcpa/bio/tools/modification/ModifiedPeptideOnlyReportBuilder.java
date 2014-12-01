package cn.ac.rcpa.bio.tools.modification;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.biojava.bio.BioException;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.proteomics.FollowCandidatePeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideHitFilterByPeptideFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideModificationFilter;
import cn.ac.rcpa.bio.proteomics.modification.ModificationInfo;
import cn.ac.rcpa.bio.proteomics.modification.SequenceModificationSitePair;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.bio.tools.report.AbstractExcelReportBuilder;
import cn.ac.rcpa.bio.utils.SequenceUtils;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.POIExcelUtils;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ModifiedPeptideOnlyReportBuilder extends AbstractExcelReportBuilder {
	public static final String version = "1.0.0";

	public BuildSummaryPeptideHit getHighestXCorrPeptideHit(
			List<BuildSummaryPeptideHit> phitlist) {
		BuildSummaryPeptideHit result = null;
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
		return result;
	}

	class ModifiedPeptideMap extends
			HashMap<ModificationInfo, List<BuildSummaryPeptideHit>> {
		public ModifiedPeptideMap(List<BuildSummaryPeptideHit> pephits) {
			for (BuildSummaryPeptideHit hit : pephits) {
				ModificationInfo key = new ModificationInfo(modifiedAminoacids, hit);

				if (!this.containsKey(key)) {
					this.put(key, new ArrayList<BuildSummaryPeptideHit>());
				}
				this.get(key).add(hit);
			}
		}

		public List<ModificationInfo> getModificationInfos() {
			List<ModificationInfo> result = new ArrayList<ModificationInfo>(this
					.keySet());
			Collections.sort(result);
			return result;
		}
	}

	private String modifiedAminoacids;

	private Map<String, String> proteinNameDescriptionMap;

	private IFilter<IIdentifiedPeptideHit> filter;

	public ModifiedPeptideOnlyReportBuilder(String url, String project,
			SequenceDatabaseType dbType, String modifiedAminoacids, String proteinFastaFile)
			throws NoSuchElementException, IOException, BioException {
		super(url, project, dbType);

		this.modifiedAminoacids = modifiedAminoacids;
		this.filter = new IdentifiedPeptideHitFilterByPeptideFilter(
				new IdentifiedPeptideModificationFilter(modifiedAminoacids, 0, false));

		this.proteinNameDescriptionMap = SequenceUtils
				.readProteinNameDescriptionMap(proteinFastaFile, dbType);
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader()
				.read(originFile);
		List<BuildSummaryPeptideHit> modifiedPeptides = PeptideUtils.getSubset(
				pephits, filter);

		ModifiedPeptideMap pepMap = new ModifiedPeptideMap(modifiedPeptides);

		initExcel("ModifiedPeptides");

		int rowindex = 0;
		HSSFRow row;

		row = sheet.createRow((short) (rowindex++));

		int colindex = 0;
		POIExcelUtils.createCell(row, colindex++, normalStyle,
				"Phosphopeptide Sequence");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "ModifiedSite");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "PositiveSite");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "AmbigiousSite");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Note");
		POIExcelUtils.createCell(row, colindex++, normalStyle,
				"Diff_Modified_Candidate");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Access Number");
		POIExcelUtils.createCell(row, colindex++, normalStyle, "Reference");

		List<ModificationInfo> infos = pepMap.getModificationInfos();
		for (ModificationInfo mi : infos) {
			List<BuildSummaryPeptideHit> hits = pepMap.get(mi);

			List<String> proteinNames = hits.get(0).getPeptide(0).getProteinNames();

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

			BuildSummaryPeptideHit hit = getHighestXCorrPeptideHit(hits);
			String seq = hit.getPeptide(0).getSequence();
			String curUrl = getUrl() + "/showdta.do?project=" + getProject()
					+ "&outfile=" + hit.getPeakListInfo().getLongFilename() + "out"
					+ "&peptide=" + URLEncoder.encode(seq, "UTF-8");
			POIExcelUtils.createHyperLinkCell(row, colindex++, linkStyle, curUrl, hit
					.getPeptide(0).getSequence());

			SequenceModificationSitePair pair = mi.asPair();
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

			if (hit.getFollowCandidates().size() > 0) {
				List<String> candidates = new ArrayList<String>();
				for (FollowCandidatePeptide candidate : hit.getFollowCandidates()) {
					candidates.add(candidate.getSequence());
				}
				POIExcelUtils.createCell(row, colindex++, normalWrapStyle, StringUtils
						.join(candidates.toArray(new String[0]), "\n"));
			} else {
				POIExcelUtils.createCell(row, colindex++, normalWrapStyle, "");
			}

			POIExcelUtils.createCell(row, colindex++, normalWrapStyle, StringUtils
					.join(proteinAccessNumbers.toArray(new String[0]), "\n"));
			POIExcelUtils.createCell(row, colindex++, normalWrapStyle, StringUtils
					.join(proteinDescriptions.toArray(new String[0]), "\n"));
		}

		String resultFile = RcpaFileUtils.changeExtension(originFile, ".Modified_"
				+ modifiedAminoacids + "_Only.peptides.report");
		FileOutputStream fileOut = new FileOutputStream(resultFile);
		try {
			wb.write(fileOut);
		} finally {
			fileOut.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}

	public static void main(String[] args) throws Exception {
		// new ModifiedPeptideReportBuilder("http://localhost:8080/msms", "JDAI2",
		// DatabaseType.IPI, "STY", "JWH_([^_]+)_", "JWH_[^_]+_(\\d+)_",
		// "D:\\Database\\ipi.MOUSE.v3.00.1.fasta")
		// .process("F:\\sqh\\Project\\phospho\\jwh\\summary\\2.0_2.5_3.3_0.1\\SAX_0906_Total_2.0_2.5_3.3_0.1.peptides");
	}

}
