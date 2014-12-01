package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultFileType;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.classification.ClassificationFactory;
import cn.ac.rcpa.bio.proteomics.classification.IClassification;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptide;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryPeptideHit;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProteinGroup;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide;
import cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType;
import cn.ac.rcpa.bio.tools.distribution.option.types.FilterType;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class DistributionOptionUtils {
	public DistributionOptionUtils() {
	}

	public static DistributionOption createDistributionOption(
			ClassificationInfo classificationInfo, FilterByPeptide filterByPeptide) {
		DistributionOption result = new DistributionOption();
		result.setClassificationInfo(classificationInfo);
		result.setFilterByPeptide(filterByPeptide);

		return result;
	}

	public static ClassificationInfo createClassificationInfo(
			ClassificationType classificationType, String classificationPrinciple) {
		ClassificationInfo result = new ClassificationInfo();

		result.setClassificationType(classificationType);
		result.setClassificationPrinciple(classificationPrinciple);

		return result;
	}

	public static FilterByPeptide createFilterByPeptide(FilterType filterType,
			int from, int to, int step) {
		FilterByPeptide result = new FilterByPeptide();

		result.setFilterType(filterType);
		result.setFrom(from);
		result.setTo(to);
		result.setStep(step);

		return result;
	}

	public static Map<String, Double> getExperimentValues(
			final DistributionOption option) {
		Map<String, Double> result = new HashMap<String, Double>();

		ClassificationItem[] classifications = option.getClassificationSet()
				.getClassificationItem();

		for (int i = 0; i < classifications.length; i++) {
			if (classifications[i].hasExperimentValue()) {
				result.put(classifications[i].getClassifiedName(), classifications[i]
						.getExperimentValue());
			}
		}

		return result;
	}

	public static IClassification<IIdentifiedPeptide> getClassification(
			final DistributionOption option) {
		Map<String, String> classificationMap = new HashMap<String, String>();

		ClassificationItem[] classifications = option.getClassificationSet()
				.getClassificationItem();
		for (int i = 0; i < classifications.length; i++) {
			for (int j = 0; j < classifications[i].getExperimentNameCount(); j++) {
				classificationMap.put(classifications[i].getExperimentName(j),
						classifications[i].getClassifiedName());
			}
		}

		return ClassificationFactory.getIdentifiedPeptideMapClassification(option
				.getClassificationInfo().getClassificationPrinciple(),
				classificationMap);
	}

	public static String[] getClassifiedNames(final DistributionOption option) {
		ClassificationItem[] classifications = option.getClassificationSet()
				.getClassificationItem();

		String[] result = new String[classifications.length];

		for (int i = 0; i < classifications.length; i++) {
			result[i] = classifications[i].getClassifiedName();
		}

		Arrays.sort(result);

		return result;
	}

	public static Map<String, String> getExperimentalClassifiedNamesMap(
			final DistributionOption option) {
		Map<String, String> result = new HashMap<String, String>();

		final ClassificationItem[] classifications = option.getClassificationSet()
				.getClassificationItem();
		for (ClassificationItem item : classifications) {
			final String[] experimentals = item.getExperimentName();
			for (String experimental : experimentals) {
				result.put(experimental, item.getClassifiedName());
			}
		}

		return result;
	}

	public static int getMaxPeptideCountWidth(final DistributionOption option) {
		int iMinCount = option.getFilterByPeptide().getFrom();
		for (int i = option.getFilterByPeptide().getFrom(); i <= option
				.getFilterByPeptide().getTo(); i += option.getFilterByPeptide()
				.getStep()) {
			iMinCount = i;
		}

		return Integer.toString(iMinCount).length();
	}

	public static BuildSummaryResult getIdentifiedResultFromProteinCalculationItemList(
			CalculationItemList<BuildSummaryPeptide, BuildSummaryPeptideHit> calculationItems,
			IdentifiedResultFileType fileType) {
		if (fileType == IdentifiedResultFileType.BUILD_SUMMARY) {
			BuildSummaryResult sr = new BuildSummaryResult();

			for (int i = 0; i < calculationItems.size(); i++) {
				sr.addProteinGroup((BuildSummaryProteinGroup) calculationItems.get(i)
						.getKey());
			}

			sr.sort();
			return sr;
		} else {
			throw new IllegalArgumentException(
					"Cannot create IIdentifiedResult from file type " + fileType);
		}
	}

	public static List<BuildSummaryPeptideHit> getPeptideHits(
			final DistributionOption option) throws Exception {
		String peptideFile = RcpaFileUtils.changeExtension(option.getSourceFile()
				.getFileName(), "peptides");

		if (!new File(peptideFile).exists()) {
			try {
				return IdentifiedResultIOFactory.readBuildSummaryPeptideHit(option
						.getSourceFile().getFileName());
			} catch (Exception ex) {
				throw new RuntimeException("Cannot find corresponding peptide file : "
						+ peptideFile);
			}
		}

		return IdentifiedResultIOFactory.readBuildSummaryPeptideHit(peptideFile);
	}

}
