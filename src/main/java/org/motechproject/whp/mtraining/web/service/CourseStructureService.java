package org.motechproject.whp.mtraining.web.service;

import org.motechproject.whp.mtraining.web.model.*;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class CourseStructureService {

    public List<ErrorModel> parseToCourseStructure(List<CourseStructureCsvRequest> courseStructureObjects) {
        List<ErrorModel> errors = new ArrayList<>();
        Set<String> parents = new HashSet<>();
        Map<String, BaseModel> courseMap = new HashMap<>();
        if (!courseStructureObjects.get(0).isCourse()) {
            errors.add(new ErrorModel("Could not find the course name in the CSV. Please add the course details to CSV and try importing again."));
            return errors;
        }
        for (CourseStructureCsvRequest courseStructureObject : courseStructureObjects) {
            if (courseStructureObject.hasParent()) {
                parents.add(courseStructureObject.getParentNode());
            }
        }
        for (CourseStructureCsvRequest courseStructureObject : courseStructureObjects) {
            if (courseStructureObject.isCourse() && courseStructureObjects.indexOf(courseStructureObject) != 0) {
                errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "There are multiple course nodes in the CSV. Please ensure there is only course node in the CSV and try importing again."));
                return errors;
            }
            if (isValid(courseStructureObject, errors, courseMap, parents))
                addToCourseStructure(courseStructureObject, courseMap);
        }
        return errors;
    }

    public void addToCourseStructure(CourseStructureCsvRequest courseStructureObject, Map<String, BaseModel> courseMap) {
        String baseModelType = courseStructureObject.getNodeType();
        BaseModel baseModel = null;
        switch (baseModelType.toLowerCase()) {
            case "course":
                baseModel = new Course(courseStructureObject.getNodeName(), courseStructureObject.getDescription(), courseStructureObject.getStatus());
                break;
            case "module":
                baseModel = new Module(courseStructureObject.getNodeName(), courseStructureObject.getDescription(), courseStructureObject.getStatus());
                break;
            case "chapter":
                baseModel = new Chapter(courseStructureObject.getNodeName(), courseStructureObject.getDescription(), courseStructureObject.getStatus());
                break;
            case "message":
                baseModel = new Message(courseStructureObject.getNodeName(), courseStructureObject.getDescription(), courseStructureObject.getStatus(), courseStructureObject.getFileName());
                break;
        }
        if (baseModel != null) {
            courseMap.put(courseStructureObject.getNodeName(), baseModel);

        }
    }

    private boolean isValid(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap, Set<String> parentNamesMap) {

        if (isNodeNameEmpty(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errors) || isNodeNameADuplicate(courseStructureObject, errors, courseMap)) {
            return false;
        }
        if (!courseStructureObject.isCourse()) {
            if (hasNoParent(courseStructureObject, errors, courseMap)) {
                return false;
            }
            if (hasInvalidParentName(courseStructureObject, errors, courseMap) || hasInvalidParentType(courseStructureObject, errors, courseMap)) {
                return false;
            }
        }
        if (!courseStructureObject.isMessage() && hasNoChild(courseStructureObject, parentNamesMap, errors)) {
            return false;
        }
        if (courseStructureObject.isMessage() && !courseStructureObject.hasFileName()) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "A message should have the name of the audio file. Please add the filename to CSV and try importing it again."));
            return false;
        }
        return true;
    }

    private boolean isNodeNameEmpty(String nodeName, String nodeType, List<ErrorModel> errors) {
        if (isBlank(nodeName)) {
            errors.add(new ErrorModel(nodeName, nodeType, "Name not specified."));
            return true;
        }
        return false;
    }

    private boolean hasNoChild(CourseStructureCsvRequest courseStructureObject, Set<String> parentNamesMap, List<ErrorModel> errors) {
        if (!parentNamesMap.contains(courseStructureObject.getNodeName())) {
            String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one " + NodeMapper.childToParentNameMap.get(courseStructureObject.getNodeType().toLowerCase()) + " under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.";
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean isNodeNameADuplicate(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (courseMap.containsKey(courseStructureObject.getNodeName())) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "There are 2 or more nodes with the same name: " + courseStructureObject.getNodeName() + ". Please ensure the nodes are named differently and try importing again."));
            return true;
        }
        return false;
    }

    private boolean hasNoParent(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (!courseStructureObject.hasParent()) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again."));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentType(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        BaseModel baseModel = courseMap.get(courseStructureObject.getParentNode());
        if (baseModel == null)
            return false;
        String actualClassName = baseModel.getClass().getName();
        String expectedClassName = NodeMapper.parentNameToChildCLassMap.get(courseStructureObject.getNodeType().toLowerCase());
        if (!expectedClassName.equalsIgnoreCase(actualClassName)) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "The parent node specified is of not of valid type. Please check the parent node name and try importing again."));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentName(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (getErrorModelForANode(courseStructureObject.getParentNode(), errors) == null && !courseMap.containsKey(courseStructureObject.getParentNode())) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again."));
            return true;
        }
        return false;
    }

    private ErrorModel getErrorModelForANode(String nodeName, List<ErrorModel> errors) {
        for (ErrorModel error : errors) {
            if (error.getNodeName().equalsIgnoreCase(nodeName))
                return error;

        }
        return null;
    }


}

