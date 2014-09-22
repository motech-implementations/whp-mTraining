package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.domain.Provider;

public interface CourseProgressService {

    CourseProgress createCourseProgress(CourseProgress courseProgress);

    CourseProgress updateCourseProgress(CourseProgress courseProgress);

    CourseProgress getCourseProgressForProvider(long callerId);

    CourseProgress getCourseProgress(Provider provider);
}
