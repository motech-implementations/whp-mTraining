//package org.motechproject.whp.mtraining.validator;
//
//import org.apache.commons.lang.StringUtils;
//import org.motechproject.mtraining.service.MTrainingService;
//import org.motechproject.whp.mtraining.domain.Content;
//import org.motechproject.whp.mtraining.dto.*;
//import org.motechproject.whp.mtraining.repository.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * Validator for validating fields of different DTOs which represent the course structure:
// * {@link CourseDto}
// * {@link ModuleDto}
// * {@link ChapterDto}
// * {@link MessageDto}
// */
//
//@Component
//public class CourseStructureValidator {
//    private static Logger logger = LoggerFactory.getLogger(CourseStructureValidator.class);
//
//    @Autowired
//    private MTrainingService mTrainingService;
//
//    public CourseStructureValidationResponse validateQuestion(QuestionDto question) {
//        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
//        if (question == null) {
//            validationResponse.addError("Question should not be null");
//            return validationResponse;
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Validating Question: %s", question.getName()));
//        }
//        validateName(question.getName(), validationResponse, "Question name should not be blank");
//        validateExternalId(question.getExternalContentId(), validationResponse);
//        //validateIfContentExists(question.getContentId(), allQuestions, validationResponse, "Question does not exist for given contentId");
//
//        return validationResponse;
//    }
//
//    public CourseStructureValidationResponse validateQuiz(QuizDto quiz) {
//        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
//        if (quiz == null) {
//            validationResponse.addError("Quiz should not be null");
//            return validationResponse;
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Validating Quiz"));
//        }
//        if (quiz.getNoOfQuestionsToBePlayed() == 0) {
//            validationResponse.addError("No of questions to be played should not be 0 for a quiz");
//        }
//        validateIfContentExists(quiz.getContentId(), mTrainingService.getQuizForChapter(quiz.getContentId()), validationResponse, "Quiz does not exist for given contentId");
//        return validationResponse;
//    }
//
//    public CourseStructureValidationResponse validateMessage(MessageDto message) {
//        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
//        if (message == null) {
//            validationResponse.addError("Message should not be null");
//            return validationResponse;
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Validating Message: %s", message.getName()));
//        }
//        validateName(message.getName(), validationResponse, "Message name should not be blank");
//        validateExternalId(message.getExternalContentId(), validationResponse);
//        validateIfContentExists(message.getContentId(), allMessages, validationResponse, "Message does not exist for given contentId");
//
//        return validationResponse;
//    }
//
//    public CourseStructureValidationResponse validateChapter(ChapterDto chapter) {
//        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
//        if (chapter == null) {
//            validationResponse.addError("Chapter should not be null");
//            return validationResponse;
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Validating Chapter: %s", chapter.getName()));
//        }
//        validateName(chapter.getName(), validationResponse, "Chapter name should not be blank");
//        validateIfContentExists(chapter.getContentId(), allChapters, validationResponse, "Chapter does not exist for given contentId");
//
//        return validationResponse;
//    }
//
//    public CourseStructureValidationResponse validateModule(ModuleDto module) {
//        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
//        if (module == null) {
//            validationResponse.addError("Module should not be null");
//            return validationResponse;
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Validating Module: %s", module.getName()));
//        }
//        validateName(module.getName(), validationResponse, "Module name should not be blank");
//        // TODO
//        //validateIfContentExists(module.getContentId(), allModules, validationResponse, "Module does not exist for given contentId");
//
//        return validationResponse;
//    }
//
//    public CourseStructureValidationResponse validateCourse(CourseDto course) {
//        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
//        if (course == null) {
//            validationResponse.addError("Course should not be null");
//            return validationResponse;
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Validating Course: %s", course.getName()));
//        }
//        validateName(course.getName(), validationResponse, "Course name should not be blank");
//        // TODO
//        //validateIfContentExists(course.getContentId(), allCourses, validationResponse, "Course does not exist for given contentId");
//
//        return validationResponse;
//    }
//
//    private void validateName(String chapterName, CourseStructureValidationResponse validationResponse, String errorMessage) {
//        if (StringUtils.isBlank(chapterName)) {
//            validationResponse.addError(errorMessage);
//        }
//    }
//
//    private void validateExternalId(String fileName, CourseStructureValidationResponse validationResponse) {
//        if (StringUtils.isBlank(fileName)) {
//            validationResponse.addError("ExternalId should not be blank for a message");
//        }
//    }
//
//    private <T extends Content> void validateIfContentExists(UUID contentId, AllContents<T> allContents, CourseStructureValidationResponse validationResponse, String errorMessage) {
//        if (contentId == null) {
//            return;
//        }
//        List<T> existingCourses = allContents.findByContentId(contentId);
//        if (existingCourses.isEmpty()) {
//            validationResponse.addError(errorMessage + ": " + contentId);
//        }
//    }
//}
