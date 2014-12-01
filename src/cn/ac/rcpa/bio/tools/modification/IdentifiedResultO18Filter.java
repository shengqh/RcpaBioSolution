package cn.ac.rcpa.bio.tools.modification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideO18Filter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedPeptideO18ValidFilter;
import cn.ac.rcpa.bio.sequest.SequestParseException;
import cn.ac.rcpa.bio.tools.filter.IdentifiedResultFilter;
import cn.ac.rcpa.filter.AndFilter;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.filter.NotFilter;

public class IdentifiedResultO18Filter implements IFileProcessor {
	public static String version = "1.0.1";

	private static IFilter<IIdentifiedPeptide> O18ValidFilter = new IdentifiedPeptideO18ValidFilter();

	private static IFilter<IIdentifiedPeptide> O18Filter = new IdentifiedPeptideO18Filter();

	private static IdentifiedResultO18Filter instance;

	public static IdentifiedResultO18Filter getInstance() {
		if (instance == null) {
			instance = new IdentifiedResultO18Filter();
		}
		return instance;
	}

	private IdentifiedResultO18Filter() {
	}

	public static void main(String[] args) throws Exception {
		final String file = "F:\\Science\\Data\\rxli\\BSA_5P_MIX_PromageEnzyme_summary\\BSA_5P_MIX_PromageEnzyme.noredundant";
		getInstance().process(file);
	}

	public List<String> process(String file) throws IOException,
			SequestParseException {
		List<String> result = new ArrayList<String>();

		result.add(processUnvalidPeptides(file));

		result.add(processModifiedO18Peptides(file));

		result.add(processUnmodifiedO18Peptides(file));

		return result;
	}

	private String processUnmodifiedO18Peptides(String file) throws IOException,
			SequestParseException {
		final ArrayList<IFilter<IIdentifiedPeptide>> unmodifiedO18FilterArray = new ArrayList<IFilter<IIdentifiedPeptide>>();

		unmodifiedO18FilterArray.add(O18ValidFilter);
		unmodifiedO18FilterArray.add(new NotFilter<IIdentifiedPeptide>(O18Filter));

		final IFilter<IIdentifiedPeptide> ModifiedO18Filter = new AndFilter<IIdentifiedPeptide>(
				unmodifiedO18FilterArray);

		return IdentifiedResultFilter.processByPeptideFilter(file,
				ModifiedO18Filter);
	}

	private String processModifiedO18Peptides(String file) throws IOException,
			SequestParseException {
		final ArrayList<IFilter<IIdentifiedPeptide>> modifiedO18FilterArray = new ArrayList<IFilter<IIdentifiedPeptide>>();

		modifiedO18FilterArray.add(O18ValidFilter);
		modifiedO18FilterArray.add(O18Filter);

		final IFilter<IIdentifiedPeptide> ModifiedO18Filter = new AndFilter<IIdentifiedPeptide>(
				modifiedO18FilterArray);

		return IdentifiedResultFilter.processByPeptideFilter(file,
				ModifiedO18Filter);
	}

	private String processUnvalidPeptides(String file) throws IOException,
			SequestParseException {
		return IdentifiedResultFilter.processByPeptideFilter(file,
				new NotFilter<IIdentifiedPeptide>(O18ValidFilter));
	}
}
