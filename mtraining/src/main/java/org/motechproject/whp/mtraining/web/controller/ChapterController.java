package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseUnitMetadataValidator;
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
    CourseUnitMetadataValidator courseUnitMetadataValidator;

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
    public ResponseEntity<HttpStatus> createChapterDto(@RequestBody ChapterDto chapter) {
        if (courseUnitMetadataValidator.isPresentInDb(chapter)) {
            dtoFactoryService.createOrUpdateFromDto(chapter);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public  ResponseEntity<HttpStatus> updateChapterDto(@RequestBody ChapterDto chapter) {
        if (courseUnitMetadataValidator.isPresentInDb(chapter)) {
        	dtoFactoryService.updateCourseDto(chapter);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/chapter/{chapterId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeChapterDto(@PathVariable Long chapterId) {
        manyToManyRelationService.deleteRelationsById(chapterId);
        mTrainingService.deleteChapter(chapterId);
    }

    @RequestMapping(value = "/chapterByContentId/{contentId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ChapterDto getChapterByContentId(@PathVariable String contentId) {
        return (ChapterDto) dtoFactoryService.getDtoByContentId(contentId, ChapterDto.class);
    }

}
