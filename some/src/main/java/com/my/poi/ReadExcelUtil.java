package com.my.poi;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelUtil {

    public static void main(String[] args) throws Exception{

        //读取xlsx
        Map<Integer, List<String[]>> map = readXlsx("E:/read.xlsx");
        for(int n=0;n<map.size();n++){
            List<String[]> list = map.get(n);
            System.out.println("-------------------------sheet"+n+"--------------------------------");
            for(int i=0;i<list.size();i++){
                String[] arr = (String[]) list.get(i);
                for(int j=0;j<arr.length;j++){
                    if(j==arr.length-1)
                        System.out.print(arr[j]);
                    else
                        System.out.print(arr[j]+"|");
                }
                System.out.println();
            }
        }
        //写入xlsx
        writeXlsx("E:/write.xlsx",map);
    }

    //读取Xlsx
    public static Map<Integer, List<String[]>> readXlsx(String fileName) {
        Map<Integer, List<String[]>> map = new HashMap<Integer, List<String[]>>();
        try {
            InputStream is = new FileInputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            // 循环工作表Sheet
            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                XSSFSheet xssfSheet = workbook.getSheetAt(numSheet);
                if (xssfSheet == null) {
                    continue;
                }
                List<String[]> list = new ArrayList<String[]>();

                for (int row=0;row<=xssfSheet.getLastRowNum();row++){
                    XSSFRow xssfRow = xssfSheet.getRow(row);
                    if (xssfRow == null) {
                        continue;
                    }
                    String[] singleRow = new String[xssfRow.getLastCellNum()];
                    for(int column=0;column<xssfRow.getLastCellNum();column++){
                        Cell cell = xssfRow.getCell(column,Row.CREATE_NULL_AS_BLANK);
                        switch(cell.getCellType()){
                            case Cell.CELL_TYPE_BLANK:
                                singleRow[column] = "";
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                singleRow[column] = Boolean.toString(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_ERROR:
                                singleRow[column] = "";
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                singleRow[column] = cell.getStringCellValue();
                                if (singleRow[column] != null) {
                                    singleRow[column] = singleRow[column].replaceAll("#N/A", "").trim();
                                }
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    singleRow[column] = String.valueOf(cell.getDateCellValue());
                                } else {
                                    cell.setCellType(Cell.CELL_TYPE_STRING);
                                    String temp = cell.getStringCellValue();
                                    // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                                    if (temp.indexOf(".") > -1) {
                                        singleRow[column] = String.valueOf(new Double(temp)).trim();
                                    } else {
                                        singleRow[column] = temp.trim();
                                    }
                                }

                                break;
                            case Cell.CELL_TYPE_STRING:
                                singleRow[column] = cell.getStringCellValue().trim();
                                break;
                            default:
                                singleRow[column] = "";
                                break;
                        }
                    }
                    list.add(singleRow);
                }
                map.put(numSheet, list);
            }
        } catch (FileNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return map;
    }


    //写入Xlsx
    public static void writeXlsx(String fileName,Map<Integer,List<String[]>> map) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            for(int sheetnum=0;sheetnum<map.size();sheetnum++){
                XSSFSheet sheet = wb.createSheet(""+sheetnum);
                List<String[]> list = map.get(sheetnum);
                for(int i=0;i<list.size();i++){
                    XSSFRow row = sheet.createRow(i);
                    String[] str = list.get(i);
                    for(int j=0;j<str.length;j++){
                        XSSFCell cell = row.createCell(j);
                        cell.setCellValue(str[j]);
                    }
                }
            }
            FileOutputStream outputStream = new FileOutputStream(fileName);
            wb.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }


}
