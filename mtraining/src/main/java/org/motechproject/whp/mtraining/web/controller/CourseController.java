package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseStructureValidator;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Web API for CoursePlan
 */
@Controller
public class CourseController {

    @Autowired
    CoursePlanService coursePlanService;

    @Autowired
    DtoFactoryService dtoFactoryService;

    @Autowired
    ManyToManyRelationService manyToManyRelationService;

    @Autowired
    CourseStructureValidator courseStructureValidator;

    @RequestMapping("/courses")
    @ResponseBody
    public List<CoursePlanDto> getAllCourses() {
        return dtoFactoryService.getAllCoursePlanDtos();
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public CoursePlanDto getCourse(@PathVariable long courseId) {
        return dtoFactoryService.getCoursePlanDtoById(courseId);
    }

    @RequestMapping(value = "/course", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseStatus createCourse(@RequestBody CoursePlanDto coursePlanDto) {
        if (courseStructureValidator.isPresentInDb(coursePlanDto)) {
            dtoFactoryService.createOrUpdateFromDto(coursePlanDto);
            return ResponseStatus.OK;
        }
        return ResponseStatus.INVALID_COURSE;
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseStatus updateCourse(@RequestBody CoursePlanDto coursePlanDto) {
        if (courseStructureValidator.isPresentInDb(coursePlanDto)) {
            dtoFactoryService.createOrUpdateFromDto(coursePlanDto);
            return ResponseStatus.OK;
        }
        return ResponseStatus.INVALID_COURSE;
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeCourseDto(@PathVariable long courseId) {
        CoursePlan coursePlan = coursePlanService.getCoursePlanById(courseId);
        manyToManyRelationService.deleteRelationsById(courseId);
        coursePlanService.deleteCoursePlan(coursePlan);
    }

}
