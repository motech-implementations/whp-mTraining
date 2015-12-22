package org.motechproject.whp.mtraining.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang.StringUtils;
import org.motechproject.whp.mtraining.exception.DataExportException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class PdfTableWriter implements TableWriter {

    private static final float WIDTH_PERCENTAGE = 100.0F;
    public static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    public static final Font TABLE_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Rectangle PAGE_RECTANGLE = new Rectangle(20, 36, 580, 762);

    private final PdfWriter pdfWriter;
    private final Document pdfDocument;
    private final PdfContentByte pdfCanvas;
    private PdfPTable dataTable;

    public PdfTableWriter(OutputStream outputStream) {
        pdfDocument = new Document();
        try {
            pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
        } catch (DocumentException e) {
            throw new DataExportException("Unable to create a PDF writer instance", e);
        }
        pdfDocument.open();
        pdfCanvas = pdfWriter.getDirectContent();
    }

    @Override
    public void writeHeader(String[] headers) throws IOException {
        this.dataTable = new PdfPTable(headers.length);
        this.dataTable.setWidthPercentage(WIDTH_PERCENTAGE);

        for (String header: headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            this.dataTable.addCell(cell);
        }
    }

    @Override
    public void writeRow(Map<String, String> row, String[] headers) throws IOException {
        if (this.dataTable == null) {
            this.writeHeader(headers);
        }

        for (String header: headers) {
            String value = row.get(header);
            if (StringUtils.isBlank(value)) {
                value = "\n";
            }
            Paragraph paragraph = new Paragraph(value, TABLE_FONT);
            PdfPCell cell = new PdfPCell(paragraph);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            this.dataTable.addCell(cell);
        }
    }

    @Override
    public void close() {
        try {
            ColumnText column = new ColumnText(pdfCanvas);
            column.setSimpleColumn(PAGE_RECTANGLE);
            column.addElement(dataTable);
            int status = column.go();
            while (ColumnText.hasMoreText(status)) {
                pdfDocument.newPage();
                column.setSimpleColumn(PAGE_RECTANGLE);
                status = column.go();
            }
            pdfDocument.close();
        } catch (DocumentException ex) {
            throw new DataExportException("Unable to add a table to the PDF file", ex);
        } finally {
            pdfWriter.close();
        }
    }
}
