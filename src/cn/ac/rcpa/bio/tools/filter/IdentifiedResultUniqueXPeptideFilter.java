package cn.ac.rcpa.bio.tools.filter;

import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultWriter;

public class IdentifiedResultUniqueXPeptideFilter implements IFileProcessor {
	public final static String version = "1.0.0";

	private int uniquePeptideCount;

	public IdentifiedResultUniqueXPeptideFilter(int uniquePeptideCount) {
		this.uniquePeptideCount = uniquePeptideCount;
	}

	public List<String> process(String originFile) throws Exception {
		BuildSummaryResult ir = new BuildSummaryResultReader().read(originFile);

		BuildSummaryResult larger = new BuildSummaryResult();
		BuildSummaryResult less = new BuildSummaryResult();
		for (int i = 0; i < ir.getProteinGroupCount(); i++) {
			if (ir.getProteinGroup(i).getProtein(0).getUniquePeptides().length < uniquePeptideCount) {
				less.addProteinGroup(ir.getProteinGroup(i));
			} else {
				larger.addProteinGroup(ir.getProteinGroup(i));
			}
		}

		String resultFile1 = originFile + ".unique" + uniquePeptideCount + "+";
		BuildSummaryResultWriter.getInstance().write(resultFile1, larger);
		String resultFile2 = originFile + ".unique" + uniquePeptideCount + "-";
		BuildSummaryResultWriter.getInstance().write(resultFile2, less);
		return Arrays.asList(new String[] { resultFile1, resultFile2 });
	}
}
