package org.motechproject.whp.mtraining.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.mtraining.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* Validator for validating fields which represent the course structure:
* {@link Course}
* {@link Chapter}
* {@link Lesson}
*/

@Component
public class CourseStructureValidator {
    private static Logger logger = LoggerFactory.getLogger(CourseStructureValidator.class);

    @Autowired
    private MTrainingService mTrainingService;

    public CourseStructureValidationResponse validateQuiz(Quiz quiz) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (quiz == null) {
            validationResponse.addError("Quiz should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Quiz"));
        }
        if (quiz.getQuestions().size() == 0) {
            validationResponse.addError("No of questions should not be 0 for a quiz");
        }
        if (mTrainingService.getQuizById(quiz.getId()) == null){
            validationResponse.addError("Quiz does not exist for given Id: " + quiz.getId());
        }
        return validationResponse;
    }

    public CourseStructureValidationResponse validateLesson(Lesson lesson) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (lesson == null) {
            validationResponse.addError("Lesson should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Lesson: %s", lesson.getName()));
        }
        validateName(lesson.getName(), validationResponse, "Lesson name should not be blank");
        if (mTrainingService.getLessonById(lesson.getId()) == null){
            validationResponse.addError("Lesson does not exist for given Id: " + lesson.getId());
        }
        return validationResponse;
    }

    public CourseStructureValidationResponse validateChapter(Chapter chapter) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (chapter == null) {
            validationResponse.addError("Chapter should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Chapter: %s", chapter.getName()));
        }
        validateName(chapter.getName(), validationResponse, "Chapter name should not be blank");
        if (mTrainingService.getChapterById(chapter.getId()) == null){
            validationResponse.addError("Chapter does not exist for given Id: " + chapter.getId());
        }
        return validationResponse;
    }

    public CourseStructureValidationResponse validateCourse(Course course) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (course == null) {
            validationResponse.addError("Course should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Course: %s", course.getName()));
        }
        validateName(course.getName(), validationResponse, "Course name should not be blank");
        if (mTrainingService.getCourseById(course.getId()) == null){
            validationResponse.addError("Course does not exist for given Id: " + course.getId());
        }
        return validationResponse;
    }

    private void validateName(String chapterName, CourseStructureValidationResponse validationResponse, String errorMessage) {
        if (StringUtils.isBlank(chapterName)) {
            validationResponse.addError(errorMessage);
        }
    }
}
