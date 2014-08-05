package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(value = "/course/remove", params = "id", method = RequestMethod.DELETE)
    @ResponseBody
    public String removeCourse(@RequestParam("id") long id){
        CoursePlan coursePlan = coursePlanService.getCoursePlanById(id);
        if (coursePlan == null)
            return "Course with id="+id+" doesn't exist!";
        coursePlanService.deleteCoursePlan(coursePlan);
        return "Removed course with id="+id+" successfully!";
    }
}
