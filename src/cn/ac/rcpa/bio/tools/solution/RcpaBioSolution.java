package cn.ac.rcpa.bio.tools.solution;

import cn.ac.rcpa.bio.tools.BisulfideSpectrumUI;
import cn.ac.rcpa.bio.tools.TheoreticalDigestorUI;
import cn.ac.rcpa.bio.tools.TheoreticalSpectrumBuilderUI;
import cn.ac.rcpa.bio.tools.TruncatedDigestBuilderUI;
import cn.ac.rcpa.bio.tools.convert.ExtractPeptideHitFromProteinUI;
import cn.ac.rcpa.bio.tools.convert.FastaToAligmentCompatibleConverterUI;
import cn.ac.rcpa.bio.tools.convert.UniprotDat2FastaUI;
import cn.ac.rcpa.bio.tools.database.CalculateDatabaseSizeUI;
import cn.ac.rcpa.bio.tools.database.ExtractFastaFileByAccessNumberPatternUI;
import cn.ac.rcpa.bio.tools.database.ExtractFastaFileByAccessNumberUI;
import cn.ac.rcpa.bio.tools.database.ExtractFastaFileByReferenceUI;
import cn.ac.rcpa.bio.tools.database.Fasta2AccessNumberUI;
import cn.ac.rcpa.bio.tools.database.IPIAccessNumber2SwissProtUI;
import cn.ac.rcpa.bio.tools.filter.IdentifiedPeptideInUniqueXProteinFilterUI;
import cn.ac.rcpa.bio.tools.filter.IdentifiedPeptideSpRankFilterUI;
import cn.ac.rcpa.bio.tools.filter.IdentifiedProteinInfoUniqueXPeptideDistillerUI;
import cn.ac.rcpa.bio.tools.filter.IdentifiedProteinUniquePeptideCountFilterUI;
import cn.ac.rcpa.bio.tools.filter.IdentifiedResultUniqueXPeptideFilterUI;
import cn.ac.rcpa.bio.tools.filter.ProteinFastaFilterByWeightLargerUI;
import cn.ac.rcpa.bio.tools.filter.ProteinFastaFilterByWeightLessUI;
import cn.ac.rcpa.bio.tools.image.MascotSpectraMultipleFileImageBuilderUI;
import cn.ac.rcpa.bio.tools.image.SequestSpectraImageBuilderUI;
import cn.ac.rcpa.bio.tools.modification.BuildSummaryPeptideHitModificationFilterUI;
import cn.ac.rcpa.bio.tools.modification.IdentifiedPeptideDifferentStateStatisticsCalculatorUI;
import cn.ac.rcpa.bio.tools.modification.IdentifiedPeptideModificationStatisticsCalculatorUI;
import cn.ac.rcpa.bio.tools.modification.IdentifiedResultModificationFilterUI;
import cn.ac.rcpa.bio.tools.modification.IdentifiedResultO18FilterUI;
import cn.ac.rcpa.bio.tools.modification.ModifiedPeptideOnlyReportBuilderUI;
import cn.ac.rcpa.bio.tools.modification.ModifiedPeptidePairFractionAndPhValueReportBuilderUI;
import cn.ac.rcpa.bio.tools.modification.ModifiedPeptidesSpecialFormatDistillerUI;
import cn.ac.rcpa.bio.tools.modification.PairwiseDifferentModificationPeptideFilterUI;
import cn.ac.rcpa.bio.tools.modification.PairwiseModificationPeptideFilterUI;
import cn.ac.rcpa.bio.tools.other.MergePeptideBuilderUI;
import cn.ac.rcpa.bio.tools.other.PeptideDetectabilityBuilderUI;
import cn.ac.rcpa.bio.tools.other.SubstractPeptideBuilderUI;
import cn.ac.rcpa.bio.tools.report.BuildSummaryPeptideHitReporterUI;
import cn.ac.rcpa.bio.tools.report.BuildSummaryProteinReporterUI;
import cn.ac.rcpa.bio.tools.report.ProteinSequenceReporterUI;
import cn.ac.rcpa.bio.tools.report.RelexExcelReportBuilderUI;
import cn.ac.rcpa.bio.tools.solution.commands.BuildSummaryResultViewerCommand;
import cn.ac.rcpa.bio.tools.solution.commands.IdentifiedResultN15FilterCommand;
import cn.ac.rcpa.bio.tools.solution.commands.RelexProteinMergerCommand;
import cn.ac.rcpa.bio.tools.statistic.IdentifiedPeptideAminoacidStatisticCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.IdentifiedPeptideStatisticsCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.IdentifiedResultSummaryBuilderUI;
import cn.ac.rcpa.bio.tools.statistic.IdentifiedScanDistributionCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.MassShiftProcessorUI;
import cn.ac.rcpa.bio.tools.statistic.ObverseReverseIdentifiedPeptideSeparatorUI;
import cn.ac.rcpa.bio.tools.statistic.OptimalXCorrAndDeltaCnCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.PValueOfOverRepresentedByHypergeometricCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.PeptideFDRCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.PeptideSequenceStatisticsCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.ProteinFalseDiscoveryRateCalculatorUI;
import cn.ac.rcpa.bio.tools.statistic.ProteinPositiveProbabilityCalculatorUI;
import cn.ac.rcpa.tools.GrappaGraphViewerUI;

