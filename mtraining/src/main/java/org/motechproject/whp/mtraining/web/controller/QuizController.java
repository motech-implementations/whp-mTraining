package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.reports.QuizReporter;
import org.motechproject.whp.mtraining.service.impl.ProviderServiceImpl;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizRequest;
import org.motechproject.whp.mtraining.web.domain.QuizResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.motechproject.whp.mtraining.web.domain.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_QUIZ;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.statusFor;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class QuizController {

    private Logger LOGGER = LoggerFactory.getLogger(QuizController.class);
    private QuizService quizService;
    private ProviderServiceImpl providerService;
    private QuizReporter quizReporter;

    @Autowired
    public QuizController(QuizService quizService, ProviderServiceImpl providerService, QuizReporter quizReporter) {
        this.quizService = quizService;
        this.providerService = providerService;
        this.quizReporter = quizReporter;
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
            List<ContentIdentifierDto> questionsForQuiz = quizService.getQuestionsForQuiz(new ContentIdentifierDto(quizId, quizVersion));
            if (questionsForQuiz == null) {
                LOGGER.error(String.format("No quiz found for quizId %s and version %s", quizId, quizVersion));
                return new ResponseEntity<>(new BasicResponse(callerId, sessionId, uniqueId, ResponseStatus.MISSING_QUIZ), OK);
            }
            LOGGER.debug(String.format("Questions found for quizId %s and version %s - %s", quizId, quizVersion, questionsForQuiz));
            return new ResponseEntity<>(new QuizResponse(callerId, sessionId, uniqueId, questionsForQuiz), OK);
        } catch (IllegalStateException e) {
            LOGGER.error(String.format("Invalid quiz with quizId %s and version %s.Not enough questions found", quizId, quizVersion));
            return new ResponseEntity<>(new BasicResponse(callerId, sessionId, uniqueId, ResponseStatus.INVALID_QUIZ), OK);
        }
    }

    @RequestMapping(value = "/quiz", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> submitQuizResults(@RequestBody QuizReportRequest quizReportRequest) {
        LOGGER.debug(String.format("Received quizResult request for %s with quiz %s", quizReportRequest.getCallerId(), quizReportRequest.getQuizDto()));
        Long callerId = quizReportRequest.getCallerId();
        String sessionId = quizReportRequest.getSessionId();
        String uniqueId = quizReportRequest.getUniqueId();
        ResponseStatus validationResponse = quizReportRequest.validate();
        if (!validationResponse.isValid())
            return new ResponseEntity<>(new BasicResponse(callerId, sessionId, uniqueId, validationResponse), OK);
        QuizDto quiz = quizService.getQuiz(quizReportRequest.getQuizDto());
        return quiz == null ?
                new ResponseEntity<>(new BasicResponse(callerId, sessionId, uniqueId, INVALID_QUIZ), OK) :
                new ResponseEntity<>(quizReporter.validateAndProcessQuiz(quiz, quizReportRequest), OK);
    }
}
