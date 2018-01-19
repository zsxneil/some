package com.my.poi;

/**
 * 提单数据导出Excel 生成器
 * @version 1.0
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class ExcelFileGenerator {

	private final int SPLIT_COUNT = 1500; //Excel每个工作簿的行数

	private List<Object> fieldName = null; //excel标题数据集

	private List<Object> fieldData = null; //excel数据内容	

	private HSSFWorkbook workBook = null;

	/**
	 * 构造器
	 * @param fieldName 结果集的字段名
	 */
	public ExcelFileGenerator(List<Object> fieldName, List<Object> fieldData) {

		this.fieldName = fieldName;
		this.fieldData = fieldData;
	}

	/**
	 * 创建HSSFWorkbook对象
	 * @return HSSFWorkbook
	 */
	public HSSFWorkbook createWorkbook() {

		workBook = new HSSFWorkbook();
		int rows = fieldData.size();
		int sheetNum = 0;

		if (rows % SPLIT_COUNT == 0) {
			sheetNum = rows / SPLIT_COUNT;
		} else {
			sheetNum = rows / SPLIT_COUNT + 1;
		}

		for (int i = 1; i <= sheetNum; i++) {
			HSSFSheet sheet = workBook.createSheet("Page " + i);
			
			//设置表头样式  2016-11-12
			HSSFCellStyle cellStyle = workBook.createCellStyle();
			cellStyle.setWrapText(true);
			HSSFFont font = workBook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setColor(HSSFColor.RED.index);
			cellStyle.setFont(font);
			
			HSSFRow headRow = sheet.createRow((short) 0); 
			for (int j = 0; j < fieldName.size(); j++) {
				HSSFCell cell = headRow.createCell((short) j);
				//添加样式
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle);
				sheet.setColumnWidth((short)j, (short)8000);
				if(fieldName.get(j) != null){
					HSSFRichTextString richText = new HSSFRichTextString((String) fieldName.get(j));
					cell.setCellValue(richText);
				}else{
					HSSFRichTextString richText = new HSSFRichTextString("-");
					cell.setCellValue(richText);
				}
			}
			
			HSSFCellStyle cellDataStyle = workBook.createCellStyle();
			cellDataStyle.setWrapText(true);
			cellDataStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			for (int k = 0; k < (i == sheetNum ? rows % SPLIT_COUNT : SPLIT_COUNT); k++) {
				HSSFRow row = sheet.createRow((short) (k + 1));
				row.setHeight((short)300);
				//将数据内容放入excel单元格
				ArrayList<Object> rowList = (ArrayList<Object>) fieldData.get((i - 1) * SPLIT_COUNT + k );
				for (int n = 0; n < rowList.size(); n++) {
					HSSFCell cell = row.createCell(n);
					//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellStyle(cellDataStyle);
					if(rowList.get(n) != null){
						HSSFRichTextString richText = new HSSFRichTextString(rowList.get(n).toString());
						cell.setCellValue(richText);
					}else{
						HSSFRichTextString richText = new HSSFRichTextString("");
						cell.setCellValue(richText);
					}
				}
			}
		}
		return workBook;
	}

	public void expordExcel(OutputStream os)  {
		workBook = createWorkbook();
//		byte[] buffer = workBook.getBytes();
//		os.write(buffer);
		try {
			workBook.write(os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("E:\\fb.xls");
		OutputStream os = new FileOutputStream(file);
		ArrayList<Object> fieldName = new ArrayList();
		fieldName.add("头1\ntbyi");
		fieldName.add("头2");
		fieldName.add("头3");
		fieldName.add("头4");
		fieldName.add("头5");
		
		ArrayList<Object> fieldData = new ArrayList();
	
		for(int i=0;i<2000;i++) {
			List<Object> row = new ArrayList();
			row.add("分页测试" + i);
			row.add("分页测试" + i);
			row.add("分页测试" + i);
			row.add("分页测试" + i);
			row.add("分页测试" + i);
			
			fieldData.add(row);
		}
		
		ExcelFileGenerator generator = new ExcelFileGenerator(fieldName, fieldData);
		generator.expordExcel(os);
	}

}

