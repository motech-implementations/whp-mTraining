package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.ProviderStatusDetailedReportDto;
import org.motechproject.whp.mtraining.dto.ProviderWiseStatusReportDto;
import org.motechproject.whp.mtraining.dto.TrainingStatusReportDto;
import org.motechproject.whp.mtraining.util.TableWriter;

import java.io.IOException;
import java.util.List;

public interface ReportService {

    List<TrainingStatusReportDto> getAllTrainingStatusReports();

    List<ProviderWiseStatusReportDto> getAllWiseStatusReports();

    List<ProviderStatusDetailedReportDto> getAllStatusDetailedReports();

    void exportTrainingStatusReport(TableWriter tableWriter) throws IOException;

    void exportProviderWiseStatusReport(TableWriter tableWriter) throws IOException;

    void exportProviderStatusDetailedReport(TableWriter tableWriter) throws IOException;
}
