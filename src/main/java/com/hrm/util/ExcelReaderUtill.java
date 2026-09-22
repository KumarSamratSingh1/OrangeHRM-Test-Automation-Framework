package com.hrm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.SkipException;

import com.hrm.base.TestBase;

public class ExcelReaderUtill extends TestBase {

	public static String TESTDATA_SHEET_PATH = findTestDataPath();

	private static String findTestDataPath() {
		String sysPath = System.getProperty("testdata.sheet.path");
		if (sysPath != null && new File(sysPath).exists()) {
			return sysPath;
		}
		String[] possiblePaths = {
			System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx",
			System.getProperty("user.dir") + "/src/main/java/com/hrm/testdata/TestData.xlsx",
			"src/test/resources/TestData.xlsx",
			"src/main/java/com/hrm/testdata/TestData.xlsx"
		};
		for (String path : possiblePaths) {
			if (new File(path).exists()) {
				return path;
			}
		}
		return System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";
	}

	public static Object[][] getTestData(String sheetName) {
		Workbook book = null;
		InputStream is = null;
		try {
			File file = new File(TESTDATA_SHEET_PATH);
			if (file.exists()) {
				is = new FileInputStream(file);
			} else {
				is = ExcelReaderUtill.class.getClassLoader().getResourceAsStream("TestData.xlsx");
				if (is == null) {
					is = ExcelReaderUtill.class.getResourceAsStream("/com/hrm/testdata/TestData.xlsx");
				}
			}

			if (is == null) {
				throw new SkipException("TestData.xlsx not found at " + TESTDATA_SHEET_PATH + ". This test requires external test data.");
			}

			book = WorkbookFactory.create(is);
			Sheet sheet = book.getSheet(sheetName);

			if (sheet == null) {
				throw new SkipException("Sheet '" + sheetName + "' not found in TestData.xlsx. This test requires test data.");
			}

			int rowCount = sheet.getLastRowNum();
			if (rowCount <= 0) {
				throw new SkipException("Sheet '" + sheetName + "' in TestData.xlsx contains no data rows. This test requires test data.");
			}

			Row headerRow = sheet.getRow(0);
			if (headerRow == null || headerRow.getLastCellNum() <= 0) {
				throw new SkipException("Sheet '" + sheetName + "' has no header columns. This test requires test data.");
			}

			int colCount = headerRow.getLastCellNum();
			Object[][] data = new Object[rowCount][colCount];
			DataFormatter df = new DataFormatter();

			for (int i = 0; i < rowCount; i++) {
				Row row = sheet.getRow(i + 1);
				for (int k = 0; k < colCount; k++) {
					data[i][k] = (row != null && row.getCell(k) != null) ? df.formatCellValue(row.getCell(k)) : "";
				}
			}
			return data;
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			System.err.println("Error reading test data sheet '" + sheetName + "': " + e.getMessage());
			throw new SkipException("Failed to read test data from sheet '" + sheetName + "': " + e.getMessage() + ". Test requires test data.");
		} finally {
			if (book != null) {
				try {
					book.close();
				} catch (Exception ignored) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception ignored) {
				}
			}
		}
	}
}