public class RcpaBioSolution extends AbstractRcpaSolutionUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3125540856649064270L;

	private static final String title = "RCPA Bioinformatics Solution";

	private static final String version = "2.0.6";

	public RcpaBioSolution() {
		super(title, version);
	}

	@Override
	protected void addAllCommand() {
		/** Annotation **/
		//addCommand(new SequestProteinGroupSimplifierCommand());
		//addCommand(new ProteinAnnotationByFastaFileUI.Command());
		addCommand(new SequestSpectraImageBuilderUI.Command());
		//addCommand(new MascotSpectraImageBuilderUI.Command());
		addCommand(new MascotSpectraMultipleFileImageBuilderUI.Command());

		// addCommand(new DBAnnotationFromFastaFileUI.Command());
		// addCommand(new SqlProcessorUI.Command());
		// addCommand(new GOAnnotationByFastaFileUI.Command());
		// addCommand(new GOEntryTreeViewerUI.Command());
		// addCommand(new GOAnnotationSpecialCategoryIdentifierUI.Command());
		// addCommand(new
		// GOAnnotation2OverRepresentedTableProcessorUI.Command());
		// addCommand(new GeneGoAnnotationTaxonomyBuilderUI.Command());
		// addCommand(new UnigeneGoAnnotationBuilderUI.Command());
		// addCommand(new SubcellularLocationAnnotatorUI.Command());

		//addCommand(new DistributionCalculatorUI.Command());
		addCommand(new TheoreticalDigestorUI.Command());
		addCommand(new TheoreticalSpectrumBuilderUI.Command());
		//addCommand(new BuildFastaDatabaseUI.Command());
		addCommand(new BuildSummaryResultViewerCommand());
		addCommand(new BisulfideSpectrumUI.Command());
		addCommand(new IdentifiedPeptideStatisticsCalculatorUI.Command());
		addCommand(new PeptideSequenceStatisticsCalculatorUI.Command());
		addCommand(new RelexProteinMergerCommand());
		addCommand(new IdentifiedResultModificationFilterUI.Command());
		addCommand(new IdentifiedResultO18FilterUI.Command());
		//addCommand(new ReversedDatabaseBuilderUI.Command());
		//addCommand(new ReversedDatabaseOnlyBuilderUI.Command());
		addCommand(new OptimalXCorrAndDeltaCnCalculatorUI.Command());
		addCommand(new IdentifiedScanDistributionCalculatorUI.Command());
		addCommand(new IdentifiedResultN15FilterCommand());
		addCommand(new IdentifiedResultSummaryBuilderUI.Command());
		addCommand(new ExtractPeptideHitFromProteinUI.Command());
		addCommand(new PairwiseModificationPeptideFilterUI.Command());
		addCommand(new PairwiseDifferentModificationPeptideFilterUI.Command());
		addCommand(new IdentifiedPeptideSpRankFilterUI.Command());
		addCommand(new UniprotDat2FastaUI.Command());
		addCommand(new FastaToAligmentCompatibleConverterUI.Command());
		addCommand(new IdentifiedProteinUniquePeptideCountFilterUI.Command());
		addCommand(new ExtractFastaFileByAccessNumberUI.Command());
		addCommand(new ExtractFastaFileByReferenceUI.Command());
		addCommand(new Fasta2AccessNumberUI.Command());
		addCommand(new IPIAccessNumber2SwissProtUI.Command());
		addCommand(new IdentifiedPeptideAminoacidStatisticCalculatorUI.Command());
		addCommand(new ProteinFastaFilterByWeightLargerUI.Command());
		addCommand(new ProteinFastaFilterByWeightLessUI.Command());
		addCommand(new IdentifiedPeptideInUniqueXProteinFilterUI.Command());
		addCommand(new IdentifiedResultUniqueXPeptideFilterUI.Command());
		addCommand(new BuildSummaryPeptideHitModificationFilterUI.Command());
		addCommand(new IdentifiedProteinInfoUniqueXPeptideDistillerUI.Command());
		addCommand(new BuildSummaryPeptideHitReporterUI.Command());
		addCommand(new BuildSummaryProteinReporterUI.Command());
		addCommand(new ProteinSequenceReporterUI.Command());
		// addCommand(new ShotgunExcelReportBuilderUI.Command());
		addCommand(new GrappaGraphViewerUI.Command());
		addCommand(new RelexExcelReportBuilderUI.Command());
		addCommand(new ObverseReverseIdentifiedPeptideSeparatorUI.Command());
		addCommand(new ProteinPositiveProbabilityCalculatorUI.Command());
		addCommand(new IdentifiedPeptideModificationStatisticsCalculatorUI.Command());
		addCommand(new IdentifiedPeptideDifferentStateStatisticsCalculatorUI.Command());
		//addCommand(new MascotGenericFormat2DtaUI.Command());
		//addCommand(new Dta2MascotGenericFormatUI.Command());
		addCommand(new ModifiedPeptidesSpecialFormatDistillerUI.Command());
		addCommand(new TruncatedDigestBuilderUI.Command());
		addCommand(new ModifiedPeptidePairFractionAndPhValueReportBuilderUI.Command());
		addCommand(new ModifiedPeptideOnlyReportBuilderUI.Command());
		// addCommand(new MascotSummaryBuilderUI.Command());
		addCommand(new ProteinFalseDiscoveryRateCalculatorUI.Command());
		addCommand(new MassShiftProcessorUI.Command());
		addCommand(new PeptideDetectabilityBuilderUI.Command());
		addCommand(new SubstractPeptideBuilderUI.Command());
		addCommand(new MergePeptideBuilderUI.Command());
		addCommand(new PValueOfOverRepresentedByHypergeometricCalculatorUI.Command());
		addCommand(new PeptideFDRCalculatorUI.Command());
		addCommand(new ExtractFastaFileByAccessNumberPatternUI.Command());
		addCommand(new CalculateDatabaseSizeUI.Command());
		// addCommand(new MascotHtml2TextProcessorUI.Command());
	}

	public static void main(String[] args) {
		new RcpaBioSolution().showSelf();
	}
}
