package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.BookmarkReport;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;
import org.motechproject.whp.mtraining.repository.AllBookmarkRequests;
import org.motechproject.whp.mtraining.repository.Courses;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.util.DateTimeUtil;
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
    private AllBookmarkRequests allBookmarkRequests;
    private BookmarkService bookmarkService;
    private Courses courses;

    @Autowired
    public BookmarkController(Providers providers, Sessions sessions, AllBookmarkRequests allBookmarkRequests,
                              BookmarkService bookmarkService, Courses courses) {
        this.providers = providers;
        this.sessions = sessions;
        this.allBookmarkRequests = allBookmarkRequests;
        this.bookmarkService = bookmarkService;
        this.courses = courses;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> getBookmark(@RequestParam Long callerId, @RequestParam String uniqueId, @RequestParam(required = false) String sessionId) {
        String currentSessionId = currentSession(sessionId);
        LOGGER.debug(String.format("Received bookmarkDto request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        if (callerId == null)
            return responseFor(null, uniqueId, currentSessionId, BookmarkRequestType.GET, MISSING_CALLER_ID);
        if (isBlank(uniqueId))
            return responseFor(callerId, null, currentSessionId, BookmarkRequestType.GET, MISSING_UNIQUE_ID);

        Provider provider = providers.getByCallerId(callerId);
        if (provider == null)
            return responseFor(callerId, uniqueId, currentSessionId, BookmarkRequestType.GET, UNKNOWN_PROVIDER);
        if (isInvalid(provider.getActivationStatus()))
            return responseFor(callerId, uniqueId, currentSessionId, BookmarkRequestType.GET, NOT_WORKING_PROVIDER);

        BookmarkDto bookmarkDto = getBookmark(provider.getRemedyId());
        allBookmarkRequests.add(new BookmarkRequest(provider.getRemedyId(), callerId, uniqueId, currentSessionId, OK, BookmarkRequestType.GET, new BookmarkReport(bookmarkDto)));
        return new ResponseEntity<>(new BookmarkResponse(callerId, currentSessionId, uniqueId, provider.getLocation(), new Bookmark(bookmarkDto)), HttpStatus.OK);
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<MotechResponse> postBookmark(@RequestBody BookmarkPostRequest bookmarkPostRequest) {
        LOGGER.debug(String.format("Received bookmark update request for %s with bookmark %s", bookmarkPostRequest.getCallerId(), bookmarkPostRequest));

        Long callerId = bookmarkPostRequest.getCallerId();
        String uniqueId = bookmarkPostRequest.getUniqueId();
        String sessionId = bookmarkPostRequest.getSessionId();

        if (callerId == null) {
            return responseFor(null, uniqueId, sessionId, BookmarkRequestType.POST, MISSING_CALLER_ID);
        }
        if (isBlank(uniqueId)) {
            return responseFor(callerId, null, sessionId, BookmarkRequestType.POST, MISSING_UNIQUE_ID);
        }
        if (isBlank(sessionId)) {
            return responseFor(callerId, uniqueId, null, BookmarkRequestType.POST, MISSING_SESSION_ID);
        }

        Provider provider = providers.getByCallerId(callerId);

        if (provider == null) {
            return responseFor(callerId, uniqueId, sessionId, BookmarkRequestType.POST, UNKNOWN_PROVIDER);
        }

        Bookmark bookmark = bookmarkPostRequest.getBookmark();
        BookmarkDto bookmarkDto = new BookmarkDto(provider.getRemedyId(), bookmark.getCourseIdentifierDto(), bookmark.getModuleIdentifierDto(),
                bookmark.getChapterIdentifierDto(), bookmark.getMessageIdentifierDto(), DateTimeUtil.parse(bookmark.getDateModified()));
        bookmarkService.update(bookmarkDto);
        allBookmarkRequests.add(new BookmarkRequest(provider.getRemedyId(), callerId, uniqueId, sessionId, OK, BookmarkRequestType.POST, new BookmarkReport(bookmarkDto)));
        return responseFor(callerId, uniqueId, sessionId, OK, BookmarkRequestType.POST, HttpStatus.CREATED);
    }

    private BookmarkDto getBookmark(String externalId) {
        BookmarkDto bookmark = bookmarkService.getBookmark(externalId);
        if (bookmark == null) {
            Course latestCourse = courses.getLatestCourse();
            ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(latestCourse.getCourseId(), latestCourse.getVersion());
            bookmarkService.createInitialBookmark(externalId, contentIdentifierDto);
            bookmark = bookmarkService.getBookmark(externalId);
        }
        return bookmark;
    }

    private String currentSession(String sessionId) {
        return isBlank(sessionId) ? sessions.create() : sessionId;
    }

    private ResponseEntity<MotechResponse> responseFor(Long callerId, String uniqueId, String currentSessionId, BookmarkRequestType requestType, ResponseStatus status) {
        return responseFor(callerId, uniqueId, currentSessionId, status, requestType, HttpStatus.OK);
    }

    private ResponseEntity<MotechResponse> responseFor(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status, BookmarkRequestType requestType, HttpStatus httpStatus) {
        report(callerId, uniqueId, currentSessionId, requestType, status);
        return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, currentSessionId, uniqueId, status), httpStatus);
    }

    private BookmarkRequest report(Long callerId, String uniqueId, String currentSessionId, BookmarkRequestType requestType, ResponseStatus status) {
        return allBookmarkRequests.add(new BookmarkRequest(callerId, uniqueId, currentSessionId, status, requestType));
    }


}
