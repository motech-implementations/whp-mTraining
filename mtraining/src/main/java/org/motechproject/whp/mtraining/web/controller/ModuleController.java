package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.repository.CourseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Web API for Module
 */
@Controller
public class ModuleController {

    @Autowired
    CourseDataService courseDataService;

    @RequestMapping("/modules")
    @ResponseBody
    public List<Course> getAllModules() {
        return courseDataService.retrieveAll();
    }

    @RequestMapping(value = "/module/remove", params = "id", method = RequestMethod.DELETE)
    @ResponseBody
    public String removeModule(@RequestParam("id") long id){
        Course course = courseDataService.findCourseById(id);
        if (course == null)
            return "Module with id="+id+" doesn't exist!";
        courseDataService.delete(course);
        return "Removed module with id="+id+" successfully!";
    }
}
