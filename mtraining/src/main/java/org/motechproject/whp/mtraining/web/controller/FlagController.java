package org.motechproject.whp.mtraining.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.constants.CourseStatus;
import org.motechproject.whp.mtraining.domain.*;
import org.motechproject.whp.mtraining.exception.CourseNotFoundException;
import org.motechproject.whp.mtraining.exception.InvalidBookmarkException;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;
import org.motechproject.whp.mtraining.service.*;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.*;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.statusFor;
import static org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType.*;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.apache.commons.lang.StringUtils.isBlank;

@Controller
public class FlagController {

    DtoFactoryService dtoFactoryService;
    FlagBuilder flagBuilder;
    ProviderService providerService;
    BookmarkRequestService bookmarkRequestService;
    CourseProgressService courseProgressService;
    CoursePublicationAttemptService coursePublicationAttemptService;
    Sessions sessions;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagController.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FlagController(DtoFactoryService dtoFactoryService, FlagBuilder flagBuilder, ProviderService providerService,
                          BookmarkRequestService bookmarkRequestService, CourseProgressService courseProgressService,
                          CoursePublicationAttemptService coursePublicationAttemptService, Sessions sessions) {
        this.dtoFactoryService = dtoFactoryService;
        this.flagBuilder = flagBuilder;
        this.providerService = providerService;
        this.bookmarkRequestService = bookmarkRequestService;
        this.courseProgressService = courseProgressService;
        this.coursePublicationAttemptService = coursePublicationAttemptService;
        this.sessions = sessions;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> getBookmark(final CourseProgressGetRequest courseProgressGetRequest) {
        String sessionId = courseProgressGetRequest.getSessionId();
        Long callerId = courseProgressGetRequest.getCallerId();
        String uniqueId = courseProgressGetRequest.getUniqueId();
        String currentSessionId = currentSession(sessionId);
        LOGGER.debug(String.format("Received bookmark request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        List<ValidationError> validationErrors = courseProgressGetRequest.validate();
        if (!validationErrors.isEmpty()) {
            ValidationError validationError = validationErrors.get(0);
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, statusFor(validationError.getErrorCode()));
        }
        Provider provider = providerService.getProviderByCallerId(callerId);
        if (provider == null)
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, UNKNOWN_PROVIDER);
        if (isInvalid(provider.getProviderStatus()))
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, NOT_WORKING_PROVIDER);
        CourseProgress courseProgress = getCourseProgress(provider.getCallerId());
        if(courseProgress == null){
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, ResponseStatus.COURSE_NOT_FOUND);
        }
        Flag bookmark = courseProgress.getFlag();
        bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, currentSessionId, OK, GET, courseProgress.getCourseStartTime(), courseProgress.getTimeLeftToCompleteCourse(), courseProgress.getCourseStatus(), new BookmarkReport(bookmark)));
        return new ResponseEntity<>(new CourseProgressResponse(callerId, currentSessionId, uniqueId,
                provider.getLocation(), courseProgress), HttpStatus.OK);
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
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
        Flag bookmark = courseProgress.getFlag();
        Provider provider = providerService.getProviderByCallerId(callerId);
        if (provider == null) {
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, UNKNOWN_PROVIDER);
        }
        CourseStatus courseStatus = CourseStatus.enumFor(courseProgress.getCourseStatus());
        try {
            CourseProgress actualCourseProgress = getCourseProgress(provider.getCallerId());
            if (actualCourseProgress == null) {
                courseProgress.setCallerId(provider.getCallerId());
                courseProgressService.createCourseProgress(courseProgress);
            } else {
                actualCourseProgress.setCourseStartTime(courseProgress.getCourseStartTime());
                actualCourseProgress.setCourseStatus(courseProgress.getCourseStatus());
                actualCourseProgress.setFlag(courseProgress.getFlag());
                actualCourseProgress.setTimeLeftToCompleteCourse(courseProgress.getTimeLeftToCompleteCourse());
                actualCourseProgress.setCallerId(provider.getCallerId());
                courseProgressService.updateCourseProgress(courseProgress);
            }
        } catch (InvalidBookmarkException ex) {
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, INVALID_FLAG);
        }
        bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, sessionId, OK, POST, courseProgress.getCourseStartTime(), courseProgress.getTimeLeftToCompleteCourse(), courseStatus.getValue(), new BookmarkReport(bookmark)));
        return response(callerId, uniqueId, sessionId, OK, POST, CREATED);
    }

    private CourseProgress getCourseProgress(long callerId) {
        CoursePublicationAttempt latestCoursePublicationAttempt = coursePublicationAttemptService.getLastSuccessfulCoursePublicationAttempt();
        if(latestCoursePublicationAttempt == null)
            return null;
        ContentIdentifier contentIdentifier = new ContentIdentifier(latestCoursePublicationAttempt.getCourseId(),
                dtoFactoryService.getCoursePlanDtoById(latestCoursePublicationAttempt.getCourseId()).getExternalId(), latestCoursePublicationAttempt.getVersion());
        CourseProgress courseProgress = courseProgressService.getCourseProgressForProvider(callerId, contentIdentifier);
        if (courseProgress == null) {
            try{
                courseProgress = courseProgressService.getInitialCourseProgressForProvider(callerId, contentIdentifier);
            }catch (CourseNotFoundException ex){
                return null;
            }
        }
        return courseProgress;
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
        return bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(callerId, uniqueId, currentSessionId, status, requestType));
    }

}
