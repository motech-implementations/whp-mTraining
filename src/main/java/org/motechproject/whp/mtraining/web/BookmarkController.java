package org.motechproject.whp.mtraining.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.whp.mtraining.domain.CallLog;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.CallLogs;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.domain.BookmarkResponse;
import org.motechproject.whp.mtraining.web.domain.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jdo.identity.LongIdentity;

@Controller
public class BookmarkController {

    private Providers providers;
    private Sessions sessions;
    private CallLogs callLogs;

    @Autowired
    public BookmarkController(Providers providers, Sessions sessions, CallLogs callLogs) {
        this.providers = providers;
        this.sessions = sessions;
        this.callLogs = callLogs;
    }

    @RequestMapping("/bookmark")
    @ResponseBody
    public BookmarkResponse getBookmark(@RequestParam Long callerId, @RequestParam String uniqueId, @RequestParam(required = false) String sessionId) {
        BookmarkResponse bookmarkResponse = new BookmarkResponse(callerId, currentSession(sessionId), uniqueId);
        Provider provider = providers.getByCallerId(callerId);
        if (provider == null) {
            bookmarkResponse.setErrorCode(ErrorCode.UNKNOWN);
        } else {
            Location location = provider.getLocation();
            bookmarkResponse.setLocation(location);
        }
        String errorCode = bookmarkResponse.hasError() ? bookmarkResponse.getErrorCode() : null;
        callLogs.record(new CallLog(callerId, uniqueId, sessionId, errorCode));
        return bookmarkResponse;
    }

    //TODO:What if sessionId is present but no session can be found with that do we need to check that ?
    private String currentSession(String sessionId) {
        return StringUtils.isBlank(sessionId) ? sessions.create() : sessionId;
    }

}
