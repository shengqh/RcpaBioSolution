package cn.ac.rcpa.bio.tools.distribution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.exception.RcpaParseException;
import cn.ac.rcpa.bio.proteomics.IDistributionReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryPeptideHitExperimentalReader;
import cn.ac.rcpa.bio.proteomics.results.buildsummary.io.BuildSummaryResultExperimentalReader;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem;
import cn.ac.rcpa.bio.tools.distribution.option.ClassificationSet;
import cn.ac.rcpa.bio.tools.distribution.option.DistributionOption;
import cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide;
import cn.ac.rcpa.bio.tools.distribution.option.SourceFile;
import cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class DistributionOptionGenerator {
	public DistributionOptionGenerator() {
	}

	public File createOptionFile(SourceFile sourceFile, SequenceDatabaseType dbType,
			DistributionType dtbType, ClassificationInfo classificationInfo,
			FilterByPeptide filterByPeptide, boolean modifiedPeptideOnly,
			String modifiedAminoacid) throws IOException, ValidationException,
			MarshalException, RcpaParseException {
		DistributionOption option = DistributionOptionUtils
				.createDistributionOption(classificationInfo, filterByPeptide);

		option.setDatabaseType(dbType.toString());

		option.setDistributionType(dtbType);

		option.setSourceFile(sourceFile);

		option.setClassificationSet(getClassificationSet(sourceFile, dbType));

		option.setModifiedPeptideOnly(modifiedPeptideOnly);

		option.setModifiedAminoacid(modifiedAminoacid);

		File result = getOptionFilename(option);

		option.marshal(new FileWriter(result));

		return result;
	}

	/**
	 * 给定源文件和分类的类型，得到保存option的文件
	 * 
	 * @param sourceFile
	 *          File
	 * @param principle
	 *          ClassificationPrincipleType
	 * @return File
	 */
	public File getOptionFilename(DistributionOption option) {
		final File sourceFile = new File(option.getSourceFile().getFileName());
		final String title = option.getDistributionType().toString() + "_"
				+ option.getClassificationInfo().getClassificationPrinciple();
		File resultDir = new File(sourceFile.getParent(), title + "_CLASSIFICATION");
		resultDir.mkdir();
		return new File(resultDir, sourceFile.getName() + "." + title
				+ ".statistic.xml");
	}

	/**
	 * 给定sourceFile和classification方式，得到ClassificationSet
	 * 
	 * @param sourceFile
	 *          SourceFile
	 * @param sphc
	 *          IIdentifiedPeptideClassification
	 * @throws IOException
	 * @throws RcpaParseException
	 * @return ClassificationSet
	 */
	private ClassificationSet getClassificationSet(SourceFile sourceFile,
			SequenceDatabaseType dbType) throws IOException, RcpaParseException {
		ClassificationSet result = new ClassificationSet();

		final Set<String> experiment = getExperimental(sourceFile.getFileName());

		fillClassifiedExperimentalMap(result, experiment);

		return result;
	}

	private Set<String> getExperimental(String proteinFile) throws IOException {
		String openFile = RcpaFileUtils.changeExtension(proteinFile, "peptides");

		IDistributionReader reader = null;
		if (new File(openFile).exists()) {
			reader = BuildSummaryPeptideHitExperimentalReader.getInstance();
		} else {
			reader = BuildSummaryResultExperimentalReader.getInstance();
			openFile = proteinFile;
		}

		return reader.getExperimental(openFile);
	}

	/**
	 * 将classifiedName与experimental的对应表填充到ClassificationSet中
	 * 
	 * @param cs
	 *          ClassificationSet
	 * @param experiment
	 *          Map
	 * @throws IndexOutOfBoundsException
	 */
	private void fillClassifiedExperimentalMap(ClassificationSet cs,
			Set<String> experiment) throws IndexOutOfBoundsException {
		cs.clearClassificationItem();

		List<String> experimentalNames = new ArrayList<String>(experiment);
		HashMap<String,List<String>> map = new HashMap<String, List<String>>();
		
		Pattern p = Pattern.compile("(.+?)_{0,1}\\d+$"); 
		for (String experimental : experimentalNames) {
			Matcher m = p.matcher(experimental);
			String key;
			if(m.find()){
				key = m.group(1);
			}
			else{
				key = experimental;
			}
			
			if(!map.containsKey(key)){
				map.put(key, new ArrayList<String>());
			}
			
			map.get(key).add(experimental);
		}

		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			ClassificationItem item = new ClassificationItem();
			item.setClassifiedName(key);
			List<String> values = map.get(key);
			Collections.sort(values);
			for(String value : values){
				item.addExperimentName(value);
			}
			cs.addClassificationItem(item);
		}
	}
}
