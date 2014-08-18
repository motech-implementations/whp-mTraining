package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public void createLesson(@RequestBody LessonDto lesson) { dtoFactoryService.createOrUpdateLessonFromDto(lesson); }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public void updateLesson(@RequestBody LessonDto lesson) {
        dtoFactoryService.createOrUpdateLessonFromDto(lesson);
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeLesson(@PathVariable Long lessonId) {
        manyToManyRelationService.deleteRelationsById(lessonId);
        mTrainingService.deleteLesson(lessonId);
    }

}
