package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.ivr.CoursePublisher;
import org.motechproject.whp.mtraining.ivr.IVRResponse;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.jdo.annotations.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Contains API methods for JSON representation for Tree View
 */
@Controller
public class TreeViewController {

    @Autowired
    DtoFactoryService dtoFactoryService;

    @Autowired
    ManyToManyRelationService manyToManyRelationService;

    @Autowired
    CoursePublisher coursePublisher;

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

    @Transactional
    @RequestMapping(value = "/updateStates", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void updateAllStates(@RequestBody Map<String, String> stateMap) {
        for (Map.Entry<String, String> entry : stateMap.entrySet()) {
            dtoFactoryService.updateState(Long.valueOf(entry.getKey()), CourseUnitState.valueOf(entry.getValue()));
        }
    }

    @RequestMapping(value = "/publish/{courseId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public IVRResponse publishCourse(@PathVariable long courseId) {
        try {
            return coursePublisher.publish(courseId);
        } catch (Exception ex) {
            return new IVRResponse(500, ex.getMessage());
        }
    }

}
