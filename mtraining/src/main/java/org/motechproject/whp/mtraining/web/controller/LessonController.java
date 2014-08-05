package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.repository.LessonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public List<Lesson> getAllLessons(){
        return lessonDataService.retrieveAll();
    }

    @RequestMapping(value = "/lesson/remove", params = "id", method = RequestMethod.DELETE)
    @ResponseBody
    public String removeLesson(@RequestParam("id") long id){
        Lesson lesson = lessonDataService.findLessonById(id);
        if (lesson == null)
            return "Lesson with id="+id+" doesn't exist!";
        lessonDataService.delete(lesson);
        return "Removed lesson with id="+id+" successfully!";
    }
}
