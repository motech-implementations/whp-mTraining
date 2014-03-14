package org.motechproject.whp.mtraining.csv.validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.request.CourseStructureCsvRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class CourseStructureValidator {
    private static final Logger LOG = LoggerFactory.getLogger(CourseStructureValidator.class);

    private static final String COURSE_NAME_NOT_FOUND = "Could not find the course name in the CSV. Please add the course details to CSV and try importing again.";
    private static final String MULTIPLE_COURSE_NODES_IN_CSV = "There are multiple course nodes in the CSV. Please ensure there is only 1 course node in the CSV and try importing again.";

    @Autowired
    private CourseService courseService;

    public List<CourseImportError> validate(List<CourseStructureCsvRequest> requests) {
        List<CourseImportError> errors = new ArrayList<>();
        Set<String> parents = new HashSet<>();
        if (!requests.get(0).isCourse()) {
            LOG.error(COURSE_NAME_NOT_FOUND);
            errors.add(new CourseImportError(COURSE_NAME_NOT_FOUND));
            return errors;
        }
        for (CourseStructureCsvRequest request : requests) {
            if (request.hasParent()) {
                parents.add(request.getParentNode());
            }
        }
        for (CourseStructureCsvRequest request : requests) {
            if (request.isCourse() && requests.indexOf(request) != 0) {
                LOG.error(MULTIPLE_COURSE_NODES_IN_CSV);
                errors.add(new CourseImportError(request.getNodeName(), request.getNodeType(), MULTIPLE_COURSE_NODES_IN_CSV));
                return errors;
            }
            validate(request, requests, errors, parents);
        }
        return errors;
    }

    private void validate(CourseStructureCsvRequest request, List<CourseStructureCsvRequest> requests, List<CourseImportError> errors, Set<String> parentNamesMap) {
        if (isInvalidNodeName(request, errors) || isNodeNameADuplicate(request, errors, requests)) {
            return;
        }
        validateStatus(request, errors);
        if (!request.isCourse()) {
            if (hasNoParent(request, errors)) {
                return;
            }
            if (hasInvalidParentName(request, requests, errors) || hasInvalidParentType(request, requests, errors)) {
                return;
            }
        }
        if (!request.isMessage() && hasNoChild(request, parentNamesMap, errors)) {
            return;
        }
        if (request.isMessage() && !request.hasFileName()) {
            String errorMessage = "A message should have the name of the audio file. Please add the filename to CSV and try importing it again.";
            LOG.error(errorMessage);
            errors.add(new CourseImportError(request.getNodeName(), request.getNodeType(), errorMessage));
        }
    }

    private void validateStatus(CourseStructureCsvRequest request, List<CourseImportError> errors) {
        if (request.isValidStatus())
            return;
        errors.add(new CourseImportError(request.getNodeName(), request.getNodeType(), "Invalid status. Status should be either ACTIVE OR INACTIVE or blank."));
    }

    private boolean isInvalidNodeName(CourseStructureCsvRequest request, List<CourseImportError> errors) {
        String nodeName = request.getNodeName();
        if (isBlank(nodeName)) {
            String errorMessage = "Name not specified. Please specify the node name and try importing again.";
            LOG.error(errorMessage);
            errors.add(new CourseImportError(errorMessage));
            return true;
        }
        return request.isCourse() ? isValidCourseName(nodeName, errors) : false;
    }

    private boolean isValidCourseName(String nodeName, List<CourseImportError> errors) {
        List<CourseDto> existingCourses = courseService.getAllCourses();
        if (existingCourses.isEmpty() || StringUtils.equalsIgnoreCase(existingCourses.get(0).getName(), nodeName))
            return true;
        errors.add(new CourseImportError("Invalid course name. Course already exists with different name. Please ensure that you are adding a couse with same name as existing course to update it"));
        return false;
    }

    private boolean hasNoChild(CourseStructureCsvRequest courseStructureObject, Set<String> parentNamesMap, List<CourseImportError> errors) {
        if (!parentNamesMap.contains(courseStructureObject.getNodeName())) {
            String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one " + courseStructureObject.getChildNodeType().toLowerCase() + " under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.";
            LOG.error(errorMessage);
            errors.add(new CourseImportError(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean isNodeNameADuplicate(final CourseStructureCsvRequest request, List<CourseImportError> errors, List<CourseStructureCsvRequest> requests) {
        boolean duplicateNodeNameExists = CollectionUtils.exists(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseStructureCsvRequest otherRequest = (CourseStructureCsvRequest) object;
                return !request.equals(otherRequest) && StringUtils.equals(request.getNodeName(), otherRequest.getNodeName());
            }
        });
        if (duplicateNodeNameExists) {
            String errorMessage = "There are 2 or more nodes with the same name: " + request.getNodeName() + ". Please ensure the nodes are named differently and try importing again.";
            LOG.error(errorMessage);
            errors.add(new CourseImportError(request.getNodeName(), request.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasNoParent(CourseStructureCsvRequest courseStructureObject, List<CourseImportError> errors) {
        if (!courseStructureObject.hasParent()) {
            String errorMessage = "All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.";
            LOG.error(errorMessage);
            errors.add(new CourseImportError(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentType(CourseStructureCsvRequest request, List<CourseStructureCsvRequest> requests, List<CourseImportError> errors) {
        final String parentNodeName = request.getParentNode();
        CourseStructureCsvRequest parentNode = (CourseStructureCsvRequest) CollectionUtils.find(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseStructureCsvRequest otherRequest = (CourseStructureCsvRequest) object;
                return StringUtils.equals(otherRequest.getNodeName(), parentNodeName);
            }
        });

        if (parentNode != null && !request.hasValidParentType(parentNode.getNodeType())) {
            String errorMessage = "The parent node specified is of not of valid type. Please check the parent node name and try importing again.";
            LOG.error(errorMessage);
            errors.add(new CourseImportError(request.getNodeName(), request.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentName(CourseStructureCsvRequest request, List<CourseStructureCsvRequest> requests, List<CourseImportError> errors) {
        final String parentNodeName = request.getParentNode();
        boolean parentNodeExists = CollectionUtils.exists(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseStructureCsvRequest otherRequest = (CourseStructureCsvRequest) object;
                return StringUtils.equals(otherRequest.getNodeName(), parentNodeName);
            }
        });

        if (!parentNodeExists) {
            String errorMessage = "Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.";
            LOG.error(errorMessage);
            errors.add(new CourseImportError(request.getNodeName(), request.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }
}

