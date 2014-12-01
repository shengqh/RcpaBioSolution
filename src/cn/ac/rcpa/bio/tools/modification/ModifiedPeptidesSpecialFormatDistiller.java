package cn.ac.rcpa.bio.tools.modification;

import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptideHit;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideHitFilterByPeptideFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideModificationFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryModifiedPeptideHitWriter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitReader;
import cn.ac.rcpa.bio.proteomics.utils.PeptideUtils;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class ModifiedPeptidesSpecialFormatDistiller implements
		IFileProcessor {
	private IFilter<IIdentifiedPeptideHit> filter;

	public static final String version = "1.0.1";

	private String modifiedAminoacids;

	public ModifiedPeptidesSpecialFormatDistiller(String modifiedAminoacids) {
		this.modifiedAminoacids = modifiedAminoacids;
		this.filter = new IdentifiedPeptideHitFilterByPeptideFilter(
				new IdentifiedPeptideModificationFilter(modifiedAminoacids, 0, false));
	}

	public List<String> process(String originFile) throws Exception {
		List<BuildSummaryPeptideHit> pephits = new BuildSummaryPeptideHitReader()
				.read(originFile);

		BuildSummaryModifiedPeptideHitWriter writer = new BuildSummaryModifiedPeptideHitWriter(
				modifiedAminoacids);

		List<BuildSummaryPeptideHit> modifiedPeptides = PeptideUtils.getSubset(
				pephits, filter);

		String resultFile = RcpaFileUtils.changeExtension(originFile, ".Modified_"
				+ modifiedAminoacids + ".peptides");
		writer.write(resultFile, modifiedPeptides);

		return Arrays.asList(new String[] { resultFile });
	}

	public static void main(String[] args) throws Exception {
		new ModifiedPeptidesSpecialFormatDistiller("STY")
				.process("data/TestPhospho2.peptides");
	}

}
