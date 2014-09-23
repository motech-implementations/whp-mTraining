package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseStructureValidator;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
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

    @Autowired
    CourseStructureValidator courseStructureValidator;

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
    public ResponseStatus createChapterDto(@RequestBody ChapterDto chapter) {
        if (courseStructureValidator.isPresentInDb(chapter)) {
            dtoFactoryService.createOrUpdateFromDto(chapter);
            return ResponseStatus.OK;
        }
        return ResponseStatus.INVALID_CHAPTER;
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public  ResponseStatus updateChapterDto(@RequestBody ChapterDto chapter) {
        if (courseStructureValidator.isPresentInDb(chapter)) {
            dtoFactoryService.createOrUpdateFromDto(chapter);
            return ResponseStatus.OK;
        }
        return ResponseStatus.INVALID_CHAPTER;
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeChapterDto(@PathVariable Long chapterId) {
        manyToManyRelationService.deleteRelationsById(chapterId);
        mTrainingService.deleteChapter(chapterId);
    }

}
