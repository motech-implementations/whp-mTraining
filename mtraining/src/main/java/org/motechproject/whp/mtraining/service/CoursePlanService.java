package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.CoursePlan;

import java.util.List;

public interface CoursePlanService {

    CoursePlan createCoursePlan(CoursePlan CoursePlan);

    CoursePlan updateCoursePlan(CoursePlan CoursePlan);

    void deleteCoursePlan(CoursePlan CoursePlan);

    List<CoursePlan> getAllCoursePlans();

    CoursePlan getCoursePlanById(long id);

    CoursePlan getCoursePlanByName(String coursePlanName);

    CoursePlan getCoursePlanByExternalId(String externalId);

    CoursePlan getCoursePlanByLocation(long locationId);

}