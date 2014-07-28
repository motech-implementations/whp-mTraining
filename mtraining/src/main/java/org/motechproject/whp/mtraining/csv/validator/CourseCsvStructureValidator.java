package org.motechproject.whp.mtraining.csv.validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.whp.mtraining.csv.domain.CallLogRecordType.QUESTION;
import static org.motechproject.whp.mtraining.csv.domain.CallLogRecordType.from;

@Component
public class CourseCsvStructureValidator {
    private Logger logger = LoggerFactory.getLogger(CourseCsvStructureValidator.class);
    private String ACTIVE_STATUS = "active";
    private String INACTIVE_STATUS = "inactive";
    private String BLANK_STATUS = "";

    private MTrainingService courseService;

    @Autowired
    public CourseCsvStructureValidator(MTrainingService courseService) {
        this.courseService = courseService;
    }

    public List<CsvImportError> validate(List<CourseCsvRequest> requests) {
        List<CsvImportError> errors = new ArrayList<>();
        Set<String> parents = new HashSet<>();
        if (!requests.get(0).isCourse()) {
            CsvImportError error = new CsvImportError("Could not find the course name in the CSV. Please add the course details to CSV and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error: %s", error.getMessage()));
            return errors;
        }
        for (CourseCsvRequest request : requests) {
            if (request.hasParent()) {
                parents.add(request.getParentNode());
            }
        }
        for (CourseCsvRequest request : requests) {
            if (request.isCourse() && requests.indexOf(request) != 0) {
                createErrorResponse(request, errors, "There are multiple course nodes in the CSV. Please ensure there is only 1 course node in the CSV and try importing again.");
                return errors;
            }
            validate(request, requests, errors, parents);
        }
        return errors;
    }

    private void validate(CourseCsvRequest request, List<CourseCsvRequest> requests, List<CsvImportError> errors, Set<String> parentNamesMap) {
        if (isInvalidNodeName(request, errors) || isNodeNameADuplicate(request, errors, requests)) {
            return;
        }
        if (!request.isValidStatus())
            createErrorResponse(request, errors, "Invalid status. Status should be either ACTIVE OR INACTIVE.");

        if (!request.hasFileName())
            createErrorResponse(request, errors, "Missing audio filename. Please add the filename to CSV and try importing it again.");

        if (!request.isCourse()) {
            if (!request.hasParent())
                createErrorResponse(request, errors, "All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.");
            else if (hasInvalidParentName(request, requests, errors) || hasInvalidParentType(request, requests, errors))
                return;
        }
        if (request.isChapter()) {
            try {
                Integer quizQuestions = isNotBlank(request.getNoOfQuizQuestions()) ? parseInt(request.getNoOfQuizQuestions()) : 0;
                if (quizQuestions > 0) {
                    Long passPercentage = isNotBlank(request.getPassPercentage()) ? parseLong(request.getPassPercentage()) : -1;
                    if (passPercentage < 0 || passPercentage > 100) {
                        createErrorResponse(request, errors, "Specify the pass percentage between 0 and 100 for the chapter's quiz and try importing again.");
                        return;
                    }
                    verifyQuestions(quizQuestions, requests, request, errors);
                }
                if (quizQuestions == 0 && questionsPresentForChapter(requests, request))
                    createErrorResponse(request, errors, "The chapter has questions in the CSV but number of questions to be played in the quiz is not specified. Please specify the number of questions for the chapter and try again.");
            } catch (NumberFormatException e) {
                createErrorResponse(request, errors, "A Chapter should have valid no of questions and pass percentage between 0 and 100. Please try importing it again.");
            }
        }
        if (!(request.isMessage() || request.isQuestion()) && (hasNoChild(request, parentNamesMap, errors) || hasNoActiveChild(request, requests, errors)))
            return;

        if (request.isQuestion())
            validateOptionsAndAnswers(request, errors);
    }

    private boolean questionsPresentForChapter(List<CourseCsvRequest> requests, CourseCsvRequest chapterRequest) {
        for (CourseCsvRequest request : requests) {
            if (QUESTION.equals(from(request.getNodeType())) && request.getParentNode().equals(chapterRequest.getNodeName()))
                return true;
        }
        return false;
    }

    private void verifyQuestions(Integer quizQuestions, List<CourseCsvRequest> requests, CourseCsvRequest chapterRequest, List<CsvImportError> errors) {
        List<String> statusList = newArrayList(ACTIVE_STATUS, INACTIVE_STATUS, BLANK_STATUS);
        if (!chapterRequest.isInActive()) {
            statusList.remove(INACTIVE_STATUS);
            String errorMessage = "Number of active questions available in the CSV for this active chapter is less than number of quiz questions specified for the chapter." +
                    " Please add more active questions for the chapter or  and try importing again.";
            addErrorIfNoOfQuestionsIsNotSufficient(quizQuestions, requests, chapterRequest, errors, statusList, errorMessage);
        } else {
            String errorMessage = "Number of questions available in the CSV for this chapter is less than number of quiz questions specified for the chapter. " +
                    "Please add more questions for the chapter and try importing again.";
            addErrorIfNoOfQuestionsIsNotSufficient(quizQuestions, requests, chapterRequest, errors, statusList, errorMessage);
        }
    }

    private void addErrorIfNoOfQuestionsIsNotSufficient(Integer quizQuestions, List<CourseCsvRequest> requests, CourseCsvRequest chapterRequest, List<CsvImportError> errors, List<String> status, String errorMessage) {
        int noOfQuestions = 0;
        for (CourseCsvRequest request : requests) {
            if (QUESTION.equals(from(request.getNodeType())) && chapterRequest.getNodeName().equalsIgnoreCase(request.getParentNode()) && status.contains(request.getStatus()))
                noOfQuestions++;
        }
        if (noOfQuestions < quizQuestions) {
            createErrorResponse(chapterRequest, errors, errorMessage);
        }
    }

    private void validateOptionsAndAnswers(CourseCsvRequest request, List<CsvImportError> errors) {
        if (isEmpty(request.getOptionsAsList()) || !request.getOptionsAsList().contains(request.getCorrectAnswer()))
            createErrorResponse(request, errors, "A Question should contain options and correct answer should be one among the options. Please verify and try importing it again.");

        if (isBlank(request.getCorrectAnswerFileName())) {
            createErrorResponse(request, errors, "A Question should have the name of the correct answer audio file. Please add the filename to CSV and try importing it again.");
        }
    }

    private boolean hasNoChild(CourseCsvRequest courseStructureObject, Set<String> parentNamesMap, List<CsvImportError> errors) {
        if (!parentNamesMap.contains(courseStructureObject.getNodeName())) {
            String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one " + courseStructureObject.getChildNodeType().toLowerCase() + " under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.";
            createErrorResponse(courseStructureObject, errors, errorMessage);
            return true;
        }
        return false;
    }

    private boolean hasNoActiveChild(CourseCsvRequest courseStructureObject, List<CourseCsvRequest> requests, List<CsvImportError> errors) {
        int activeChildNumber = 0;
        if (courseStructureObject.isInActive())
            return false;
        for (CourseCsvRequest request : requests) {
            if (courseStructureObject.getNodeName().equalsIgnoreCase(request.getParentNode()) && !request.isInActive())
                activeChildNumber++;
        }
        if (activeChildNumber >= 1)
            return false;
        String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one active " + courseStructureObject.getChildNodeType().toLowerCase() + " under it. Please add active " + courseStructureObject.getChildNodeType().toLowerCase() + "s under the " + courseStructureObject.getNodeType().toLowerCase() + " or mark the " + courseStructureObject.getNodeType().toLowerCase() + " as inactive and try importing it again.";
        createErrorResponse(courseStructureObject, errors, errorMessage);
        return true;
    }

    private boolean isNodeNameADuplicate(final CourseCsvRequest request, List<CsvImportError> errors, List<CourseCsvRequest> requests) {
        boolean duplicateNodeNameExists = CollectionUtils.exists(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseCsvRequest otherRequest = (CourseCsvRequest) object;
                return !request.equals(otherRequest) && StringUtils.equals(request.getNodeName(), otherRequest.getNodeName());
            }
        });
        if (duplicateNodeNameExists) {
            String errorMessage = "There are 2 or more nodes with the same name: " + request.getNodeName() + ". Please ensure the nodes are named differently and try importing again.";
            createErrorResponse(request, errors, errorMessage);
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentType(CourseCsvRequest request, List<CourseCsvRequest> requests, List<CsvImportError> errors) {
        final String parentNodeName = request.getParentNode();
        CourseCsvRequest parentNode = (CourseCsvRequest) CollectionUtils.find(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseCsvRequest otherRequest = (CourseCsvRequest) object;
                return StringUtils.equals(otherRequest.getNodeName(), parentNodeName);
            }
        });
        if (parentNode != null && !request.hasValidParentType(parentNode.getNodeType())) {
            createErrorResponse(request, errors, "The parent node specified is of not of valid type. Please check the parent node name and try importing again.");
            return true;
        }
        return false;
    }

    private boolean hasInvalidParentName(CourseCsvRequest request, List<CourseCsvRequest> requests, List<CsvImportError> errors) {
        final String parentNodeName = request.getParentNode();
        boolean parentNodeExists = CollectionUtils.exists(requests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseCsvRequest otherRequest = (CourseCsvRequest) object;
                return StringUtils.equals(otherRequest.getNodeName(), parentNodeName);
            }
        });
        if (!parentNodeExists) {
            createErrorResponse(request, errors, "Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.");
            return true;
        }
        return false;
    }

    private boolean isInvalidNodeName(CourseCsvRequest request, List<CsvImportError> errors) {
        String nodeName = request.getNodeName();
        if (isBlank(nodeName)) {
            CsvImportError error = new CsvImportError("Name not specified. Please specify the node name and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error: %s", error.getMessage()));
            return true;
        }
        return request.isCourse() && isInValidCourseName(nodeName, errors);
    }

    private boolean isInValidCourseName(String nodeName, List<CsvImportError> errors) {
        List<Course> existingCourses = courseService.getCourseByName(nodeName);
        if (existingCourses.isEmpty() || equalsIgnoreCase(existingCourses.get(0).getName(), nodeName))
            return false;
        CsvImportError error = new CsvImportError(String.format("Course: %s already exists in database. You cannot import a new course.", existingCourses.get(0).getName()));
        errors.add(error);
        logger.info(String.format("Validation error: %s", error.getMessage()));
        return true;
    }

    private void createErrorResponse(CourseCsvRequest request, List<CsvImportError> errors, String errorMessage) {
        CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), errorMessage);
        errors.add(error);
        logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
    }
}
