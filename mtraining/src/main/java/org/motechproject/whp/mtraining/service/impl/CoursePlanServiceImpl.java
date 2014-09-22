package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.repository.CoursePlanDataService;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("coursePlanService")
public class CoursePlanServiceImpl implements CoursePlanService {

    @Autowired
    private CoursePlanDataService coursePlanDataService;

    @Autowired
    private ContentOperationService contentOperationService;

    @Override
    public CoursePlan createCoursePlan(CoursePlan coursePlan) {
        return coursePlanDataService.create(coursePlan);
    }

    @Override
    public CoursePlan updateCoursePlan(CoursePlan coursePlan) {
        return coursePlanDataService.update(coursePlan);
    }

    @Override
    public void deleteCoursePlan(CoursePlan coursePlan) {
        coursePlanDataService.delete(coursePlan);
    }

    @Override
    public List<CoursePlan> getAllCoursePlans() {
        return coursePlanDataService.retrieveAll();
    }

    @Override
    public CoursePlan getCoursePlanById(long id) {
       return coursePlanDataService.findCoursePlanById(id);
    }

    @Override
    public CoursePlan getCoursePlanByName(String coursePlanName) {
        return coursePlanDataService.findCourseByName(coursePlanName);
    }

    @Override
    public CoursePlan getCoursePlanByExternalId(String externalId) {
        List<CoursePlan> coursePlans = getAllCoursePlans();
        for(CoursePlan coursePlan : coursePlans) {
            if (contentOperationService.getUuidFromJsonString(coursePlan.getContent()).toString().equalsIgnoreCase(externalId)) {
                return coursePlan;
            }
        }
        return null;
    }

    @Override
    public CoursePlan getCoursePlanByLocation(long locationId) {
        return coursePlanDataService.findCoursePlanByLocationId(locationId);
    }

}