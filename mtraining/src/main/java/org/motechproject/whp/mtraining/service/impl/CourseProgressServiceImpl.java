package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.builder.CourseProgressUpdater;
import org.motechproject.whp.mtraining.constants.CourseStatus;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.domain.CourseConfiguration;
import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.repository.CourseProgressDataService;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.whp.mtraining.service.CourseProgressService;
import org.motechproject.whp.mtraining.service.FlagService;
import org.springframework.beans.factory.annotation.Autowired;

public class CourseProgressServiceImpl implements CourseProgressService {

    @Autowired
    CourseConfigurationService courseConfigurationService;

    @Autowired
    FlagService flagService;

    @Autowired
    CourseProgressService courseProgressService;
    
    @Autowired
    CourseProgressDataService courseProgressDataService;

    @Override
    public CourseProgress createCourseProgress(CourseProgress courseProgress) {
        
        return courseProgressDataService.create(courseProgress);
    }

    @Override
    public CourseProgress updateCourseProgress(CourseProgress courseProgress) {
        return courseProgressDataService.update(courseProgress);
    }

    /**
     * Return the course progress dto for the given courseId for the enrollee.
     * This API makes the assumption that at a given point of time an enrollee will be doing one course only.
     * The courseProgressDTO contains information about course progress as well as the current bookmark.
     * If the enrollee has course progress information for only courses that has been successfully completed by enrollee and certification released i.e the course
     * has been 'CLOSED' with respect to the enrollee,then null is returned.
     * Please have a look at @CourseStatus for more information.
     *
     * @param externalId
     * @param courseIdentifier
     * @return CourseProgressDto
     */
    @Override
    public CourseProgress getCourseProgressForProvider(String externalId, ContentIdentifier courseIdentifier) {
        CourseProgress courseProgress = courseProgressService.getCourseProgressForProvider(externalId, courseIdentifier);
        if (courseProgress != null) {
            Flag flag = flagService.getFlagByExternalId(externalId);
            if (flag == null) {
                return null;
            }
            CourseProgress curseProgress = new CourseProgress(externalId, courseProgress.getCourseStartTime(), flag, 0, courseProgress.getCourseStatus());
            if (courseProgress.isCourseClosed()) {
                return courseProgress;
            }
            setTimeLeftToCompleteCourse(flag.getCourseIdentifier().getUnitId(), courseProgress);
            return courseProgressService.updateCourseProgress(courseProgress);
        }
        return null;
    }

    /**
     * Returns the initial course progress DTO for a given course.
     * The DTO also contains the current bookmark
     *
     * @param externalId
     * @param courseIdentifier
     * @return CourseProgressDto
     */
    @Override
    public CourseProgress getInitialCourseProgressForProvider(String externalId, ContentIdentifier courseIdentifier) {
        Flag flag = flagService.getInitialFlag(externalId, courseIdentifier);
        CourseProgress courseProgress = new CourseProgress(externalId, null, flag, 0, CourseStatus.STARTED.value());
        setTimeLeftToCompleteCourse(flag.getCourseIdentifier().getUnitId(), courseProgress);
        return courseProgress;
    }

    private void setTimeLeftToCompleteCourse(long courseId, CourseProgress courseProgress) {
        CourseConfiguration courseConfig = courseConfigurationService.getCourseConfigurationByCourseId(courseId);
        if (courseConfig == null) {
            return;
        }
        courseProgress.setTimeLeftToCompleteCourse(courseConfig.getCourseDuration());
    }
}
