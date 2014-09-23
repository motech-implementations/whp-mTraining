package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseStructureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    CourseStructureValidator courseStructureValidator;

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
        if (courseStructureValidator.isPresentInDb(module)) {
            dtoFactoryService.createOrUpdateFromDto(module);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<HttpStatus> updateModule(@RequestBody ModuleDto module) {
        if (courseStructureValidator.isPresentInDb(module)) {
            dtoFactoryService.createOrUpdateFromDto(module);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeModule(@PathVariable Long moduleId) {
        manyToManyRelationService.deleteRelationsById(moduleId);
        mTrainingService.deleteCourse(moduleId);
    }

}
