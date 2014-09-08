package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.domain.CourseProgress;

public interface CourseProgressService {

    CourseProgress createCourseProgress(CourseProgress courseProgress);

    CourseProgress updateCourseProgress(CourseProgress courseProgress);

    CourseProgress getCourseProgressForProvider(long callerId);

    CourseProgress getInitialCourseProgressForProvider(long callerId, ContentIdentifier courseIdentifier);
}
