package org.motechproject.whp.mtraining.csv.validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.whp.mtraining.csv.domain.CallLogRecordType.QUESTION;
import static org.motechproject.whp.mtraining.csv.domain.CallLogRecordType.from;

@Component
public class CourseStructureValidator {
    private CourseService courseService;

    private static final String COURSE_NAME_NOT_FOUND = "Could not find the course name in the CSV. Please add the course details to CSV and try importing again.";
    private static final String MULTIPLE_COURSE_NODES_IN_CSV = "There are multiple course nodes in the CSV. Please ensure there is only 1 course node in the CSV and try importing again.";

    private Logger logger = LoggerFactory.getLogger(CourseStructureValidator.class);
    private String ACTIVE_STATUS = "active";
    private String INACTIVE_STATUS = "inactive";
    private String BLANK_STATUS = "";

    @Autowired
    public CourseStructureValidator(CourseService courseService) {
        this.courseService = courseService;
    }

    public List<CsvImportError> validate(List<CourseCsvRequest> requests) {
        List<CsvImportError> errors = new ArrayList<>();
        Set<String> parents = new HashSet<>();
        if (!requests.get(0).isCourse()) {
            CsvImportError error = new CsvImportError(COURSE_NAME_NOT_FOUND);
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
                CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), MULTIPLE_COURSE_NODES_IN_CSV);
                errors.add(error);
                logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
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
        validateStatus(request, errors);

        if (!request.hasFileName()) {
            CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "Missing audio filename. Please add the filename to CSV and try importing it again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
            return;
        }

