package org.adelbs.iso8583.bsparser.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private Hashtable<String, Integer> celNum = new Hashtable<String, Integer>();
	private Hashtable<String, String> celValues = new Hashtable<String, String>();

	private Integer rowCount = 0;
	private Integer currentRow = 0;
	
	public ExcelReader(String fileName) {
		this(fileName, null);
	}
	
	public ExcelReader(String fileName, String sheetName) {

		int rowNumber = 0;
		int colNumber = 0;

		try {
			FileInputStream file = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet;
			Iterator<Row> rowIterator;
			
			if (sheetName != null)
				sheet = workbook.getSheet(sheetName);
			else
				sheet = workbook.getSheetAt(0);
			
			Row row;
			Cell cell;
			Iterator<Cell> cellIterator;
			
			if (sheet != null) {
				rowIterator = sheet.iterator();
				
				while (rowIterator.hasNext()) {
					row = rowIterator.next();
					cellIterator = row.cellIterator();
					colNumber = 0;
					
					while (cellIterator.hasNext()) {
						cell = cellIterator.next();
						
						if (rowNumber == 0)
							celNum.put(cellVal(cell), colNumber);
						else
							celValues.put("["+ rowNumber +","+ colNumber +"]", cellVal(cell));
						
						colNumber++;
					}
					
					rowNumber++;
				}
			}
			
			rowCount = rowNumber - 1;
			file.close();
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	private String cellVal(Cell cell) {
		String result = "";
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) 
			result = String.valueOf(cell.getNumericCellValue());
		else if (cell.getCellType() == Cell.CELL_TYPE_STRING) 
			result = cell.getStringCellValue();
		else
			result = "NOT SUPPORTED";
		
		return result;
	}

	public String getCel(String celName) {
		String value = "";
		if (currentRow > 0 && currentRow <= rowCount)
			value = getCel(currentRow, celName);
		return value;
	}
	
	public String getCel(int rowNum, String celName) {
		String result = "";
		String keyValue = "";
		
		if (celNum.containsKey(celName)) {
			keyValue = "["+ rowNum +","+ celNum.get(celName) +"]";
			if (celValues.containsKey(keyValue)) {
				result = celValues.get(keyValue);
			}
		}
		
		return result;
	}
	
	public Integer getRowCount() {
		return rowCount;
	}
	
	public Integer getRowNum() {
		return currentRow;
	}
	
	public boolean next() {
		boolean result = hasMoreRows();
		
		if (currentRow > -1 && currentRow < rowCount)
			currentRow++;
		else
			currentRow = -1;
		
		return result;
	}
	
	public boolean hasMoreRows() {
		return (currentRow > -1 && currentRow < rowCount);
	}
	
	public void gotoRow(Integer number) {
		if (number > -1 && number <= rowCount)
			currentRow = number;
		else
			currentRow = -1;
	}
	
	public void gotoRow(String colName, String colValue) {
		currentRow = -1;
		if (colName != null && colValue != null) {
			for (int i = 0; i <= rowCount; i++) {
				if (colValue.equals(getCel(i, colName))) {
					currentRow = i;
					break;
				}
			}
		}		
	}
}
