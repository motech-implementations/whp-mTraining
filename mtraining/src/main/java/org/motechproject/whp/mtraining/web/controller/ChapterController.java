package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web API for ChapterDto
 */
@Controller
public class ChapterController {

    @Autowired
    MTrainingService mTrainingService;

    @Autowired
    DtoFactoryService dtoFactoryService;

    @RequestMapping("/chapters")
    @ResponseBody
    public List<ChapterDto> getAllChapterDtos() {
        return dtoFactoryService.getAllChapterDtos();
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ChapterDto getChapterDto(@PathVariable long chapterId) {
        return dtoFactoryService.getChapterDtoById(chapterId);
    }

    @RequestMapping(value = "/chapter", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void createChapterDto(@RequestBody ChapterDto chapter) {
        dtoFactoryService.createOrUpdateChapterFromDto(chapter);
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public  void updateChapterDto(@RequestBody ChapterDto chapter) {
        dtoFactoryService.createOrUpdateChapterFromDto(chapter);
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeChapterDto(@PathVariable long chapterId) {
        mTrainingService.deleteChapter(chapterId);
    }

}
