package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.domain.CourseProgress;

public interface CourseProgressService {

    CourseProgress createCourseProgress(CourseProgress courseProgress);

    CourseProgress updateCourseProgress(CourseProgress courseProgress);

    CourseProgress getCourseProgressForProvider(String externalId, ContentIdentifier courseIdentifier);

    CourseProgress getInitialCourseProgressForProvider(String externalId, ContentIdentifier courseIdentifier);
}
