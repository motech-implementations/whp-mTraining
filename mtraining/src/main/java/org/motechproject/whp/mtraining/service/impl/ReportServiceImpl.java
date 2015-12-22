package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.whp.mtraining.dto.TrainingStatusReportDto;
import org.motechproject.whp.mtraining.repository.CourseProgressDataService;
import org.motechproject.whp.mtraining.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jdo.Query;
import java.util.List;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    private static final String TRAINING_STATUS_REPORT_QUERY = "select WHP_MTRAINING_LOCATION.district, count(WHP_MTRAINING_PROVIDER.location_id_OID) as 'providerRegistered', " +
            "SUM(if(WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'CLOSED' || WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'COMPLETED', 1, 0)) as 'providerCompletedCourse', " +
            "SUM(if(WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'STARTED' || WHP_MTRAINING_COURSEPROGRESS.courseStatus = 'ONGOING', 1, 0)) as 'providerInCourse' " +
            "from WHP_MTRAINING_LOCATION left join WHP_MTRAINING_PROVIDER on WHP_MTRAINING_LOCATION.id = WHP_MTRAINING_PROVIDER.location_id_OID " +
            "left join WHP_MTRAINING_COURSEPROGRESS on WHP_MTRAINING_PROVIDER.callerId = WHP_MTRAINING_COURSEPROGRESS.callerId group by WHP_MTRAINING_LOCATION.district";

    @Autowired
    private CourseProgressDataService courseProgressDataService;

    @Override
    public List<TrainingStatusReportDto> getAllTrainingStatusReports() {
        return courseProgressDataService.executeQuery(new QueryExecution<List<TrainingStatusReportDto>>() {

            @Override
            public List<TrainingStatusReportDto> execute(Query query, InstanceSecurityRestriction restriction) {
                Query q = query.getPersistenceManager().newQuery("javax.jdo.query.SQL", TRAINING_STATUS_REPORT_QUERY);
                q.setResultClass(TrainingStatusReportDto.class);

                return (List<TrainingStatusReportDto>) q.execute();
            }
        });
    }
}
