package org.motechproject.whp.mtraining.web.controller;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
    @RequestMapping(value = "/updateRelations/{courseId}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void updateRelations(@RequestBody String jsonString, @PathVariable long courseId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        List<ManyToManyRelation> relations = mapper.convertValue(node.get("relations"), new TypeReference<List<ManyToManyRelation>>(){});
        List<Long> updatedIds = mapper.convertValue(node.get("updatedIds"), new TypeReference<List<Long>>(){});
        manyToManyRelationService.updateRelationsForCourse(relations, courseId, updatedIds);
    }

    @Transactional
    @RequestMapping(value = "/updateStates", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public List<Long> updateAllStates(@RequestBody Map<String, String> stateMap) {
        return dtoFactoryService.updateStates(stateMap);
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
