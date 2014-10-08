package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseStructureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Web API for Lesson
 */
@Controller
public class LessonController {

    @Autowired
    MTrainingService mTrainingService;

    @Autowired
    DtoFactoryService dtoFactoryService;

    @Autowired
    ManyToManyRelationService manyToManyRelationService;

    @Autowired
    CourseStructureValidator courseStructureValidator;

    @RequestMapping("/lessons")
    @ResponseBody
    public List<LessonDto> getAllLessons() {
        return dtoFactoryService.getAllLessonDtos();
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public LessonDto getLesson(@PathVariable long lessonId) {
        return dtoFactoryService.getLessonDtoById(lessonId);
    }

    @RequestMapping(value = "/lesson", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<HttpStatus> createLesson(@RequestBody LessonDto lesson) {
        if (courseStructureValidator.isPresentInDb(lesson)) {
            dtoFactoryService.createOrUpdateFromDto(lesson);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<HttpStatus> updateLesson(@RequestBody LessonDto lesson) {
        if (courseStructureValidator.isPresentInDb(lesson)) {
        	dtoFactoryService.updateCourseDto(lesson);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeLesson(@PathVariable Long lessonId) {
        manyToManyRelationService.deleteRelationsById(lessonId);
        mTrainingService.deleteLesson(lessonId);
    }
    
    @RequestMapping(value = "/lessonByContentId/{contentId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public LessonDto getLessonByContentId(@PathVariable String contentId) {
        return (LessonDto) dtoFactoryService.getDtoByContentId(contentId, LessonDto.class);
    }
    
}
