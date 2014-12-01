/*
 * Created on 2005-12-5
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class POIExcelUtils {
	public static HSSFCell createCell(HSSFRow row, int col, HSSFCellStyle style,
			String value) {
		HSSFCell cell = row.createCell((short) col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	public static HSSFCell createCell(HSSFRow row, int col, HSSFCellStyle style,
			int value) {
		HSSFCell cell = row.createCell((short) col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	public static HSSFCell createCell(HSSFRow row, int col, HSSFCellStyle style,
			double value) {
		HSSFCell cell = row.createCell((short) col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	public static HSSFCell createHyperLinkCell(HSSFRow row, int col,
			HSSFCellStyle style, String link, String value) {
		HSSFCell cell = row.createCell((short) col);
		cell.setCellStyle(style);
		cell.setCellFormula("HYPERLINK(\"" + link + "\",\"" + value + "\")");
		return cell;
	}

	public static HSSFCellStyle getNormalStyle(HSSFWorkbook wb) {
		HSSFCellStyle result = wb.createCellStyle();
		result.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);
		return result;
	}

}
