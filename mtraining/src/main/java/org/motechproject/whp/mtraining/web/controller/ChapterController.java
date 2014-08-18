package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
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

    @Autowired
    ManyToManyRelationService manyToManyRelationService;

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
    public void removeChapterDto(@PathVariable Long chapterId) {
        manyToManyRelationService.deleteRelationsById(chapterId);
        mTrainingService.deleteChapter(chapterId);
    }

}
