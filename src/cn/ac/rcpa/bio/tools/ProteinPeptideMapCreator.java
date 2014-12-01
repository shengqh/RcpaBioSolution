package cn.ac.rcpa.bio.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.DistributionResultMap;
import cn.ac.rcpa.bio.proteomics.StringPeptideMap;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultExperimentalReader;

public class ProteinPeptideMapCreator implements IFileProcessor {
	private SequenceDatabaseType dbType;

	public static final String version = "1.0.0";

	public ProteinPeptideMapCreator(SequenceDatabaseType dbType) {
		this.dbType = dbType;
	}

	public List<String> process(String originFile) throws Exception {
		StringPeptideMap proPepMap = BuildSummaryResultExperimentalReader
				.getInstance().getExperimentalDetailMap(originFile);

		final String proMap = printGroupProteinMap(originFile, proPepMap);
		final String pepMap = printGroupPeptideMap(originFile, proPepMap);

		return Arrays.asList(new String[] { proMap, pepMap });
	}

	private String printGroupPeptideMap(String originFile,
			StringPeptideMap proPepMap) throws IOException {
		final String result = originFile + ".gpepmap";
		PrintWriter pwPep = new PrintWriter(new FileWriter(result));
		try {
			pwPep.println("Peptide\tGroup");
			int groupindex = 0;
			for (String protein : proPepMap.keySet()) {
				groupindex++;
				final DistributionResultMap pepMap = proPepMap.get(protein);
				for (String peptide : pepMap.keySet()) {
					pwPep.println(peptide + "\t" + groupindex);
				}
			}
		} finally {
			pwPep.close();
		}

		return result;
	}

	private String printGroupProteinMap(String originFile,
			StringPeptideMap proPepMap) throws IOException {
		final String result = originFile + ".gpromap";
		final IAccessNumberParser acParser = AccessNumberParserFactory
				.getParser(dbType);
		PrintWriter pw = new PrintWriter(new FileWriter(result));
		try {
			pw.println("Protein\tGroup");
			int groupindex = 0;
			for (String protein : proPepMap.keySet()) {
				groupindex++;
				String[] proteins = protein.split(" ! ");
				for (int i = 0; i < proteins.length; i++) {
					pw.println(acParser.getValue(proteins[i]) + "\t" + groupindex);
				}
			}
		} finally {
			pw.close();
		}
		return result;
	}
}
