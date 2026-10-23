package CRM_Vtiger_BITM_GenericUtility;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class CRM_Vtiger_BITM_ExcelUtility implements AutoCloseable {

    // Excel path is embedded here, not in the properties file.
    private static final String EXCEL_PATH = "src/test/resources/CRM_VTiger_Data.xlsx";

    private Workbook workbook;
    private final DataFormatter formatter = new DataFormatter();

    // Open the default Excel workbook.
    public void openWorkbook() throws IOException {
        openWorkbook(EXCEL_PATH);
    }

    // Open a workbook using a specified path when needed.
    public void openWorkbook(String path) throws IOException {

        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Excel file path cannot be empty.");
        }

        File file = new File(path);

        if (!file.exists() || !file.isFile()) {
            throw new IOException("Excel file was not found: " + file.getAbsolutePath());
        }

        closeWorkbook();

        try (FileInputStream input = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(input);
        } catch (Exception e) {
            throw new IOException("Unable to open Excel workbook: " + file.getAbsolutePath(), e);
        }
    }

    // Get a worksheet by name.
    private Sheet getSheet(String sheetName) {

        if (workbook == null) {
            throw new IllegalStateException("Workbook is not open. Call openWorkbook() first.");
        }

        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            throw new IllegalArgumentException("Worksheet not found: " + sheetName);
        }

        return sheet;
    }

    // Get cell text safely.
    private String getCellValue(Row row, int columnIndex) {

        if (row == null) {
            return "";
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell).trim();
    }

    // Read all non-empty data rows as maps using column headers.
    public List<Map<String, String>> getAllDataAsMaps(String sheetName) {

        Sheet sheet = getSheet(sheetName);
        List<Map<String, String>> records = new ArrayList<>();

        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {
            throw new IllegalArgumentException("Header row is missing in worksheet: " + sheetName);
        }

        int lastColumn = headerRow.getLastCellNum();

        if (lastColumn <= 0) {
            throw new IllegalArgumentException("No column headers found in worksheet: " + sheetName);
        }

        List<String> headers = new ArrayList<>();

        for (int column = 0; column < lastColumn; column++) {

            String header = getCellValue(headerRow, column);

            if (header.isEmpty()) {
                throw new IllegalArgumentException("Blank column header at column index " + column + " in worksheet: " + sheetName);
            }

            headers.add(header);
        }

        for (int rowIndex = 1;
             rowIndex <= sheet.getLastRowNum();
             rowIndex++) {

            Row row = sheet.getRow(rowIndex);

            if (row == null || isRowEmpty(row, lastColumn)) {
                continue;
            }

            Map<String, String> record = new LinkedHashMap<>();

            for (int column = 0; column < headers.size(); column++) {

                record.put(headers.get(column), getCellValue(row, column));
            }

            records.add(record);
        }

        return records;
    }

    // Check whether a row contains any data.
    private boolean isRowEmpty(Row row, int columnCount) {

        for (int column = 0; column < columnCount; column++) {

            if (!getCellValue(row, column).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    // Read one cell using its header and a zero-based data-row index.
    // Row index 0 means the first data row, below the header.
    public String getCellDataByHeader(String sheetName, int rowNum, String columnName) {

        if (rowNum < 0) {
            throw new IllegalArgumentException("Data row index cannot be negative.");
        }

        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be empty.");
        }

        Sheet sheet = getSheet(sheetName);
        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {
            throw new IllegalArgumentException("Header row is missing in worksheet: " + sheetName);
        }

        int columnIndex = -1;

        for (int column = 0; column < headerRow.getLastCellNum();
             column++) {

            String header = getCellValue(headerRow, column);

            if (header.equalsIgnoreCase(columnName.trim())) {
                columnIndex = column;
                break;
            }
        }

        if (columnIndex == -1) {
            throw new IllegalArgumentException("Column '" + columnName + "' not found in worksheet: " + sheetName);
        }

        Row dataRow = sheet.getRow(rowNum + 1);

        if (dataRow == null) {
            return "";
        }

        return getCellValue(dataRow, columnIndex);
    }

    // Read all rows as a two-dimensional list, including headers.
    public List<List<String>> getAllData(String sheetName) {

        Sheet sheet = getSheet(sheetName);
        List<List<String>> allRows = new ArrayList<>();

        int lastColumn = 0;

        Row headerRow = sheet.getRow(0);

        if (headerRow != null && headerRow.getLastCellNum() > 0) {
            lastColumn = headerRow.getLastCellNum();
        }

        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum();
             rowIndex++) {

            Row row = sheet.getRow(rowIndex);

            if (row == null) {
                continue;
            }

            List<String> rowData = new ArrayList<>();

            for (int column = 0; column < lastColumn; column++) {
                rowData.add(getCellValue(row, column));
            }

            allRows.add(rowData);
        }

        return allRows;
    }

    // Close workbook resources safely.
    public void closeWorkbook() throws IOException {

        if (workbook != null) {
            workbook.close();
            workbook = null;
        }
    }

    @Override
    public void close() throws IOException {
        closeWorkbook();
    }
}
