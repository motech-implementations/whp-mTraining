package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.validator.CourseStructureValidator;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseStatus createModule(@RequestBody ModuleDto module) {
        if (courseStructureValidator.isPresentInDb(module)) {
            dtoFactoryService.createOrUpdateFromDto(module);
            return ResponseStatus.OK;
        }
        return ResponseStatus.INVALID_MODULE;
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseStatus updateModule(@RequestBody ModuleDto module) {
        if (courseStructureValidator.isPresentInDb(module)) {
            dtoFactoryService.createOrUpdateFromDto(module);
            return ResponseStatus.OK;
        }
        return ResponseStatus.INVALID_MODULE;
    }

    @RequestMapping(value = "/module/{moduleId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeModule(@PathVariable Long moduleId) {
        manyToManyRelationService.deleteRelationsById(moduleId);
        mTrainingService.deleteCourse(moduleId);
    }

}
