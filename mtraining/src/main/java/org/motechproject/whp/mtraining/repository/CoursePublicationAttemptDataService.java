package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursePublicationAttemptDataService extends MotechDataService<CoursePublicationAttempt> {

    @Lookup
    CoursePublicationAttempt findCoursePublicationAttemptById(@LookupField(name = "id") long id);

    @Lookup
    CoursePublicationAttempt findCoursePublicationAttemptByCourseId(@LookupField(name = "courseId") long courseId);
}
