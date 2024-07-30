package util;

import base.TestBase;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelApiTest {
    public FileInputStream fis = null;
    public XSSFWorkbook workbook = null;
    public XSSFSheet sheet = null;
    public XSSFRow row = null;
    public XSSFCell cell = null;
    String cellValue;

    public ExcelApiTest(String xlFilePath) throws Exception {
        fis = new FileInputStream(xlFilePath);
        workbook = new XSSFWorkbook(fis);
        fis.close();
    }

    public String getCellData(String sheetName, String colName, int rowNum) {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        int col_Num = -1;
        try {
            sheet = workbook.getSheet(sheetName);
            row = sheet.getRow(0);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
                    col_Num = i;
            }
            row = sheet.getRow(rowNum - 1);
            cell = row.getCell(col_Num);
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            } else if (DateUtil.isCellDateFormatted(cell)) {
                    DateFormat df = new SimpleDateFormat("M/d/YY");
                    Date date = cell.getDateCellValue();
                    return cellValue = df.format(date);
            } else if (cell.getCellType() == CellType.FORMULA) {
                switch (evaluator.evaluateFormulaCell(cell)) {
                    case BOOLEAN:
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case NUMERIC:
                        cellValue = String.valueOf(cell.getNumericCellValue());
                        break;
                    case STRING:
                        cellValue = String.valueOf(cell.getStringCellValue());
                        break;
                }
                return cellValue;
            } else if (cell.getCellType() == CellType.BLANK)
                return "";

            else {
                return String.valueOf(cell.getBooleanCellValue());
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            return "row " + rowNum + " or column " + col_Num + " does not exist  in Excel";
        }

    }

    public int getIntCellData(String sheetName, String colName, int rowNum) {
        int col_Num = -1;
        try {
            sheet = workbook.getSheet(sheetName);
            row = sheet.getRow(0);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
                    col_Num = i;
            }

            row = sheet.getRow(rowNum - 1);
            cell = row.getCell(col_Num);

            int cellValue = (int) (cell.getNumericCellValue());
            return cellValue;
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.parseInt("row " + rowNum + " or column " + col_Num + " does not exist  in Excel");
        }
    }

    public int getEmptyCellNumber(String sheetName, String colName) {

        return 1;
    }

}
