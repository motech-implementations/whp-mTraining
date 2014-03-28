package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.exception.BookmarkUpdateException;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.domain.CoursePublicationStatus;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.BookmarkReport;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;
import org.motechproject.whp.mtraining.repository.AllBookmarkRequests;
import org.motechproject.whp.mtraining.repository.AllCoursePublicationStatus;
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
import static org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType.GET;
import static org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType.POST;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_SESSION_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;
import static org.springframework.http.HttpStatus.CREATED;

@Controller
public class BookmarkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkController.class);

    private Providers providers;
    private Sessions sessions;
    private AllBookmarkRequests allBookmarkRequests;
    private BookmarkService bookmarkService;
    private AllCoursePublicationStatus allCoursePublicationStatus;

    @Autowired
    public BookmarkController(Providers providers, Sessions sessions, AllBookmarkRequests allBookmarkRequests,
                              BookmarkService bookmarkService, AllCoursePublicationStatus allCoursePublicationStatus) {
        this.providers = providers;
        this.sessions = sessions;
        this.allBookmarkRequests = allBookmarkRequests;
        this.bookmarkService = bookmarkService;
        this.allCoursePublicationStatus = allCoursePublicationStatus;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> getBookmark(@RequestParam Long callerId, @RequestParam String uniqueId, @RequestParam(required = false) String sessionId) {
        String currentSessionId = currentSession(sessionId);
        LOGGER.debug(String.format("Received bookmarkDto request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        if (callerId == null)
            return responseAfterLogging(null, uniqueId, currentSessionId, GET, MISSING_CALLER_ID);
        if (isBlank(uniqueId))
            return responseAfterLogging(callerId, null, currentSessionId, GET, MISSING_UNIQUE_ID);

        Provider provider = providers.getByCallerId(callerId);
        if (provider == null)
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, UNKNOWN_PROVIDER);
        if (isInvalid(provider.getProviderStatus()))
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, NOT_WORKING_PROVIDER);

        BookmarkDto bookmarkDto = getBookmark(provider.getRemediId());
        allBookmarkRequests.add(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, currentSessionId, OK, GET, new BookmarkReport(bookmarkDto)));
        return new ResponseEntity<>(new BookmarkResponse(callerId, currentSessionId, uniqueId,
                provider.getLocation(), mapToBookmark(bookmarkDto)), HttpStatus.OK);
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<MotechResponse> postBookmark(@RequestBody BookmarkPostRequest bookmarkPostRequest) {
        LOGGER.debug(String.format("Received bookmark update request for %s with bookmark %s", bookmarkPostRequest.getCallerId(), bookmarkPostRequest));

        Long callerId = bookmarkPostRequest.getCallerId();
        String uniqueId = bookmarkPostRequest.getUniqueId();
        String sessionId = bookmarkPostRequest.getSessionId();

        if (callerId == null) {
            return responseAfterLogging(null, uniqueId, sessionId, POST, MISSING_CALLER_ID);
        }
        if (isBlank(uniqueId)) {
            return responseAfterLogging(callerId, null, sessionId, POST, MISSING_UNIQUE_ID);
        }
        if (isBlank(sessionId)) {
            return responseAfterLogging(callerId, uniqueId, null, POST, MISSING_SESSION_ID);
        }
        Bookmark bookmark = bookmarkPostRequest.getBookmark();

        if (!bookmark.hasValidModifiedDate()) {
            return responseAfterLogging(callerId, uniqueId, sessionId, BookmarkRequestType.POST, ResponseStatus.INVALID_BOOKMARK_MODIFIED_DATE);
        }

        Provider provider = providers.getByCallerId(callerId);

        if (provider == null) {
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, UNKNOWN_PROVIDER);
        }

        BookmarkDto bookmarkDto = new BookmarkDto(provider.getRemediId(), bookmark.getCourseIdentifierDto(), bookmark.getModuleIdentifierDto(),
                bookmark.getChapterIdentifierDto(), bookmark.getMessageIdentifierDto(), ISODateTimeUtil.parseWithTimeZoneUTC(bookmark.getDateModified()));
        try {
            bookmarkService.addOrUpdate(bookmarkDto);
        } catch (BookmarkUpdateException ex) {
            bookmarkService.deleteBookmarkFor(bookmarkDto.getExternalId());
        } finally {
            allBookmarkRequests.add(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, sessionId, OK, POST, new BookmarkReport(bookmarkDto)));
            return response(callerId, uniqueId, sessionId, OK, POST, CREATED);
        }
    }

    private Bookmark mapToBookmark(BookmarkDto bookmarkDto) {
        return new Bookmark(bookmarkDto.getCourse(), bookmarkDto.getModule(), bookmarkDto.getChapter(), bookmarkDto.getMessage(), bookmarkDto.getDateModified());
    }

    //TODO:Don't we need to check if is published ?
    private BookmarkDto getBookmark(String externalId) {
        BookmarkDto bookmark = bookmarkService.getBookmark(externalId);
        if (bookmark == null) {
            CoursePublicationStatus latestCoursePublicationStatus = allCoursePublicationStatus.getLatestCoursePublicationStatus();
            ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(latestCoursePublicationStatus.getCourseId(), latestCoursePublicationStatus.getVersion());
            bookmark = bookmarkService.createInitialBookmark(externalId, contentIdentifierDto);
        }
        return bookmark;
    }

    private String currentSession(String sessionId) {
        return isBlank(sessionId) ? sessions.create() : sessionId;
    }

    private ResponseEntity<MotechResponse> responseAfterLogging(Long callerId, String uniqueId, String currentSessionId, BookmarkRequestType requestType, ResponseStatus status) {
        return responseAfterLogging(callerId, uniqueId, currentSessionId, status, requestType, HttpStatus.OK);
    }

    private ResponseEntity<MotechResponse> responseAfterLogging(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status, BookmarkRequestType requestType, HttpStatus httpStatus) {
        report(callerId, uniqueId, currentSessionId, requestType, status);
        return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, currentSessionId, uniqueId, status), httpStatus);
    }

    private ResponseEntity<MotechResponse> response(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status, BookmarkRequestType requestType, HttpStatus httpStatus) {
        return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, currentSessionId, uniqueId, status), httpStatus);
    }


    private BookmarkRequest report(Long callerId, String uniqueId, String currentSessionId, BookmarkRequestType requestType, ResponseStatus status) {
        return allBookmarkRequests.add(new BookmarkRequest(callerId, uniqueId, currentSessionId, status, requestType));
    }


}
