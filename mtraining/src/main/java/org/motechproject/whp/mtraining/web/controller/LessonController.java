package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.repository.LessonDataService;
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
    LessonDataService lessonDataService;

    @RequestMapping("/lessons")
    @ResponseBody
    public List<Lesson> getAllLessons() {
        return lessonDataService.retrieveAll();
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Lesson getLesson(@PathVariable long lessonId) {
        return lessonDataService.findLessonById(lessonId);
    }

    @RequestMapping(value = "/lesson", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Lesson createLesson(@RequestBody Lesson lesson) {
        return lessonDataService.create(lesson);
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public Lesson updateLesson(@RequestBody Lesson lesson) {
        return lessonDataService.update(lesson);
    }

    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.DELETE, consumes = "application/json")
    @ResponseBody
    public void removeLesson(@RequestBody Lesson lesson) {
        lessonDataService.delete(lesson);
    }

}
