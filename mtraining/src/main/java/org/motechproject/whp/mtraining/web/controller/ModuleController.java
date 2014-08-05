package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.repository.CourseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Course getModule(@PathVariable long moduleId) {
        return courseDataService.findCourseById(moduleId);
    }

    @RequestMapping(value = "/module", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Course createModule(@RequestBody Course course) {
        return courseDataService.create(course);
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public Course updateModule(@RequestBody Course course) {
        return courseDataService.update(course);
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.DELETE, consumes = "application/json")
    @ResponseBody
    public void removeModule(@RequestBody Course course) {
        courseDataService.delete(course);
    }

}
