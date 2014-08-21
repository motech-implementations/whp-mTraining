package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Contains API methods for JSON representation for Tree View
 */
@Controller
public class TreeViewController {

    @Autowired
    DtoFactoryService dtoFactoryService;

    @RequestMapping("/all")
    @ResponseBody
    public List<CoursePlanDto> getAllCoursesWithChildCollections() {
        return dtoFactoryService.getAllCourseDtosWithChildCollections();
    }


}
