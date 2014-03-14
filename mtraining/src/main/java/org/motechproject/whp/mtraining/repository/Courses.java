package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Course;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class Courses extends RepositorySupport<Course> {

    @Override
    Class getType() {
        return Course.class;
    }

    @Transactional
    public Course byCourseId(UUID courseId) {
        return filterByField("courseId", UUID.class, courseId);
    }

    @Transactional
    public Course getLatestCourse() {
        return allInOrder("version", "desc").get(0);
    }
}
