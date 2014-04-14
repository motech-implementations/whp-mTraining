package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.constants.CourseStatus;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseProgressDto;
import org.motechproject.mtraining.exception.InvalidBookmarkException;
import org.motechproject.mtraining.service.CourseProgressService;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.BookmarkReport;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;
import org.motechproject.whp.mtraining.repository.AllBookmarkRequests;
import org.motechproject.whp.mtraining.repository.AllCoursePublicationAttempts;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.Bookmark;
import org.motechproject.whp.mtraining.web.domain.CourseProgress;
import org.motechproject.whp.mtraining.web.domain.CourseProgressGetRequest;
import org.motechproject.whp.mtraining.web.domain.CourseProgressPostRequest;
import org.motechproject.whp.mtraining.web.domain.CourseProgressResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType.GET;
import static org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType.POST;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_BOOKMARK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.statusFor;
import static org.springframework.http.HttpStatus.CREATED;

@Controller
public class BookmarkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkController.class);

    private Providers providers;
    private Sessions sessions;
    private AllBookmarkRequests allBookmarkRequests;
    private CourseProgressService courseProgressService;
    private AllCoursePublicationAttempts allCoursePublicationAttempts;

    @Autowired
    public BookmarkController(Providers providers, Sessions sessions, AllBookmarkRequests allBookmarkRequests,
                              CourseProgressService courseProgressService, AllCoursePublicationAttempts allCoursePublicationAttempts) {
        this.providers = providers;
        this.sessions = sessions;
        this.allBookmarkRequests = allBookmarkRequests;
        this.courseProgressService = courseProgressService;
        this.allCoursePublicationAttempts = allCoursePublicationAttempts;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> getBookmark(final CourseProgressGetRequest courseProgressGetRequest) {
        String sessionId = courseProgressGetRequest.getSessionId();
        Long callerId = courseProgressGetRequest.getCallerId();
        String uniqueId = courseProgressGetRequest.getUniqueId();

        String currentSessionId = currentSession(sessionId);

        LOGGER.debug(String.format("Received bookmarkDto request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        List<ValidationError> validationErrors = courseProgressGetRequest.validate();
        if (!validationErrors.isEmpty()) {
            ValidationError validationError = validationErrors.get(0);
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, statusFor(validationError.getErrorCode()));
        }

        Provider provider = providers.getByCallerId(callerId);
        if (provider == null)
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, UNKNOWN_PROVIDER);
        if (isInvalid(provider.getProviderStatus()))
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, NOT_WORKING_PROVIDER);

        CourseProgressDto courseProgressDto = getEnrolleeCourseProgress(provider.getRemediId());
        CourseProgress courseProgress = mapToCourseProgress(courseProgressDto);
        BookmarkDto bookmarkDto = courseProgressDto.getBookmarkDto();
        allBookmarkRequests.add(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, currentSessionId, OK, GET, courseProgressDto.getCourseStartTime(), courseProgressDto.getTimeLeftToCompleteCourse(), courseProgressDto.getCourseStatus().value(), new BookmarkReport(bookmarkDto)));
        return new ResponseEntity<>(new CourseProgressResponse(callerId, currentSessionId, uniqueId,
                provider.getLocation(), courseProgress), HttpStatus.OK);
    }

    private CourseProgress mapToCourseProgress(CourseProgressDto courseProgressDto) {
        return new CourseProgress(courseProgressDto.getCourseStartTime(), mapToBookmark(courseProgressDto.getBookmarkDto()), courseProgressDto.getTimeLeftToCompleteCourse(), courseProgressDto.getCourseStatus().value());
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<MotechResponse> postBookmark(@RequestBody CourseProgressPostRequest courseProgressPostRequest) {
        LOGGER.debug(String.format("Received bookmark update request for %s with bookmark %s", courseProgressPostRequest.getCallerId(), courseProgressPostRequest));
        Long callerId = courseProgressPostRequest.getCallerId();
        String uniqueId = courseProgressPostRequest.getUniqueId();
        String sessionId = courseProgressPostRequest.getSessionId();
        List<ValidationError> validationErrors = courseProgressPostRequest.validate();
        if (!validationErrors.isEmpty()) {
            ValidationError validationError = validationErrors.get(0);
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, statusFor(validationError.getErrorCode()));
        }

        CourseProgress courseProgress = courseProgressPostRequest.getCourseProgress();
        Bookmark bookmark = courseProgress.getBookmark();

        Provider provider = providers.getByCallerId(callerId);

        if (provider == null) {
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, UNKNOWN_PROVIDER);
        }

        CourseStatus courseStatus = CourseStatus.enumFor(courseProgress.getCourseStatus());

        BookmarkDto bookmarkDto = new BookmarkDto(provider.getRemediId(), bookmark.getCourseIdentifierDto(), bookmark.getModuleIdentifierDto(),
                bookmark.getChapterIdentifierDto(), bookmark.getMessageIdentifierDto(), bookmark.getQuizIdentifierDto(), ISODateTimeUtil.parseWithTimeZoneUTC(bookmark.getDateModified()));
        CourseProgressDto courseProgressDto = new CourseProgressDto(provider.getRemediId(), ISODateTimeUtil.parseWithTimeZoneUTC(courseProgress.getCourseStartTime()), bookmarkDto, courseStatus);

        try {
            courseProgressService.addOrUpdateCourseProgress(courseProgressDto);
        } catch (InvalidBookmarkException ex) {
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, INVALID_BOOKMARK);
        }
        allBookmarkRequests.add(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, sessionId, OK, POST, courseProgress.getCourseStartTime(), courseProgress.getTimeLeftToCompleteCourse(), courseStatus.value(), new BookmarkReport(bookmarkDto)));
        return response(callerId, uniqueId, sessionId, OK, POST, CREATED);
    }

    private Bookmark mapToBookmark(BookmarkDto bookmarkDto) {
        return new Bookmark(bookmarkDto.getCourse(), bookmarkDto.getModule(), bookmarkDto.getChapter(), bookmarkDto.getMessage(), bookmarkDto.getQuiz(), bookmarkDto.getDateModified());
    }

    private CourseProgressDto getEnrolleeCourseProgress(String externalId) {
        CourseProgressDto enrolleeCourseProgressDto = courseProgressService.getCourseProgressForEnrollee(externalId);
        if (enrolleeCourseProgressDto == null) {
            CoursePublicationAttempt latestCoursePublicationAttempt = allCoursePublicationAttempts.getLastSuccessfulCoursePublicationAttempt();
            ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(latestCoursePublicationAttempt.getCourseId(), latestCoursePublicationAttempt.getVersion());
            enrolleeCourseProgressDto = courseProgressService.getInitialCourseProgressForEnrollee(externalId, contentIdentifierDto);
        }
        return enrolleeCourseProgressDto;
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
