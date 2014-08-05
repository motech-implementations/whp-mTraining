package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.repository.ChapterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Web API for Chapter
 */
@Controller
public class ChapterController {

    @Autowired
    ChapterDataService chapterDataService;

    @RequestMapping("/chapters")
    @ResponseBody
    public List<Chapter> getAllChapters(){
        return chapterDataService.retrieveAll();
    }

    @RequestMapping(value = "/chapter/remove", params = "id", method = RequestMethod.DELETE)
    @ResponseBody
    public String removeChapter(@RequestParam("id") long id){
        Chapter chapter = chapterDataService.findChapterById(id);
        if (chapter == null)
            return "Chapter with id="+id+" doesn't exist!";
        chapterDataService.delete(chapter);
        return "Removed chapter with id="+id+" successfully!";
    }
}
