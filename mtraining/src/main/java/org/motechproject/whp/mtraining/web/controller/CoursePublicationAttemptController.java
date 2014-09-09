package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CoursePublicationAttemptController {

    @Autowired
    CoursePublicationAttemptService coursePublicationAttemptService;

    @RequestMapping("/coursePublicationAttempts")
    @ResponseBody
    public List<CoursePublicationAttempt> getAllCoursePublicationAttempts() {
        return coursePublicationAttemptService.getAllCoursePublicationAttempt();
    }

}
