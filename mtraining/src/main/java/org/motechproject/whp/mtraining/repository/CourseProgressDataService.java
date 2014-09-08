package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.CourseProgress;

import java.util.List;

public interface CourseProgressDataService extends MotechDataService<CourseProgress> {

    @Lookup
    List<CourseProgress> findCourseProgressesByCallerId(@LookupField(name = "callerId") long callerId);
}
