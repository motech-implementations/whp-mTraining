package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;

import java.util.List;

public interface CoursePublicationAttemptService {

    CoursePublicationAttempt createCoursePublicationAttempt(CoursePublicationAttempt coursePublicationAttempt);

    CoursePublicationAttempt updateCoursePublicationAttempt(CoursePublicationAttempt coursePublicationAttempt);

    void deleteCoursePublicationAttempt (CoursePublicationAttempt coursePublicationAttempt);

    List<CoursePublicationAttempt> getAllCoursePublicationAttempt();

    CoursePublicationAttempt getCoursePublicationAttemptById(long id);

    CoursePublicationAttempt getCoursePublicationAttemptByCourseId(long courseId);
}
