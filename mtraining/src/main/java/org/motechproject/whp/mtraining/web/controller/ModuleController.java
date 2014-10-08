package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.ModuleDto;
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
 * Web API for Module
 */
@Controller
public class ModuleController {

    @Autowired
    MTrainingService mTrainingService;

    @Autowired
    DtoFactoryService dtoFactoryService;

    @Autowired
    ManyToManyRelationService manyToManyRelationService;

    @Autowired
    CourseUnitMetadataValidator courseUnitMetadataValidator;

    @RequestMapping("/modules")
    @ResponseBody
    public List<ModuleDto> getAllModules() {
        return dtoFactoryService.getAllModuleDtos();
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ModuleDto getModule(@PathVariable long moduleId) {
        return dtoFactoryService.getModuleDtoById(moduleId);
    }

    @RequestMapping(value = "/module", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<HttpStatus> createModule(@RequestBody ModuleDto module) {
        if (courseUnitMetadataValidator.isPresentInDb(module)) {
            dtoFactoryService.createOrUpdateFromDto(module);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<HttpStatus> updateModule(@RequestBody ModuleDto module) {
        if (courseUnitMetadataValidator.isPresentInDb(module)) {
        	dtoFactoryService.updateCourseDto(module);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeModule(@PathVariable Long moduleId) {
        manyToManyRelationService.deleteRelationsById(moduleId);
        mTrainingService.deleteCourse(moduleId);
    }

    @RequestMapping(value = "/moduleByContentId/{contentId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ModuleDto getModuleByContentId(@PathVariable String contentId) {
        return (ModuleDto) dtoFactoryService.getDtoByContentId(contentId, ModuleDto.class);
    }

}
