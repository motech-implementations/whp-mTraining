package org.motechproject.whp.mtraining.service.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.repository.CoursePublicationAttemptDataService;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("coursePublicationAttemptService")
public class CoursePublicationAttemptServiceImpl implements CoursePublicationAttemptService {

    @Autowired
    private CoursePublicationAttemptDataService coursePublicationAttemptDataService;

    @Override
    public CoursePublicationAttempt createCoursePublicationAttempt(CoursePublicationAttempt coursePublicationAttempt) {
        return coursePublicationAttemptDataService.create(coursePublicationAttempt);
    }

    @Override
    public CoursePublicationAttempt updateCoursePublicationAttempt(CoursePublicationAttempt coursePublicationAttempt) {
        return coursePublicationAttemptDataService.update(coursePublicationAttempt);
    }

    @Override
    public void deleteCoursePublicationAttempt(CoursePublicationAttempt coursePublicationAttempt) {
        coursePublicationAttemptDataService.delete(coursePublicationAttempt);
    }

    @Override
    public List<CoursePublicationAttempt> getAllCoursePublicationAttempt() {
        return coursePublicationAttemptDataService.retrieveAll();
    }

    @Override
    public CoursePublicationAttempt getCoursePublicationAttemptById(long id) {
        return coursePublicationAttemptDataService.findCoursePublicationAttemptById(id);
    }

    @Override
    public CoursePublicationAttempt getCoursePublicationAttemptByCourseId(long courseId) {
        return coursePublicationAttemptDataService.findCoursePublicationAttemptByCourseId(courseId);
    }

    @Override
    public CoursePublicationAttempt getLastSuccessfulCoursePublicationAttempt() {
        List<CoursePublicationAttempt> courses=coursePublicationAttemptDataService.retrieveAll();
        CoursePublicationAttempt last=null;
        for (CoursePublicationAttempt course : courses)
        {
            if (course.isPublishedToIvr())
            {
                if (last==null)
                {
                    last=course;
                    continue;
                }
                else
                
                {
                    DateTime d1=last.getCreationDate();
                    DateTime d2=course.getCreationDate();
                    if(DateTimeComparator.getInstance().compare(d1,d2)==-1)
                        last=course;
                }
            }
        }
        return last;
    }
}
