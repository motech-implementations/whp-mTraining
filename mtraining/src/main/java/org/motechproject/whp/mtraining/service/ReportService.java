package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.TrainingStatusReportDto;

import java.util.List;

public interface ReportService {

    List<TrainingStatusReportDto> getAllTrainingStatusReports();
}
