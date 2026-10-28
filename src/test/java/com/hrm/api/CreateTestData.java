package com.hrm.api;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.nio.file.*;

public class CreateTestData {
    public static void main(String[] args) throws Exception {
        String basePath = System.getProperty("user.dir");
        String xlsxPath = basePath + "/src/test/resources/TestData.xlsx";
        try (Workbook wb = new XSSFWorkbook()) {
            createSheet(wb, "PersonalDetails",
                new String[]{"FirstName","MiddleName","LastName","EmpId","OtherId","LicenseNo","Gender","MaritalStatus","Nationality"},
                new String[][]{{"SHISHU","RAJ","PANDEY","1048","2017","MH14GT7423","Male","Single","Indian"}});
            createSheet(wb, "ContactDetails",
                new String[]{"Address","City","State","Zipcode","Country","MobileNo"},
                new String[][]{{"Hinjewadi Phase-I","Pune","Maharashtra","411057","India","9716039602"}});
            createSheet(wb, "DependentDetails",
                new String[]{"DependentName","Relationship","DOB"},
                new String[][]{{"Vishal","Friend","2/1/1993"}});
            createSheet(wb, "EmergencyContactDetails",
                new String[]{"ContactName","Relationship","MobileNo","WorkPhone"},
                new String[][]{{"Priya Sharma","Parent","9876543210","02066545700"}});
            createSheet(wb, "ImmigrationDetails",
                new String[]{"DocType","DocNumber","Status","Country"},
                new String[][]{{"Passport","P1234567","Active","India"}});
            createSheet(wb, "AddLanguage",
                new String[]{"Language","Fluency","Competency"},
                new String[][]{{"English","Writing","Good"}});
            createSheet(wb, "Membership",
                new String[]{"Membership","PaidBy","Amount","Currency"},
                new String[][]{{"PayClub","Company","1500","Indian Rupee"}});
            createSheet(wb, "WorkExperience",
                new String[]{"Company","JobTitle"},
                new String[][]{{"Msys Technologies","QA Engineer"}});
            Path path = Paths.get(xlsxPath);
            Files.createDirectories(path.getParent());
            try (FileOutputStream fos = new FileOutputStream(xlsxPath)) {
                wb.write(fos);
            }
            System.out.println("TestData.xlsx created at: " + xlsxPath);
        }
    }
    private static void createSheet(Workbook wb, String name, String[] headers, String[][] rows) {
        Sheet s = wb.createSheet(name);
        Row h = s.createRow(0);
        for (int i = 0; i < headers.length; i++) h.createCell(i).setCellValue(headers[i]);
        for (int r = 0; r < rows.length; r++) {
            Row row = s.createRow(r + 1);
            for (int c = 0; c < rows[r].length; c++) row.createCell(c).setCellValue(rows[r][c]);
        }
    }
}
