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
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.Bookmark;
import org.motechproject.whp.mtraining.web.domain.BookmarkPostRequest;
import org.motechproject.whp.mtraining.web.domain.BookmarkResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
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

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_SESSION_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@Controller
public class BookmarkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkController.class);

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

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET)
    @ResponseBody
    public MotechResponse getBookmark(@RequestParam Long callerId, @RequestParam String uniqueId, @RequestParam(required = false) String sessionId) {
        String currentSessionId = currentSession(sessionId);
        LOGGER.debug(String.format("Received bookmark request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        if (callerId == null)
            return response(null, uniqueId, currentSessionId, MISSING_CALLER_ID);
        if (isBlank(uniqueId))
            return response(callerId, null, currentSessionId, MISSING_UNIQUE_ID);

        Provider provider = providers.getByCallerId(callerId);
        if (provider == null)
            return response(callerId, uniqueId, currentSessionId, UNKNOWN_PROVIDER);
        if (isInvalid(provider.getActivationStatus()))
            return response(callerId, uniqueId, currentSessionId, NOT_WORKING_PROVIDER);

        BookmarkDto bookmark = getBookmark(provider.getRemedyId());
        bookmarkRequestLogs.record(new BookmarkRequestLog(callerId, uniqueId, currentSessionId, OK, provider.getRemedyId(), bookmark));
        BookmarkResponse bookmarkResponse = new BookmarkResponse(callerId, currentSessionId, uniqueId, provider.getLocation(), bookmark);
        return bookmarkResponse;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<MotechResponse> postBookmark(@RequestBody BookmarkPostRequest bookmarkPostRequest) {
        LOGGER.debug(String.format("Received bookmark update request for %s with bookmark %s", bookmarkPostRequest.getCallerId(), bookmarkPostRequest));

        Long callerId = bookmarkPostRequest.getCallerId();
        String uniqueId = bookmarkPostRequest.getUniqueId();
        String sessionId = bookmarkPostRequest.getSessionId();

        if (callerId == null) {
            logBookmarkPostResponse(null, sessionId, MISSING_CALLER_ID);
            return new ResponseEntity<>(response(callerId, uniqueId, sessionId, MISSING_CALLER_ID), HttpStatus.OK);
        }
        if (isBlank(uniqueId)) {
            logBookmarkPostResponse(callerId, sessionId, MISSING_UNIQUE_ID);
            return new ResponseEntity<>(response(callerId, uniqueId, sessionId, MISSING_UNIQUE_ID), HttpStatus.OK);
        }
        if (isBlank(sessionId)) {
            logBookmarkPostResponse(callerId, sessionId, MISSING_SESSION_ID);
            return new ResponseEntity<>(response(callerId, uniqueId, sessionId, MISSING_SESSION_ID), HttpStatus.OK);
        }

        Provider provider = providers.getByCallerId(callerId);

        if (provider == null) {
            return new ResponseEntity<>(response(callerId, uniqueId, sessionId, UNKNOWN_PROVIDER), HttpStatus.OK);
        }

        Bookmark bookmark = bookmarkPostRequest.getBookmark();
        BookmarkDto bookmarkDto = new BookmarkDto(provider.getRemedyId(), bookmark.getCourseIdentifierDto(), bookmark.getModuleIdentifierDto(),
                bookmark.getChapterIdentifierDto(), bookmark.getMessageIdentifierDto(), bookmarkPostRequest.getDateModified());
        bookmarkService.update(bookmarkDto);

        return new ResponseEntity<>(response(callerId, uniqueId, sessionId, OK), HttpStatus.CREATED);
    }

    private BookmarkDto getBookmark(String externalId) {
        BookmarkDto bookmark = bookmarkService.getBookmark(externalId);
        if (bookmark == null) {
            Course latestCourse = courses.getLatestCourse();
            ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(latestCourse.getCourseId(), latestCourse.getVersion());
            bookmarkService.addBookmark(externalId, contentIdentifierDto);
            bookmark = bookmarkService.getBookmark(externalId);
        }
        return bookmark;
    }

    private String currentSession(String sessionId) {
        return isBlank(sessionId) ? sessions.create() : sessionId;
    }

    private MotechResponse response(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status) {
        bookmarkRequestLogs.record(new BookmarkRequestLog(callerId, uniqueId, currentSessionId, status));
        return new BasicResponse(callerId, currentSessionId, uniqueId, status);
    }

    private void logBookmarkPostResponse(Long callerId, String sessionId, ResponseStatus responseStatus) {
        LOGGER.debug(String.format("bookmark update request for %s in sesssion %s completed with status %s", callerId, sessionId, responseStatus));
    }


}