        if (!request.isCourse()) {
            if (hasNoParent(request, errors)) {
                return;
            }
            if (hasInvalidParentName(request, requests, errors) || hasInvalidParentType(request, requests, errors)) {
                return;
            }
        }
        if (request.isChapter()) {
            try {
                Integer quizQuestions = isNotBlank(request.getNoOfQuizQuestions()) ? parseInt(request.getNoOfQuizQuestions()) : 0;
                if (quizQuestions > 0) {
                    Long passPercentage = isNotBlank(request.getPassPercentage()) ? parseLong(request.getPassPercentage()) : -1;
                    if (passPercentage < 0 || passPercentage > 100) {
                        CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "Specify the pass percentage between 0 and 100 for the chapter's quiz and try importing again.");
                        errors.add(error);
                        logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
                        return;
                    }
                    verifyQuestions(quizQuestions, requests, request, errors);
                }
                if (quizQuestions == 0 && questionsPresentForChapter(requests, request)) {
                    CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "The chapter has questions in the CSV but number of questions to be played in the quiz is not specified. Please specify the number of questions for the chapter and try again.");
                    errors.add(error);
                    logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
                    return;
                }
            } catch (NumberFormatException e) {
                CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "A Chapter should have valid no of questions and pass percentage between 0 and 100. Please try importing it again.");
                errors.add(error);
                logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
                return;
            }
        }

        if (!(request.isMessage() || request.isQuestion()) && (hasNoChild(request, parentNamesMap, errors) || hasNoActiveChild(request, requests, errors))) {
            return;
        }

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
        List<String> statusList = new ArrayList<String>(Arrays.asList(ACTIVE_STATUS, INACTIVE_STATUS, BLANK_STATUS)) {
        };
        if (!chapterRequest.isInActive()) {
            statusList.remove(INACTIVE_STATUS);
            CsvImportError errorForActiveChapter = new CsvImportError(chapterRequest.getNodeName(), chapterRequest.getNodeType(), "Number of active questions available in the CSV for this active chapter is less than number of quiz questions specified for the chapter. Please add more active questions for the chapter or  and try importing again.");
            addErrorIfNoOfQuestionsIsNotSufficient(quizQuestions, requests, chapterRequest, errors, statusList, errorForActiveChapter);
        } else {
            CsvImportError errorForInActiveChapter = new CsvImportError(chapterRequest.getNodeName(), chapterRequest.getNodeType(), "Number of questions available in the CSV for this chapter is less than number of quiz questions specified for the chapter. Please add more questions for the chapter and try importing again.");
            addErrorIfNoOfQuestionsIsNotSufficient(quizQuestions, requests, chapterRequest, errors, statusList, errorForInActiveChapter);
        }
    }

    private void addErrorIfNoOfQuestionsIsNotSufficient(Integer quizQuestions, List<CourseCsvRequest> requests, CourseCsvRequest chapterRequest, List<CsvImportError> errors, List<String> status, CsvImportError error) {
        int noOfQuestions = 0;
        for (CourseCsvRequest request : requests) {
            if (QUESTION.equals(from(request.getNodeType())) && chapterRequest.getNodeName().equalsIgnoreCase(request.getParentNode()) && status.contains(request.getStatus().toLowerCase()))
                noOfQuestions++;
        }
        if (noOfQuestions < quizQuestions) {
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
        }
    }

    private void validateOptionsAndAnswers(CourseCsvRequest request, List<CsvImportError> errors) {
        if (isEmpty(request.getOptionsAsList()) || !request.getOptionsAsList().contains(request.getCorrectAnswer())) {
            CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "A Question should contain options and correct answer should be one among the options. Please verify and try importing it again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
        }
        if (isBlank(request.getCorrectAnswerFileName())) {
            CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "A Question should have the name of the correct answer audio file. Please add the filename to CSV and try importing it again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
        }
    }

    private void validateStatus(CourseCsvRequest request, List<CsvImportError> errors) {
        if (request.isValidStatus())
            return;
        CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "Invalid status. Status should be either ACTIVE OR INACTIVE.");
        errors.add(error);
        logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
    }

    private boolean isInvalidNodeName(CourseCsvRequest request, List<CsvImportError> errors) {
        String nodeName = request.getNodeName();
        if (isBlank(nodeName)) {
            CsvImportError error = new CsvImportError("Name not specified. Please specify the node name and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error: %s", error.getMessage()));
            return true;
        }
        return request.isCourse() ? isInValidCourseName(nodeName, errors) : false;
    }

    private boolean isInValidCourseName(String nodeName, List<CsvImportError> errors) {
        List<CourseDto> existingCourses = courseService.getAllCourses();
        if (existingCourses.isEmpty() || StringUtils.equalsIgnoreCase(existingCourses.get(0).getName(), nodeName))
            return false;
        CsvImportError error = new CsvImportError(String.format("Course: %s already exists in database. You cannot import a new course.", existingCourses.get(0).getName()));
        errors.add(error);
        logger.info(String.format("Validation error: %s", error.getMessage()));
        return true;
    }

    private boolean hasNoChild(CourseCsvRequest courseStructureObject, Set<String> parentNamesMap, List<CsvImportError> errors) {
        if (!parentNamesMap.contains(courseStructureObject.getNodeName())) {
            String errorMessage = "A " + courseStructureObject.getNodeType().toLowerCase() + " should have at least one " + courseStructureObject.getChildNodeType().toLowerCase() + " under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.";
            errors.add(new CsvImportError(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
            logger.info(String.format("Validation error for node %s with node type %s: %s", courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
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
        errors.add(new CsvImportError(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
        logger.info(String.format("Validation error for node %s with node type %s: %s", courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), errorMessage));
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
            errors.add(new CsvImportError(request.getNodeName(), request.getNodeType(), errorMessage));
            logger.info(String.format("Validation error for node %s with node type %s: %s", request.getNodeName(), request.getNodeType(), errorMessage));
            return true;
        }
        return false;
    }

    private boolean hasNoParent(CourseCsvRequest courseStructureObject, List<CsvImportError> errors) {
        if (!courseStructureObject.hasParent()) {
            CsvImportError error = new CsvImportError(courseStructureObject.getNodeName(), courseStructureObject.getNodeType(), "All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
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
            CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "The parent node specified is of not of valid type. Please check the parent node name and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
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
            CsvImportError error = new CsvImportError(request.getNodeName(), request.getNodeType(), "Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.");
            errors.add(error);
            logger.info(String.format("Validation error for node %s with node type %s: %s", error.getNodeName(), error.getNodeType(), error.getMessage()));
            return true;
        }
        return false;
    }
}

