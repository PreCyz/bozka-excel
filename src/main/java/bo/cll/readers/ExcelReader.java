package bo.cll.readers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class ExcelReader {

    private final Path excelPath;
    private final String sheetName;

    private final Map<String, Integer> headerCellMap = new HashMap<>();

    public void readValue(String headerName) {
        try (InputStream inputStream = Files.newInputStream(excelPath)) {
            Workbook workbook;
            if (excelPath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }

            for (Sheet sheet : workbook) {
                if (sheetName.equals(sheet.getSheetName())) {
                    createMapping(sheet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMapping(Sheet sheet) {
        int firstRow = sheet.getFirstRowNum();
        Row row = sheet.getRow(firstRow);
        for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(row.getFirstCellNum(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            headerCellMap.put(getCellValue(cell), cellIndex);
        }
    }

    public String getCellValue(Cell cell) {
        String result = "";
        CellType cellType = CellType.FORMULA.equals(cell.getCellType()) ? cell.getCachedFormulaResultType() : cell.getCellType();
        if (cellType.equals(CellType.STRING)) {
            result = cell.getStringCellValue();
        }
        if (CellType.NUMERIC.equals(cellType)) {
            if (DateUtil.isCellDateFormatted(cell)) {
                result = String.valueOf(cell.getDateCellValue());
            } else {
                result = String.valueOf(cell.getNumericCellValue());
            }
        }
        if (CellType.BOOLEAN.equals(cellType)) {
            result = String.valueOf(cell.getBooleanCellValue());
        }
        return result;
    }

    private void process(Workbook workbook) {
        for (Sheet sheet : workbook) {
            if (sheetName.equals(sheet.getSheetName())) {
                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();
                for (int index = firstRow + 1; index <= lastRow; index++) {
                    Row row = sheet.getRow(index);
                    for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        getCellValue(cell);
                    }
                }
            }
        }
    }
}
