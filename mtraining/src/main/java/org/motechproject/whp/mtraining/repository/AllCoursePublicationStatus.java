package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.CoursePublicationStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class AllCoursePublicationStatus extends RepositorySupport<CoursePublicationStatus> {

    @Override
    Class getType() {
        return CoursePublicationStatus.class;
    }

    @Transactional
    public CoursePublicationStatus byCourseId(UUID courseId) {
        return filterByField("courseId", UUID.class, courseId);
    }

    @Transactional
    public CoursePublicationStatus getLatestCoursePublicationStatus() {
        return allInOrder("version", "desc").get(0);
    }
}
