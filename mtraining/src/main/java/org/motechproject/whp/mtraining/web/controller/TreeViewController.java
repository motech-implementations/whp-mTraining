package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jdo.annotations.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains API methods for JSON representation for Tree View
 */
@Controller
public class TreeViewController {

    @Autowired
    DtoFactoryService dtoFactoryService;

    @Autowired
    ManyToManyRelationService manyToManyRelationService;

    @RequestMapping("/all")
    @ResponseBody
    public List<CoursePlanDto> getAllCoursesWithChildCollections() {
        return dtoFactoryService.getAllCourseDtosWithChildCollections();
    }

    @RequestMapping("/relations")
    @ResponseBody
    public List<ManyToManyRelation> getAllRelations() {
        return manyToManyRelationService.getAllRelations();
    }

    @Transactional
    @RequestMapping(value = "/updateRelations", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void updateAllRelations(@RequestBody ManyToManyRelation[] relations) {
        manyToManyRelationService.updateAll(Arrays.asList(relations));
    }




}
