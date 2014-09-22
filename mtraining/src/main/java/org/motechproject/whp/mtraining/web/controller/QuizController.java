package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.reports.QuizReporter;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.*;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.statusFor;

/**
 * Web API for Quiz
 */
@Controller
public class QuizController {

    private Logger LOGGER = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    MTrainingService mTrainingService;

    @Autowired
    DtoFactoryService dtoFactoryService;

    @Autowired
    ProviderService providerService;

    @Autowired
    QuizReporter quizReporter;

    @RequestMapping("/quizzes")
    @ResponseBody
    public List<QuizDto> getAllQuizzes() {
        return dtoFactoryService.getAllQuizDtos();
    }

    @RequestMapping(value = "/quiz-api/{quizId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public QuizDto getQuiz(@PathVariable long quizId) {
        return dtoFactoryService.getQuizDtoById(quizId);
    }

    @RequestMapping(value = "/quiz-api", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void createQuiz(@RequestBody QuizDto quiz) {
        dtoFactoryService.createOrUpdateFromDto(quiz);
    }

    @RequestMapping(value = "/quiz-api/{quizId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public void updateQuiz(@RequestBody QuizDto quiz) {
        dtoFactoryService.createOrUpdateFromDto(quiz);
    }

    @RequestMapping(value = "/quiz-api/{quizId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeQuiz(@PathVariable long quizId) {
        mTrainingService.deleteQuiz(quizId);
    }

    @RequestMapping(value = "/quiz", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> getQuestionsForQuiz(@RequestParam Long callerId, @RequestParam String uniqueId,
                                                                        @RequestParam(required = false) String sessionId, @RequestParam UUID quizId,
                                                                        @RequestParam Integer quizVersion) {
        LOGGER.debug(String.format("Received question request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        QuizRequest quizRequest = new QuizRequest(callerId, uniqueId, sessionId, quizId, quizVersion);
        BasicResponse basicResponse = new BasicResponse(callerId, sessionId, uniqueId);
        List<ValidationError> validationErrors = quizRequest.validate();
        if (!validationErrors.isEmpty()) {
            ValidationError firstError = validationErrors.get(0);
            ResponseStatus responseStatus = statusFor(firstError.getErrorCode());
            return new ResponseEntity<>(basicResponse.withResponse(responseStatus), HttpStatus.OK);
        }
        ResponseStatus providerStatus = providerService.validateProvider(callerId);
        if (!providerStatus.isValid()) {
            return new ResponseEntity<>(basicResponse.withResponse(providerStatus), HttpStatus.OK);
        }
        try {
            QuizDto quiz = dtoFactoryService.getQuizDtoByUuid(quizId);
            List<ContentIdentifier> questionsForQuiz = new ArrayList<>();
            if (quiz != null) {
                for (QuestionDto question : quiz.getQuestions()) {
                    questionsForQuiz.add(new ContentIdentifier(question.getId(), question.getContentId(), quiz.getVersion()));
                }
            }
            else {
                LOGGER.error(String.format("No quiz found for quizId %s and version %s", quizId, quizVersion));
                return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, sessionId, uniqueId, ResponseStatus.MISSING_QUIZ), HttpStatus.OK);
            }
            LOGGER.debug(String.format("Questions found for quizId %s and version %s - %s", quizId, quizVersion, questionsForQuiz));
            return new ResponseEntity<MotechResponse>(new QuizResponse(callerId, sessionId, uniqueId, questionsForQuiz), HttpStatus.OK);
        } catch (IllegalStateException e) {
            LOGGER.error(String.format("Invalid quiz with quizId %s and version %s.Not enough questions found", quizId, quizVersion));
            return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, sessionId, uniqueId, ResponseStatus.QUIZ_NOT_FOUND), HttpStatus.OK);
        }
    }
    @RequestMapping(value = "/quiz", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> submitQuizResults(@RequestBody QuizReportRequest quizReportRequest) {
        LOGGER.debug(String.format("Received quizResult request for %s with quiz %s", quizReportRequest.getCallerId(), quizReportRequest.getQuiz()));
        Long callerId = quizReportRequest.getCallerId();
        String sessionId = quizReportRequest.getSessionId();
        String uniqueId = quizReportRequest.getUniqueId();
        List<ValidationError> validationErrors = quizReportRequest.validate();
        BasicResponse basicResponse = new BasicResponse(callerId, uniqueId, sessionId);
        if (!validationErrors.isEmpty()) {
            ValidationError firstValidationError = validationErrors.get(0);
            return new ResponseEntity<MotechResponse>(basicResponse.withResponse(firstValidationError.getErrorCode(), firstValidationError.getMessage()), HttpStatus.OK);
        }
        Provider provider = providerService.getProviderByCallerId(quizReportRequest.getCallerId());
        if (provider == null)
            return new ResponseEntity<MotechResponse>(basicResponse.withResponse(ResponseStatus.UNKNOWN_PROVIDER), HttpStatus.OK);
        return new ResponseEntity<MotechResponse>(quizReporter.processAndLogQuiz(String.valueOf(provider.getCallerId()), quizReportRequest), HttpStatus.OK);
    }

}
