package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.repository.ChapterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public List<Chapter> getAllChapters() {
        return chapterDataService.retrieveAll();
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Chapter getChapter(@PathVariable long chapterId) {
        return chapterDataService.findChapterById(chapterId);
    }

    @RequestMapping(value = "/chapter", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Chapter createChapter(@RequestBody Chapter chapter) {
        return chapterDataService.create(chapter);
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public Chapter updateChapter(@RequestBody Chapter chapter) {
        return chapterDataService.update(chapter);
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeChapter(@PathVariable long chapterId) {
        Chapter chapter = chapterDataService.findChapterById(chapterId);
        chapterDataService.delete(chapter);
    }

}
