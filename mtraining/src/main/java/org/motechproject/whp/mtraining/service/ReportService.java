package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.TrainingStatusReportDto;
import org.motechproject.whp.mtraining.util.TableWriter;

import java.io.IOException;
import java.util.List;

public interface ReportService {

    List<TrainingStatusReportDto> getAllTrainingStatusReports();

    void exportTrainingStatusReport(TableWriter tableWriter) throws IOException;

    void exportProviderWiseStatusReport(TableWriter tableWriter) throws IOException;

    void exportProviderStatusDetailedReport(TableWriter tableWriter) throws IOException;
}
