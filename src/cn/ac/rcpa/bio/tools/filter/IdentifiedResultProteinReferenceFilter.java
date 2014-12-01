/*
 * Created on 2005-5-18
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.ac.rcpa.bio.proteomics.IIdentifiedPeptide;
import cn.ac.rcpa.bio.proteomics.IIdentifiedProtein;
import cn.ac.rcpa.bio.proteomics.IIdentifiedProteinGroup;
import cn.ac.rcpa.bio.proteomics.IdentifiedResultIOFactory;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupFilterByPeptide;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupFilterByProteinFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupNoredundantFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinGroupUniquePeptideCountFilter;
import cn.ac.rcpa.bio.proteomics.filter.IdentifiedProteinReferenceFilter;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryProtein;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.BuildSummaryResult;
import cn.ac.rcpa.bio.sequest.SequestParseException;
import cn.ac.rcpa.filter.AndFilter;
import cn.ac.rcpa.filter.IFilter;
import cn.ac.rcpa.filter.NotFilter;

public class IdentifiedResultProteinReferenceFilter {
  public static void main(String[] args) throws IOException,
      SequestParseException {
    final String resultFile = "x:\\summary\\sqh\\hippocampi\\ipi.RAT_MOUSE_HUMAN.3.04.mono\\hippocampi_2d.proteins";
    //final String resultFile = "F:\\Science\\Data\\HIPP\\summary\\ipi.RAT_MOUSE_HUMAN.3.04.mono\\hippocampi_1_Salt_1\\hippocampi_1_Salt_1.proteins";

    final IFilter<IIdentifiedProtein> proteinFilter = new
        IdentifiedProteinReferenceFilter(
            "Tax_Id=10116");

    final IFilter<IIdentifiedProteinGroup> groupFilter = new
        IdentifiedProteinGroupFilterByProteinFilter(
            proteinFilter, true);

    final NotFilter<IIdentifiedProteinGroup> notGroupFilter = new NotFilter<
        IIdentifiedProteinGroup> (
            groupFilter);

    final IFilter<IIdentifiedProteinGroup> noredundantGroupFilter = new
        IdentifiedProteinGroupNoredundantFilter();

    List<IFilter<IIdentifiedProteinGroup>> filters = new ArrayList<IFilter<
        IIdentifiedProteinGroup>> ();

    //这个Group不能有ParentGroup
    filters.add(noredundantGroupFilter);

    //这个Group以及其子Group中不能有RAT蛋白
    filters.add(notGroupFilter);

    final AndFilter<IIdentifiedProteinGroup> finalGroupFilter1 = new AndFilter<
        IIdentifiedProteinGroup> (
            filters);

    IdentifiedResultFilter.processByGroupFilter(resultFile,
                                                finalGroupFilter1);

    final IFilter<IIdentifiedProteinGroup> uniquePeptideGroupFilter = new
        IdentifiedProteinGroupUniquePeptideCountFilter(
            2, null);

    //要求UniquePeptideCount >= 2
    filters.add(uniquePeptideGroupFilter);

    final AndFilter<IIdentifiedProteinGroup> finalGroupFilter2 = new AndFilter<
        IIdentifiedProteinGroup> (
            filters);

    IdentifiedResultFilter.processByGroupFilter(resultFile,
                                                finalGroupFilter2);

    BuildSummaryResult ir = IdentifiedResultIOFactory
        .readBuildSummaryResult(resultFile);

    HashMap<String,
        Set<IIdentifiedProteinGroup>> scanGroupMap = getScanGroupMap(ir);

    final IFilter<IIdentifiedProteinGroup> percentFilter = new
        IdentifiedProteinGroupFilterByPeptide(scanGroupMap, notGroupFilter);

    //这个Group的所有Peptide不能对应到包含RAT蛋白的Group上。
    filters.add(percentFilter);

    final AndFilter<IIdentifiedProteinGroup> finalGroupFilter3 = new AndFilter<
        IIdentifiedProteinGroup> (
            filters);

    IdentifiedResultFilter.processByGroupFilter(ir, resultFile,
                                                finalGroupFilter3);
  }

  private static HashMap<String,
      Set<IIdentifiedProteinGroup>> getScanGroupMap(BuildSummaryResult ir) {
    HashMap<String,
        Set<IIdentifiedProteinGroup>> scanGroupMap = new HashMap<String,
        Set<IIdentifiedProteinGroup>> ();
    for (int i = 0; i < ir.getProteinGroupCount(); i++) {
      for (BuildSummaryProtein protein : ir.getProteinGroup(i).getProteins()) {
        for (IIdentifiedPeptide peptide : protein.getPeptides()) {
          String filename = peptide.getPeakListInfo().getLongFilename();
          if (!scanGroupMap.containsKey(filename)) {
            scanGroupMap.put(filename, new HashSet<IIdentifiedProteinGroup> ());
          }
          scanGroupMap.get(filename).add(ir.getProteinGroup(i));
        }
      }
    }
    return scanGroupMap;
  }
}
