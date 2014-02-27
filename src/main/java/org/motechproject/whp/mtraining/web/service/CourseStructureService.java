package org.motechproject.whp.mtraining.web.service;

import org.motechproject.whp.mtraining.web.model.*;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseStructureService {
    private List<ErrorModel> errors;
    private Set<String> parentNamesMap;

    public List<ErrorModel> parseToCourseStructure(List<CourseStructureCsvRequest> courseStructureObjects) {
        errors = new ArrayList<>();
        parentNamesMap = new HashSet<>();
        Map<String, BaseModel> courseMap = new HashMap<String, BaseModel>();
        parentNamesMap = new HashSet<String>();
        for (CourseStructureCsvRequest courseStructureObject : courseStructureObjects) {
            if(courseStructureObject.getParentNode()!=null)
                parentNamesMap.add(courseStructureObject.getParentNode());
        }
        for (CourseStructureCsvRequest courseStructureObject : courseStructureObjects) {
            if(courseStructureObject.getNodeType().equalsIgnoreCase("course") && courseStructureObjects.indexOf(courseStructureObject)!=0 ){
                errors.add(new ErrorModel(courseStructureObject.getNodeName(),courseStructureObject.getNodeType(),"There are multiple course nodes in the CSV. Please ensure there is only course node in the CSV and try importing again."));
                return errors;
            }
            if (isValid(courseStructureObject, errors, courseMap,parentNamesMap))
                addToCourseStructure(courseStructureObject, courseMap, errors);
        }
        return errors;
    }

    public void addToCourseStructure(CourseStructureCsvRequest courseStructureObject, Map<String, BaseModel> courseMap, List<ErrorModel> errors) {
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
            default:
                //errors.put()
        }
        if (baseModel != null) {
            courseMap.put(courseStructureObject.getNodeName(), baseModel);

        }
    }

    private boolean isValid(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap, Set<String> parentNamesMap) {

        if(!courseStructureObject.getNodeType().equalsIgnoreCase("message")&& hasNoChild(courseStructureObject,parentNamesMap,errors))
            return false;
        if (nodeNameExists(courseStructureObject, errors, courseMap)) {
            return false;
        }
        if (!courseStructureObject.getNodeType().equalsIgnoreCase("course")) {
            if (hasNoParent(courseStructureObject, errors, courseMap)) {
                return false;
            }
            if (hasInvalidParentName(courseStructureObject, errors, courseMap)) {
                return false;
            }
            if (hasInvalidParentType(courseStructureObject, errors, courseMap)) {
                return false;
            }
        }
        if (courseStructureObject.getNodeType().equalsIgnoreCase("message") && hasInvalidFilenameForMessage(courseStructureObject, errors)) {
            return false;
        }
        return true;
    }

    private boolean hasNoChild(CourseStructureCsvRequest courseStructureObject, Set<String> parentNamesMap, List<ErrorModel> errors) {
        if(!parentNamesMap.contains(courseStructureObject.getNodeName())) {
            String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one " + NodeMapper.childToParentNameMap.get(courseStructureObject.getNodeType().toLowerCase()) + " under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.";
            errors.add(new ErrorModel(courseStructureObject.getNodeName(),courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean nodeNameExists(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (courseMap.containsKey(courseStructureObject.getNodeName())) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "There are 2 or more nodes with the same name: "+courseStructureObject.getNodeName()));
            return true;
        }
        return false;
    }

    private boolean hasNoParent(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (courseStructureObject.getParentNode() == null) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again."));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentType(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        BaseModel baseModel = courseMap.get(courseStructureObject.getParentNode());
        String actualClassName = baseModel.getClass().getName();
        String expectedClassName = NodeMapper.parentNameToChildCLassMap.get(courseStructureObject.getNodeType().toLowerCase());
        if(!expectedClassName.equalsIgnoreCase(actualClassName)) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(),courseStructureObject.getNodeType(),"Please check the parent node name for type and try importing again."));
            return true;
        }
        return false;
    }
    private boolean hasInvalidParentName(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        BaseModel parent = courseMap.get(courseStructureObject.getParentNode());
        if(parent==null) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "Please check the parent node name for spelling and try importing again."));
            return true;
        }
        return false;
    }

    private boolean hasInvalidFilenameForMessage(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors) {
        if (courseStructureObject.getFileName() == null) {
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "message should have file name"));
            return true;
        }
        return false;
    }
}

