package org.motechproject.whp.mtraining.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.motechproject.whp.mtraining.exception.DataExportException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelTableWriter implements TableWriter {

    private static final short FONT_HEIGHT_IN_POINTS = 11;
    private static final int INDEX_OF_HEADER_ROW = 0;

    private final Workbook workbook;
    private final Sheet sheet;
    private final OutputStream outputStream;

    private Map<String, Integer> columnIndexMap = new HashMap<>();
    private int currentRowIndex;

    private CellStyle headerStyle;
    private CellStyle cellStyle;

    public ExcelTableWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Report");

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        currentRowIndex = 1;

        createCellStyle();
    }

    @Override
    public void writeHeader(String[] headers) throws IOException {
        Row headerRow = sheet.createRow(INDEX_OF_HEADER_ROW);
        Cell headerCell;
        for (int i = 0; i < headers.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
            columnIndexMap.put(headers[i], i);
        }
    }

    @Override
    public void writeRow(Map<String, String> rowMap, String[] headers) throws IOException {
        Row row = sheet.createRow(INDEX_OF_HEADER_ROW + currentRowIndex);
        Cell dataCell;
        for (String header : headers) {
            Integer columnIndex = columnIndexMap.get(header);
            if (columnIndex != null) {
                dataCell = row.createCell(columnIndex);
                dataCell.setCellValue(rowMap.get(header));
                dataCell.setCellStyle(cellStyle);
            } else {
                throw new DataExportException("No such column: " + header);
            }
        }
        currentRowIndex++;
    }

    @Override
    public void close() {
        for (int i = 0; i < columnIndexMap.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new DataExportException("Unable to save Workbook", e);
        }
    }

    private void createCellStyle() {
        Font monthFont = workbook.createFont();
        monthFont.setFontHeightInPoints(FONT_HEIGHT_IN_POINTS);
        monthFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setFont(monthFont);
        headerStyle.setWrapText(true);
        headerStyle.setBorderRight(CellStyle.BORDER_THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(CellStyle.BORDER_THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    }
}
