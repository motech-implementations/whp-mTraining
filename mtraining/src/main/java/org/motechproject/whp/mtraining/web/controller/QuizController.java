package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.service.impl.ProviderServiceImpl;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuizRequest;
import org.motechproject.whp.mtraining.web.domain.QuizResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_QUIZ;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_QUESTION;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class QuizController {

    private Logger LOGGER = LoggerFactory.getLogger(QuizController.class);
    private QuizService quizService;
    private ProviderServiceImpl providerService;

    @Autowired
    public QuizController(QuizService quizService, ProviderServiceImpl providerService) {
        this.quizService = quizService;
        this.providerService = providerService;
    }

    @RequestMapping(value = "/quiz", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> getQuestionsForQuiz(@RequestParam Long callerId, @RequestParam String uniqueId,
                                                                        @RequestParam(required = false) String sessionId, @RequestParam UUID quizId,
                                                                        @RequestParam Integer quizVersion) {
        LOGGER.debug(String.format("Received bookmarkDto request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        QuizRequest quizRequest = new QuizRequest(callerId, uniqueId, sessionId, quizId, quizVersion);
        BasicResponse basicResponse = new BasicResponse(callerId, sessionId, uniqueId);
        ResponseStatus validationStatus = quizRequest.validate();
        if (!validationStatus.isValid())
            return new ResponseEntity<>(basicResponse.withResponse(validationStatus), HttpStatus.OK);
        ResponseStatus providerStatus = providerService.validateProvider(callerId);
        if (!providerStatus.isValid())
            return new ResponseEntity<>(basicResponse.withResponse(providerStatus), HttpStatus.OK);
        try {
            List<ContentIdentifierDto> questionsForQuiz = quizService.getQuestionsForQuiz(new ContentIdentifierDto(quizId, quizVersion));
            return questionsForQuiz == null ?
                    new ResponseEntity<>(new BasicResponse(callerId, sessionId, uniqueId, INVALID_QUIZ), OK) :
                    new ResponseEntity<>(new QuizResponse(callerId, sessionId, uniqueId, questionsForQuiz), OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new BasicResponse(callerId, sessionId, uniqueId, MISSING_QUESTION), OK);
        }
    }
}
