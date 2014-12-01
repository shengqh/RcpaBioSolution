package cn.ac.rcpa.bio.tools.modification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideN15Filter;
import cn.ac.rcpa.bio.tools.filter.IdentifiedResultFilter;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.filter.NotFilter;

public class IdentifiedResultN15Filter implements IFileProcessor {
	private IFilter<IIdentifiedPeptide> pepFilter;

	public IdentifiedResultN15Filter(String sequestParamFile)
			throws FileNotFoundException, IOException {
		pepFilter = new IdentifiedPeptideN15Filter(sequestParamFile);
	}

	public static void main(String[] args) throws Exception {
		final String noredundantFile = "F:\\Science\\Data\\caoxj\\N14N15\\62A12228_414.noredundant";
		final String sequestParamFile = "F:\\Science\\Data\\caoxj\\N14N15\\sequest.params";

		new IdentifiedResultN15Filter(sequestParamFile).process(noredundantFile);
	}

	public List<String> process(String noredundantFile) throws Exception {
		List<String> result = new ArrayList<String>();
		
		result.add(IdentifiedResultFilter.processByPeptideFilter(noredundantFile,
				pepFilter));
		
		result.add(IdentifiedResultFilter.processByPeptideFilter(noredundantFile,
				new NotFilter<IIdentifiedPeptide>(pepFilter)));
		
		return result;
	}
}
