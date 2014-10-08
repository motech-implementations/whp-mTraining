package org.motechproject.whp.mtraining.web.controller;

import org.springframework.http.HttpStatus;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseUnitMetadataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    CourseUnitMetadataValidator courseUnitMetadataValidator;

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
    public ResponseEntity<HttpStatus> createCourse(@RequestBody CoursePlanDto coursePlanDto) {
        if (courseUnitMetadataValidator.isPresentInDb(coursePlanDto)) {
            dtoFactoryService.createOrUpdateFromDto(coursePlanDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<HttpStatus> updateCourse(@RequestBody CoursePlanDto coursePlanDto) {
        if (courseUnitMetadataValidator.isPresentInDb(coursePlanDto)) {
            dtoFactoryService.updateCourseDto(coursePlanDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeCourseDto(@PathVariable long courseId) {
        CoursePlan coursePlan = coursePlanService.getCoursePlanById(courseId);
        manyToManyRelationService.deleteRelationsById(courseId);
        coursePlanService.deleteCoursePlan(coursePlan);
    }

    @RequestMapping(value = "/courseByContentId/{contentId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public CoursePlanDto getCourseByContentId(@PathVariable String contentId) {
        return (CoursePlanDto) dtoFactoryService.getDtoByContentId(contentId, CoursePlanDto.class);
    }

}
