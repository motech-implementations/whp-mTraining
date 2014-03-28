package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Course;
import org.springframework.stereotype.Repository;

import javax.jdo.FetchPlan;

@Repository
public class AllCourses extends RepositorySupport<Course> {

    @Override
    protected void configureFetchGroup(FetchPlan fetchPlan) {
        fetchPlan.setMaxFetchDepth(4);
        fetchPlan.setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
    }

    @Override
    Class getType() {
        return Course.class;
    }
}
