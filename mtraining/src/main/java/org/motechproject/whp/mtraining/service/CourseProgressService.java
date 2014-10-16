package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.exception.CourseNotPublishedException;

public interface CourseProgressService {

    CourseProgress createCourseProgress(CourseProgress courseProgress);

    CourseProgress updateCourseProgress(CourseProgress courseProgress);

    CourseProgress getCourseProgressForProvider(long callerId);

    CourseProgress getCourseProgress(Provider provider) throws CourseNotPublishedException;

    void deleteCourseProgress(CourseProgress courseProgress);

    void markCourseAsComplete(long callerId, String startTime, String externalId);
}
