/*
 * Created on 2006-1-12
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.tools.report;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import cn.ac.rcpa.IParser;
import cn.ac.rcpa.ParserForwarder;
import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.processor.IFileProcessor;

public abstract class AbstractExcelReportBuilder implements IFileProcessor {
	public AbstractExcelReportBuilder(String url, String project,
			SequenceDatabaseType dbType) {
		this.url = url;
		this.project = project;
		this.dbType = dbType;
		this.accessNumberParser = AccessNumberParserFactory.getParser(dbType);
		this.fractionParser = new ParserForwarder<String>("Fraction", "Fraction");
	}

	private String url;

	private IParser<String> fractionParser;

	private IAccessNumberParser accessNumberParser;

	private String project;

	private SequenceDatabaseType dbType;

	protected HSSFWorkbook wb;

	protected HSSFSheet sheet;

	protected HSSFCellStyle normalStyle;

	protected HSSFFont normalFont;

	protected HSSFCellStyle linkStyle;

	protected HSSFFont linkFont;

	protected HSSFCellStyle normalWrapStyle;
	
	protected void initExcel(String sheetName) {
		wb = new HSSFWorkbook();
		sheet = wb.createSheet(sheetName);

		normalStyle = wb.createCellStyle();
		normalFont = wb.createFont();
		normalFont.setFontName("Courier New");
		normalStyle.setFont(normalFont);

		normalWrapStyle = wb.createCellStyle();
		normalWrapStyle.setFont(normalFont);
		normalWrapStyle.setWrapText(true);
		
		linkStyle = wb.createCellStyle();
		linkStyle.setFillForegroundColor(HSSFColor.BLUE.index);
		linkFont = wb.createFont();
		linkFont.setFontName("Courier New");
		linkFont.setColor(HSSFColor.BLUE.index);
		linkStyle.setFont(linkFont);
	}

	public String getFraction(String experiment) {
		return fractionParser.getValue(experiment);
	}

	public void setFractionParser(IParser<String> fractionParser) {
		this.fractionParser = fractionParser;
	}

	public SequenceDatabaseType getDbType() {
		return dbType;
	}

	public IParser<String> getFractionParser() {
		return fractionParser;
	}

	public String getAccessNumber(String proteinName) {
		return accessNumberParser.getValue(proteinName);
	}

	public String getProject() {
		return project;
	}

	public String getUrl() {
		return url;
	}

}
