package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.CertificationCourse;
import org.springframework.stereotype.Repository;

import javax.jdo.FetchPlan;

@Repository
public class AllCertificationCourses extends RepositorySupport<CertificationCourse> {

    @Override
    protected void configureFetchGroup(FetchPlan fetchPlan) {
        fetchPlan.setMaxFetchDepth(4);
        fetchPlan.setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
    }

    @Override
    Class getType() {
        return CertificationCourse.class;
    }
}
