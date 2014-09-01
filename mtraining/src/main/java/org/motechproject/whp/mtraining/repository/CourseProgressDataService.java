package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.CourseProgress;

public interface CourseProgressDataService extends MotechDataService<CourseProgress> {

    @Lookup
    CourseProgress findCourseProgressByExternalId(@LookupField(name = "externalId") String id);
}
