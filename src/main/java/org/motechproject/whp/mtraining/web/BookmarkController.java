package org.motechproject.whp.mtraining.web;

import org.motechproject.whp.mtraining.domain.BookmarkRequestLog;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.BookmarkRequestLogs;
import org.motechproject.whp.mtraining.repository.Providers;
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

        if (callerId == null) {
            return errorResponse(null, uniqueId, currentSessionId, ResponseStatus.MISSING_CALLER_ID);
        }

        if (isBlank(uniqueId)) {
            return errorResponse(callerId, null, currentSessionId, ResponseStatus.MISSING_UNIQUE_ID);
        }

        Provider provider = providers.getByCallerId(callerId);
        if (provider == null) {
            return errorResponse(callerId, uniqueId, currentSessionId, ResponseStatus.UNKNOWN_PROVIDER);
        }

        BookmarkResponse bookmarkResponse = new BookmarkResponse(callerId, currentSessionId, uniqueId);
        Location location = provider.getLocation();
        bookmarkResponse.setLocation(location);
        bookmarkRequestLogs.record(new BookmarkRequestLog(callerId, uniqueId, currentSessionId, ResponseStatus.OK));
        return bookmarkResponse;
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
