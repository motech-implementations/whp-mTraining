package org.motechproject.whp.mtraining.csv.validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.request.CsvRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class CourseStructureValidator {
    private CourseService courseService;

    private static final String COURSE_NAME_NOT_FOUND = "Could not find the course name in the CSV. Please add the course details to CSV and try importing again.";
    private static final String MULTIPLE_COURSE_NODES_IN_CSV = "There are multiple course nodes in the CSV. Please ensure there is only 1 course node in the CSV and try importing again.";

    private Logger logger = LoggerFactory.getLogger(CourseStructureValidator.class);

    @Autowired
    public CourseStructureValidator(CourseService courseService) {
        this.courseService = courseService;
    }

    public List<CourseImportError> validate(List<CsvRequest> requests) {
        List<CourseImportError> errors = new ArrayList<>();
        Set<String> parents = new HashSet<>();
        if (!requests.get(0).isCourse()) {
            CourseImportError error = new CourseImportError(COURSE_NAME_NOT_FOUND);
            errors.add(error);
            logger.info(String.format("Validation error: %s", error.getMessage()));
            return errors;
        }
        for (CsvRequest request : requests) {
            if (request.hasParent()) {
                parents.add(request.getParentNode());
            }
        }
        for (CsvRequest request : requests) {
            if (request.isCourse() && requests.indexOf(request) != 0) {
                CourseImportError error = new CourseImportError(request.getNodeName(), request.getNodeType(), MULTIPLE_COURSE_NODES_IN_CSV);
                errors.add(error);
                logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
                return errors;
            }
            validate(request, requests, errors, parents);
        }
        return errors;
    }

    private void validate(CsvRequest request, List<CsvRequest> requests, List<CourseImportError> errors, Set<String> parentNamesMap) {
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
        if (!(request.isMessage() || request.isQuestion()) && hasNoChild(request, parentNamesMap, errors)) {
            return;
        }
        if ((request.isMessage() || request.isQuestion()) && !request.hasFileName()) {
            CourseImportError error = new CourseImportError(request.getNodeName(), request.getNodeType(), "A message and question should have the name of the audio file. Please add the filename to CSV and try importing it again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
            return;
        }
        if (request.isQuestion())
            validateOptionsAndAnswers(request, errors);
    }

    private void validateOptionsAndAnswers(CsvRequest request, List<CourseImportError> errors) {
        if (isEmpty(request.getOptionsAsList()) || !request.getOptionsAsList().contains(request.getCorrectAnswer())) {
            CourseImportError error = new CourseImportError(request.getNodeName(), request.getNodeType(), "A Question should contain options and correct answer should be one among the options. Please verify and try importing it again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
        }
        if (isBlank(request.getCorrectAnswerFileName())) {
            CourseImportError error = new CourseImportError(request.getNodeName(), request.getNodeType(), "A Question should have the name of the correct answer audio file. Please add the filename to CSV and try importing it again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
        }
    }

    private void validateStatus(CsvRequest request, List<CourseImportError> errors) {
        if (request.isValidStatus())
            return;
        CourseImportError error = new CourseImportError(request.getNodeName(), request.getNodeType(), "Invalid status. Status should be either ACTIVE OR INACTIVE.");
        errors.add(error);
        logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
    }

    private boolean isInvalidNodeName(CsvRequest request, List<CourseImportError> errors) {
        String nodeName = request.getNodeName();
        if (isBlank(nodeName)) {
            CourseImportError error = new CourseImportError("Name not specified. Please specify the node name and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error: %s", error.getMessage()));
            return true;
        }
        return request.isCourse() ? isValidCourseName(nodeName, errors) : false;
    }

    private boolean isValidCourseName(String nodeName, List<CourseImportError> errors) {
        List<CourseDto> existingCourses = courseService.getAllCourses();
        if (existingCourses.isEmpty() || StringUtils.equalsIgnoreCase(existingCourses.get(0).getName(), nodeName))
            return true;
        CourseImportError error = new CourseImportError(String.format("Course: %s already exists in database. You cannot import a new course.", existingCourses.get(0).getName()));
        errors.add(error);
        logger.info(String.format("Validation error: %s", error.getMessage()));
        return false;
    }

    private boolean hasNoChild(CsvRequest courseStructureObject, Set<String> parentNamesMap, List<CourseImportError> errors) {
        if (!parentNamesMap.contains(courseStructureObject.getNodeName())) {
            String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one " + courseStructureObject.getChildNodeType().toLowerCase() + " under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.";
            errors.add(new CourseImportError(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            logger.info(String.format("Validation error for node %s with node type %s: %s", courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean isNodeNameADuplicate(final CsvRequest request, List<CourseImportError> errors, List<CsvRequest> requests) {
        boolean duplicateNodeNameExists = CollectionUtils.exists(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CsvRequest otherRequest = (CsvRequest) object;
                return !request.equals(otherRequest) && StringUtils.equals(request.getNodeName(), otherRequest.getNodeName());
            }
        });
        if (duplicateNodeNameExists) {
            String errorMessage = "There are 2 or more nodes with the same name: " + request.getNodeName() + ". Please ensure the nodes are named differently and try importing again.";
            errors.add(new CourseImportError(request.getNodeName(), request.getNodeType(), errorMessage));
            logger.info(String.format("Validation error for node %s with node type %s: %s", request.getNodeName(), request.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasNoParent(CsvRequest courseStructureObject, List<CourseImportError> errors) {
        if (!courseStructureObject.hasParent()) {
            CourseImportError error = new CourseImportError(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentType(CsvRequest request, List<CsvRequest> requests, List<CourseImportError> errors) {
        final String parentNodeName = request.getParentNode();
        CsvRequest parentNode = (CsvRequest) CollectionUtils.find(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CsvRequest otherRequest = (CsvRequest) object;
                return StringUtils.equals(otherRequest.getNodeName(), parentNodeName);
            }
        });

        if (parentNode != null && !request.hasValidParentType(parentNode.getNodeType())) {
            CourseImportError error = new CourseImportError(request.getNodeName(), request.getNodeType(), "The parent node specified is of not of valid type. Please check the parent node name and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentName(CsvRequest request, List<CsvRequest> requests, List<CourseImportError> errors) {
        final String parentNodeName = request.getParentNode();
        boolean parentNodeExists = CollectionUtils.exists(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CsvRequest otherRequest = (CsvRequest) object;
                return StringUtils.equals(otherRequest.getNodeName(), parentNodeName);
            }
        });

        if (!parentNodeExists) {
            CourseImportError error = new CourseImportError(request.getNodeName(), request.getNodeType(), "Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
            return true;
        }
        return false;
    }
}

