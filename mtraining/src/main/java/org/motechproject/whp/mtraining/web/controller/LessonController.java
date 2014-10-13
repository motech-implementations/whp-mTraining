package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseUnitMetadataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
    CourseUnitMetadataValidator courseUnitMetadataValidator;

    @Autowired
    private MessageSource messageSource;

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
    public ResponseEntity<String> createLesson(@RequestBody LessonDto lesson) {
        if (!courseUnitMetadataValidator.isPresentInDb(lesson)) {
            dtoFactoryService.createOrUpdateFromDto(lesson);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<String>(messageSource.getMessage("mtraining.error.unitNotUnique",
                new String[] {lesson.getName()}, null), HttpStatus.CONFLICT);
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateLesson(@RequestBody LessonDto lesson) {
        if (!courseUnitMetadataValidator.isPresentInDb(lesson)) {
        	dtoFactoryService.updateCourseDto(lesson);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<String>(messageSource.getMessage("mtraining.error.unitNotUnique",
                new String[] {lesson.getName()}, null), HttpStatus.CONFLICT);
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
