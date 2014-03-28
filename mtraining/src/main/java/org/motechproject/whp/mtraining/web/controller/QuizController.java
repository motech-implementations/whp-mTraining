package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.impl.ProviderServiceImpl;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
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

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class QuizController {

    private Logger LOGGER = LoggerFactory.getLogger(QuizController.class);
    private QuizService quizService;
    private ProviderServiceImpl providerService;
    private Sessions sessions;

    @Autowired
    public QuizController(QuizService quizService, ProviderServiceImpl providerService, Sessions sessions) {
        this.quizService = quizService;
        this.providerService = providerService;
        this.sessions = sessions;
    }

    @RequestMapping(value = "/quiz", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> getQuestionsForQuiz(@RequestParam Long callerId, @RequestParam String uniqueId,
                                                                        @RequestParam(required = false) String sessionId, @RequestParam UUID quizId,
                                                                        @RequestParam Integer quizVersion) {
        String currentSessionId = currentSession(sessionId);
        LOGGER.debug(String.format("Received bookmarkDto request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        if (callerId == null)
            return responseAfterLogging(null, uniqueId, currentSessionId, MISSING_CALLER_ID);
        if (isBlank(uniqueId))
            return responseAfterLogging(callerId, null, currentSessionId, MISSING_UNIQUE_ID);

        Provider provider = providerService.byCallerId(callerId);
        if (provider == null)
            return responseAfterLogging(callerId, uniqueId, currentSessionId, UNKNOWN_PROVIDER);
        if (isInvalid(provider.getProviderStatus()))
            return responseAfterLogging(callerId, uniqueId, currentSessionId, NOT_WORKING_PROVIDER);

        List<ContentIdentifierDto> questionsForQuiz = quizService.getQuestionsForQuiz(new ContentIdentifierDto(quizId, quizVersion));
        return new ResponseEntity<>(new QuizResponse(callerId, sessionId, uniqueId, questionsForQuiz), OK);
    }

    private String currentSession(String sessionId) {
        return isBlank(sessionId) ? sessions.create() : sessionId;
    }

    private ResponseEntity<MotechResponse> responseAfterLogging(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status) {
        return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, currentSessionId, uniqueId, status), HttpStatus.OK);
    }
}
