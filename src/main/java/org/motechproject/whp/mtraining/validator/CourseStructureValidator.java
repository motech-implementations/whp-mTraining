package org.motechproject.whp.mtraining.validator;

import org.motechproject.whp.mtraining.web.model.BaseModel;
import org.motechproject.whp.mtraining.web.model.Chapter;
import org.motechproject.whp.mtraining.web.model.Course;
import org.motechproject.whp.mtraining.web.model.ErrorModel;
import org.motechproject.whp.mtraining.web.model.Message;
import org.motechproject.whp.mtraining.web.model.Module;
import org.motechproject.whp.mtraining.web.model.NodeMapper;
import org.motechproject.whp.mtraining.csv.CourseStructureCsvRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class CourseStructureValidator {
    private static final Logger LOG = LoggerFactory.getLogger(CourseStructureValidator.class);

    public List<ErrorModel> validate(List<CourseStructureCsvRequest> requests) {
        List<ErrorModel> errors = new ArrayList<>();
        Set<String> parents = new HashSet<>();
        Map<String, BaseModel> courseMap = new HashMap<>();
        if (!requests.get(0).isCourse()) {
            String errorMessage = "Could not find the course name in the CSV. Please add the course details to CSV and try importing again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(errorMessage));
            return errors;
        }
        for (CourseStructureCsvRequest courseStructureObject : requests) {
            if (courseStructureObject.hasParent()) {
                parents.add(courseStructureObject.getParentNode());
            }
        }
        for (CourseStructureCsvRequest courseStructureObject : requests) {
            if (courseStructureObject.isCourse() && requests.indexOf(courseStructureObject) != 0) {
                String errorMessage = "There are multiple course nodes in the CSV. Please ensure there is only 1 course node in the CSV and try importing again.";
                LOG.error(errorMessage);
                errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
                return errors;
            }
            if (isValid(courseStructureObject, errors, courseMap, parents))
                addToCourseStructure(courseStructureObject, courseMap);
        }
        if (errors.isEmpty())
            LOG.info("CSV file parsed successfully");
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
            String errorMessage = "A message should have the name of the audio file. Please add the filename to CSV and try importing it again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return false;
        }
        return true;
    }

    private boolean isNodeNameEmpty(String nodeName, String nodeType, List<ErrorModel> errors) {
        if (isBlank(nodeName)) {
            String errorMessage = "Name not specified. Please specify the node name and try importing again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasNoChild(CourseStructureCsvRequest courseStructureObject, Set<String> parentNamesMap, List<ErrorModel> errors) {
        if (!parentNamesMap.contains(courseStructureObject.getNodeName())) {
            String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one " + NodeMapper.childToParentNameMap.get(courseStructureObject.getNodeType().toLowerCase()) + " under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean isNodeNameADuplicate(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (courseMap.containsKey(courseStructureObject.getNodeName())) {
            String errorMessage = "There are 2 or more nodes with the same name: " + courseStructureObject.getNodeName() + ". Please ensure the nodes are named differently and try importing again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasNoParent(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (!courseStructureObject.hasParent()) {
            String errorMessage = "All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
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
            String errorMessage = "The parent node specified is of not of valid type. Please check the parent node name and try importing again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentName(CourseStructureCsvRequest courseStructureObject, List<ErrorModel> errors, Map<String, BaseModel> courseMap) {
        if (getErrorModelForANode(courseStructureObject.getParentNode(), errors) == null && !courseMap.containsKey(courseStructureObject.getParentNode())) {
            String errorMessage = "Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.";
            LOG.error(errorMessage);
            errors.add(new ErrorModel(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
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

