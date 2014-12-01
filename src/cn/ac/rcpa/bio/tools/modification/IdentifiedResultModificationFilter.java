package cn.ac.rcpa.bio.tools.modification;

import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideModificationFilter;
import cn.ac.rcpa.bio.tools.filter.IdentifiedPeptideHitFilter;
import cn.ac.rcpa.bio.tools.filter.IdentifiedResultFilter;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.filter.NotFilter;

public class IdentifiedResultModificationFilter implements IFileProcessor {
	public static String version = "1.0.1";

	private String modifiedAminoacids;

	public IdentifiedResultModificationFilter(String modifiedAminoacids) {
		this.modifiedAminoacids = modifiedAminoacids;
	}

	public static void main(String[] args) throws Exception {
		final String file = "F:\\Science\\Data\\zhouhu\\peptidefilter\\test.noredundant";

		new IdentifiedResultModificationFilter("STY").process(file);
	}

	public List<String> process(String originFile) throws Exception {
		final IFilter<IIdentifiedPeptide> pepFilter = new IdentifiedPeptideModificationFilter(
				modifiedAminoacids, 0, false);

		List<String> result = new ArrayList<String>();
		String modifiedFile = IdentifiedResultFilter.processByPeptideFilter(
				originFile, pepFilter);
		result.add(modifiedFile);
		result.add(IdentifiedResultFilter.processByPeptideFilter(originFile,
				new NotFilter<IIdentifiedPeptide>(pepFilter)));

		result.add(IdentifiedPeptideHitFilter.processByProteinFile(modifiedFile,
				new IdentifiedPeptideModificationFilter(modifiedAminoacids, 1, true)));
		result.add(IdentifiedPeptideHitFilter.processByProteinFile(modifiedFile,
				new IdentifiedPeptideModificationFilter(modifiedAminoacids, 2, true)));
		result.add(IdentifiedPeptideHitFilter.processByProteinFile(modifiedFile,
				new IdentifiedPeptideModificationFilter(modifiedAminoacids, 3, false)));

		return result;
	}
}
