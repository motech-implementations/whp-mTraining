package org.motechproject.whp.mtraining.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class AllCoursePublicationAttempts extends RepositorySupport<CoursePublicationAttempt> {

    @Override
    Class getType() {
        return CoursePublicationAttempt.class;
    }

    @Transactional
    public CoursePublicationAttempt byCourseId(UUID courseId) {
        return filterByField("courseId", UUID.class, courseId);
    }

    @Transactional
    public CoursePublicationAttempt getLastSuccessfulCoursePublicationAttempt() {
        List<CoursePublicationAttempt> coursePublicationAttemptsWithLatestFirst = allInOrder("version", "desc");
        CoursePublicationAttempt coursePublicationAttempt = (CoursePublicationAttempt) CollectionUtils.find(coursePublicationAttemptsWithLatestFirst, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CoursePublicationAttempt coursePublicationAttempt = (CoursePublicationAttempt) object;
                return coursePublicationAttempt.isPublishedToIvr();
            }
        });
        return coursePublicationAttempt;
    }
}
