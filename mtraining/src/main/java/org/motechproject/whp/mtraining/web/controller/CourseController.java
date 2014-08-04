package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Web API for CoursePlan
 */
@Controller
public class CourseController {

    @Autowired
    CoursePlanService coursePlanService;

    @RequestMapping("/courses")
    @ResponseBody
    public List<CoursePlan> getAllCourses() {
        return coursePlanService.getAllCoursePlans();
    }
}
