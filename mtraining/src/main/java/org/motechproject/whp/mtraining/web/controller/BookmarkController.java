package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.BookmarkRequestLog;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.BookmarkRequestLogs;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.BookmarkResponse;
import org.motechproject.whp.mtraining.web.domain.ErrorResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.*;

@Controller
public class BookmarkController {

    private Providers providers;
    private Sessions sessions;
    private BookmarkRequestLogs bookmarkRequestLogs;

    @Autowired
    public BookmarkController(Providers providers, Sessions sessions, BookmarkRequestLogs bookmarkRequestLogs) {
        this.providers = providers;
        this.sessions = sessions;
        this.bookmarkRequestLogs = bookmarkRequestLogs;
    }

    @RequestMapping("/bookmark")
    @ResponseBody
    public MotechResponse getBookmark(@RequestParam Long callerId, @RequestParam String uniqueId, @RequestParam(required = false) String sessionId) {
        String currentSessionId = currentSession(sessionId);
        if (callerId == null)
            return errorResponse(null, uniqueId, currentSessionId, MISSING_CALLER_ID);
        if (isBlank(uniqueId))
            return errorResponse(callerId, null, currentSessionId, MISSING_UNIQUE_ID);

        Provider provider = providers.getByCallerId(callerId);
        if (provider == null)
            return errorResponse(callerId, uniqueId, currentSessionId, UNKNOWN_PROVIDER);
        if (isInvalid(provider.getActivationStatus()))
            return errorResponse(callerId, uniqueId, currentSessionId, NOT_WORKING_PROVIDER);

        bookmarkRequestLogs.record(new BookmarkRequestLog(callerId, uniqueId, currentSessionId, OK));
        return new BookmarkResponse(callerId, currentSessionId, uniqueId, provider.getLocation());
    }

    //TODO:What if sessionId is present but no session can be found with that do we need to check that ?
    private String currentSession(String sessionId) {
        return isBlank(sessionId) ? sessions.create() : sessionId;
    }

    private MotechResponse errorResponse(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status) {
        bookmarkRequestLogs.record(new BookmarkRequestLog(callerId, uniqueId, currentSessionId, status));
        return new ErrorResponse(callerId, currentSessionId, uniqueId, status);
    }
}