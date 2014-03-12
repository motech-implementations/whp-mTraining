package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.whp.mtraining.domain.BookmarkRequestLog;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.BookmarkRequestLogs;
import org.motechproject.whp.mtraining.repository.Courses;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.BookmarkResponse;
import org.motechproject.whp.mtraining.web.domain.ErrorResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@Controller
public class BookmarkController {

    private Providers providers;
    private Sessions sessions;
    private BookmarkRequestLogs bookmarkRequestLogs;
    private BookmarkService bookmarkService;
    private Courses courses;

    @Autowired
    public BookmarkController(Providers providers, Sessions sessions, BookmarkRequestLogs bookmarkRequestLogs, BookmarkService bookmarkService, Courses courses) {
        this.providers = providers;
        this.sessions = sessions;
        this.bookmarkRequestLogs = bookmarkRequestLogs;
        this.bookmarkService = bookmarkService;
        this.courses = courses;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bookmark")
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

        BookmarkDto bookmark = getBookmark(provider.getId());
        bookmarkRequestLogs.record(new BookmarkRequestLog(callerId, uniqueId, currentSessionId, OK, provider.getId(), bookmark.getCourse().getContentId(),
                bookmark.getCourse().getVersion(), bookmark.getModule().getContentId(), bookmark.getModule().getVersion(), bookmark.getChapter().getContentId(),
                bookmark.getChapter().getVersion(), bookmark.getMessage().getContentId(), bookmark.getMessage().getVersion()));
        BookmarkResponse bookmarkResponse = new BookmarkResponse(callerId, currentSessionId, uniqueId, provider.getLocation(), bookmark);
        return bookmarkResponse;
    }

    private BookmarkDto getBookmark(Long externalId) {
        BookmarkDto bookmark = bookmarkService.getBookmark(externalId.toString());
        if (bookmark == null) {
            Course latestCourse = courses.getLatestCourse();
            ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(latestCourse.getCourseId(), latestCourse.getVersion());
            bookmarkService.addBookmark(externalId.toString(), contentIdentifierDto);
            bookmark = bookmarkService.getBookmark(externalId.toString());
        }
        return bookmark;
    }

    //TODO:What if sessionId is present but no session can be found with that do we need to check that ?
    private String currentSession(String sessionId) {
        return isBlank(sessionId) ? sessions.create() : sessionId;
    }

    private MotechResponse errorResponse(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status) {
        bookmarkRequestLogs.record(new BookmarkRequestLog(callerId, uniqueId, currentSessionId, status, null, null, null, null, null, null, null, null, null));
        return new ErrorResponse(callerId, currentSessionId, uniqueId, status);
    }
}