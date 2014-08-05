package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public CoursePlan getCourse(@PathVariable long courseId) {
        return coursePlanService.getCoursePlanById(courseId);
    }

    @RequestMapping(value = "/course", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CoursePlan createCourse(@RequestBody CoursePlan coursePlan) {
        return coursePlanService.createCoursePlan(coursePlan);
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public CoursePlan updateCourse(@RequestBody CoursePlan coursePlan) {
        return coursePlanService.updateCoursePlan(coursePlan);
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.DELETE, consumes = "application/json")
    @ResponseBody
    public void removeCourse(@RequestBody CoursePlan coursePlan) {
        coursePlanService.deleteCoursePlan(coursePlan);
    }

}
