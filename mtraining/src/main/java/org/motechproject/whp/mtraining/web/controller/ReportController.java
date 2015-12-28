package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.dto.ProviderWiseStatusReportDto;
import org.motechproject.whp.mtraining.dto.TrainingStatusReportDto;
import org.motechproject.whp.mtraining.service.ReportService;
import org.motechproject.whp.mtraining.util.ExcelTableWriter;
import org.motechproject.whp.mtraining.util.PdfTableWriter;
import org.motechproject.whp.mtraining.util.TableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang.CharEncoding.UTF_8;

@Controller
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    private static final String PDF_EXPORT_FORMAT = "pdf";
    private static final String XLS_EXPORT_FORMAT = "xls";

    @Autowired
    private ReportService reportService;

    @RequestMapping("/trainingStatusReports")
    @ResponseBody
    public List<TrainingStatusReportDto> getAllTrainingStatusReports() {
        return reportService.getAllTrainingStatusReports();
    }

    @RequestMapping("/wiseStatusReports")
    @ResponseBody
    public List<ProviderWiseStatusReportDto> getAllWiseStatusReports() {
        return reportService.getAllWiseStatusReports();
    }

    @RequestMapping(value = "/exportTrainingStatusReport", method = RequestMethod.GET)
    public void exportTrainingStatusReport(@RequestParam String outputFormat, HttpServletResponse response) throws IOException {
        TableWriter tableWriter = setResponseHeadersAndGetTableWriter(outputFormat, response, "TrainingStatusReport");
        reportService.exportTrainingStatusReport(tableWriter);
    }

    @RequestMapping(value = "/exportProviderWiseStatusReport", method = RequestMethod.GET)
    public void exportProviderWiseStatusReport(@RequestParam String outputFormat, HttpServletResponse response) throws IOException {
        TableWriter tableWriter = setResponseHeadersAndGetTableWriter(outputFormat, response, "ProviderWiseStatusReport");
        reportService.exportProviderWiseStatusReport(tableWriter);
    }

    @RequestMapping(value = "/exportProviderStatusDetailedReport", method = RequestMethod.GET)
    public void exportProviderStatusDetailedReport(@RequestParam String outputFormat, HttpServletResponse response) throws IOException {
        TableWriter tableWriter = setResponseHeadersAndGetTableWriter(outputFormat, response, "ProviderStatusDetailedReport");
        reportService.exportProviderStatusDetailedReport(tableWriter);
    }

    private TableWriter setResponseHeadersAndGetTableWriter(String outputFormat, HttpServletResponse response, String fileName) throws IOException {
        TableWriter tableWriter;
        if (PDF_EXPORT_FORMAT.equalsIgnoreCase(outputFormat)) {
            response.setContentType("application/pdf");
            tableWriter = new PdfTableWriter(response.getOutputStream());
        } else if (XLS_EXPORT_FORMAT.equalsIgnoreCase(outputFormat)) {
            response.setContentType("application/vnd.ms-excel");
            tableWriter = new ExcelTableWriter(response.getOutputStream());
        } else {
            throw new IllegalArgumentException("Invalid export format: " + outputFormat);
        }

        response.setCharacterEncoding(UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "." + outputFormat.toLowerCase());
        return tableWriter;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return e.getMessage();
    }
}
