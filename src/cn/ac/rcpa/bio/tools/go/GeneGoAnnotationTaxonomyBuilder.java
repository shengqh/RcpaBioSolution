/*
 * Created on 2006-1-18
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.go;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.annotation.GOAAspectType;
import cn.ac.rcpa.bio.annotation.GOAClassificationEntry;
import cn.ac.rcpa.bio.annotation.IGOEntry;
import cn.ac.rcpa.bio.annotation.impl.GOAnnotationQueryTreeBuilder;
import cn.ac.rcpa.bio.database.AbstractDBApplication;
import cn.ac.rcpa.bio.database.RcpaDBFactory;
import cn.ac.rcpa.bio.database.RcpaDatabaseType;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.utils.DatabaseUtils;

public class GeneGoAnnotationTaxonomyBuilder extends AbstractDBApplication
		implements IFileProcessor {
	private String taxonomy_id;

	private String taxonomy_name;

	public GeneGoAnnotationTaxonomyBuilder(String taxonomy_id,
			String taxonomy_name) {
		super(RcpaDBFactory.getInstance()
				.getConnection(RcpaDatabaseType.ANNOTATION));
		this.taxonomy_id = taxonomy_id;
		this.taxonomy_name = taxonomy_name;
	}

	public GeneGoAnnotationTaxonomyBuilder(Connection connection,
			String taxonomy_id, String taxonomy_name) {
		super(connection);
		this.taxonomy_id = taxonomy_id;
		this.taxonomy_name = taxonomy_name;
	}

	public List<String> process(String destDirectory) throws Exception {
		ArrayList<String> result = new ArrayList<String>();

		for (GOAAspectType type : GOAAspectType.GOA_ASPECT_TYPES) {
			GOAClassificationEntry entry = new GOAnnotationQueryTreeBuilder()
					.getGOAEntryTree(type.getRoot().getAccession(), type
							.getDefaultLevel());

			fillAnnotation(entry);

			String destFile = new File(destDirectory, taxonomy_name + ".go_"
					+ type.getRoot().getName() + ".tree").getAbsolutePath();

			entry.saveToFile(destFile);
			result.add(destFile);
		}
		return result;
	}

	public void fillAnnotation(GOAClassificationEntry rootEntry)
			throws SQLException {
		Map<String, IGOEntry> goaMap = rootEntry.getGOEntryMap();

		String tmpGOTableName = DatabaseUtils.createTempTable(connection, "GO",
				goaMap.keySet().toArray(new String[0]));

		System.out.println("Fill annotation start : " + new java.util.Date());

		fillGOAByTempTable(goaMap, tmpGOTableName);

		System.out.println("Fill annotation end   : " + new java.util.Date());

		rootEntry.removeEmptyGOEntry();

		rootEntry.fillGOAOther();
	}

	private void fillGOAByTempTable(Map<String, IGOEntry> goaMap,
			String tmpGOTableName) {
		final String sql = "SELECT DISTINCT gg.GENE_ID, tg.ACCESSNUMBER from GENE2GO as gg, GOPATH as gp, "
				+ tmpGOTableName
				+ " as tg "
				+ "where gg.TAX_ID=:tax_id AND gp.FATHER_GO=tg.ACCESSNUMBER AND gg.GO_ID=gp.CHILD_GO";
		System.out.println(sql);
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, taxonomy_id);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				GOAClassificationEntry entry = (GOAClassificationEntry) goaMap.get(rs
						.getString(2));
				entry.getProteins().add(rs.getString(1));
			}
		} catch (SQLException ex) {
			throw new IllegalStateException(ex.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		new GeneGoAnnotationTaxonomyBuilder("10090", "mouse")
				.process("F:\\science\\data\\mousedb\\gene");
	}

}
