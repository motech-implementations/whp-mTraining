package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.repository.CourseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
