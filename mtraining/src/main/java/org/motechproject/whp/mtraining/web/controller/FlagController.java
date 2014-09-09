package org.motechproject.whp.mtraining.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.constants.CourseStatus;
import org.motechproject.whp.mtraining.domain.*;
import org.motechproject.whp.mtraining.domain.views.PropertyFilterMixIn;
import org.motechproject.whp.mtraining.exception.InvalidBookmarkException;
import org.motechproject.whp.mtraining.exception.MTrainingException;
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
    FlagService flagService;
    Sessions sessions;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagController.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FlagController(DtoFactoryService dtoFactoryService, FlagBuilder flagBuilder, ProviderService providerService,
                          BookmarkRequestService bookmarkRequestService, CourseProgressService courseProgressService,
                          CoursePublicationAttemptService coursePublicationAttemptService, FlagService flagService, Sessions sessions) {
        this.dtoFactoryService = dtoFactoryService;
        this.flagBuilder = flagBuilder;
        this.providerService = providerService;
        this.bookmarkRequestService = bookmarkRequestService;
        this.courseProgressService = courseProgressService;
        this.coursePublicationAttemptService = coursePublicationAttemptService;
        this.flagService = flagService;
        this.sessions = sessions;
    }

    public String toJsonString(Object obj, String[] ignorableFieldNames) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Object.class, PropertyFilterMixIn.class);

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("PropertyFilter",
                        SimpleBeanPropertyFilter.serializeAllExcept(
                                ignorableFieldNames));
        ObjectWriter writer = mapper.writer(filters);
        try {
            return writer.writeValueAsString(obj);
        } catch (Exception exception) {
            throw new MTrainingException("Error while converting response to JSON", exception);
        }
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object getBookmark(final CourseProgressGetRequest courseProgressGetRequest) {
        String sessionId = courseProgressGetRequest.getSessionId();
        Long callerId = courseProgressGetRequest.getCallerId();
        String uniqueId = courseProgressGetRequest.getUniqueId();
        String currentSessionId = currentSession(sessionId);
        LOGGER.debug(String.format("Received bookmark request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        List<ValidationError> validationErrors = courseProgressGetRequest.validate();
        if (!validationErrors.isEmpty()) {
            ValidationError validationError = validationErrors.get(0);
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, statusFor(validationError.getErrorCode())).getBody();
        }
        Provider provider = providerService.getProviderByCallerId(callerId);
        if (provider == null)
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, UNKNOWN_PROVIDER).getBody();
        if (isInvalid(provider.getProviderStatus()))
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, NOT_WORKING_PROVIDER).getBody();
        CourseProgress courseProgress = getCourseProgress(provider.getCallerId(), null);
        if(courseProgress == null){
            return responseAfterLogging(callerId, uniqueId, currentSessionId, GET, ResponseStatus.COURSE_NOT_FOUND).getBody();
        }
        Flag bookmark = flagService.getFlagById(courseProgress.getFlag().getId());
        bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, currentSessionId, OK, GET,
                courseProgress.getCourseStartTime(), courseProgress.getTimeLeftToCompleteCourse(), courseProgress.getCourseStatus(), new BookmarkReport(bookmark)));
        String[] ignorableFieldNames = {"id", "creationDate", "modificationDate", "creator", "owner", "modifiedBy", "level"};
        courseProgress.setFlag(bookmark);
        return toJsonString(new CourseProgressResponse(callerId, currentSessionId, uniqueId,
                provider.getLocation(), courseProgress), ignorableFieldNames);
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
        CourseProgress savedCourseProgress = null;
        try {
            CourseProgress actualCourseProgress = getCourseProgress(provider.getCallerId(), bookmark.getCourseIdentifier());
            if (actualCourseProgress == null) {
                courseProgress.setCallerId(provider.getCallerId());
                savedCourseProgress = courseProgressService.createCourseProgress(courseProgress);
            } else {
                actualCourseProgress.setCourseStartTime(courseProgress.getCourseStartTime());
                actualCourseProgress.setCourseStatus(courseProgress.getCourseStatus());
                actualCourseProgress.setFlag(courseProgress.getFlag());
                actualCourseProgress.setTimeLeftToCompleteCourse(courseProgress.getTimeLeftToCompleteCourse());
                actualCourseProgress.setCallerId(provider.getCallerId());
                savedCourseProgress = courseProgressService.updateCourseProgress(actualCourseProgress);
            }
        } catch (InvalidBookmarkException ex) {
            return responseAfterLogging(callerId, uniqueId, sessionId, POST, INVALID_FLAG);
        }

        Flag flag = flagService.getFlagById(savedCourseProgress.getFlag().getId());
        bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, sessionId,
                OK, POST, savedCourseProgress.getCourseStartTime(), savedCourseProgress.getTimeLeftToCompleteCourse(), courseStatus.getValue(), new BookmarkReport(flag)));
        return response(callerId, uniqueId, sessionId, OK, POST, CREATED);
    }

    private CourseProgress getCourseProgress(long callerId, ContentIdentifier courseIdentifier) {
        CourseProgress courseProgress = courseProgressService.getCourseProgressForProvider(callerId);
        if (courseProgress == null) {
            try {
                long courseId = dtoFactoryService.getCoursePlanByExternalId(courseIdentifier.getContentId()).getId();
                courseIdentifier.setUnitId(courseId);
                courseProgress = courseProgressService.getInitialCourseProgressForProvider(callerId, courseIdentifier);
            } catch (Exception ex) {
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
