package org.motechproject.whp.mtraining.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.constants.CourseStatus;
import org.motechproject.whp.mtraining.domain.BookmarkReport;
import org.motechproject.whp.mtraining.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.domain.views.PropertyFilterMixIn;
import org.motechproject.whp.mtraining.exception.InvalidBookmarkException;
import org.motechproject.whp.mtraining.exception.MTrainingException;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;
import org.motechproject.whp.mtraining.service.BookmarkRequestService;
import org.motechproject.whp.mtraining.service.CourseProgressService;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.FlagService;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
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
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_FLAG;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.LOCATION_NOT_ASSOCIATED_WITH_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.statusFor;
import static org.springframework.http.HttpStatus.CREATED;

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
            return responseAfterLogging(callerId, uniqueId, sessionId, null, POST, statusFor(validationError.getErrorCode())).getBody();
        }
        Provider provider = providerService.getProviderByCallerId(callerId);
        if (provider == null)
            return responseAfterLogging(callerId, uniqueId, currentSessionId, null, GET, UNKNOWN_PROVIDER).getBody();
        String remediId = provider.getRemediId();
        if (isInvalid(provider.getProviderStatus()))
            return responseAfterLogging(callerId, uniqueId, currentSessionId, remediId, GET, NOT_WORKING_PROVIDER).getBody();
        if (provider.getLocation() == null) {
            return responseAfterLogging(callerId, uniqueId, currentSessionId, remediId, GET, LOCATION_NOT_ASSOCIATED_WITH_PROVIDER).getBody();
        }
        CourseProgress courseProgress = courseProgressService.getCourseProgress(provider);

        if (courseProgress == null) {
            return responseAfterLogging(callerId, uniqueId, currentSessionId, remediId, GET, ResponseStatus.COURSE_NOT_FOUND).getBody();
        }
        Flag flagForReport = flagService.getFlagById(courseProgress.getFlag().getId());
        bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, currentSessionId, OK, GET,
                courseProgress.getCourseStartTime(), courseProgress.getTimeLeftToCompleteCourse(), courseProgress.getCourseStatus(), new BookmarkReport(flagForReport)));
        String[] ignorableFieldNames = {"id", "creationDate", "modificationDate", "creator", "owner", "modifiedBy", "level"};
        courseProgress.setFlag(flagForReport);
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
            return responseAfterLogging(callerId, uniqueId, sessionId, null, POST, statusFor(validationError.getErrorCode()));
        }
        CourseProgress courseProgress = courseProgressPostRequest.getCourseProgress();

        Provider provider = providerService.getProviderByCallerId(callerId);
        if (provider == null) {
            return responseAfterLogging(callerId, uniqueId, sessionId, null, POST, UNKNOWN_PROVIDER);
        }
        String remediId = provider.getRemediId();
        CourseStatus courseStatus = CourseStatus.enumFor(courseProgress.getCourseStatus());
        CourseProgress savedCourseProgress = null;
        try {
            CourseProgress actualCourseProgress = courseProgressService.getCourseProgress(provider);
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
            return responseAfterLogging(callerId, uniqueId, sessionId, remediId, POST, INVALID_FLAG);
        }
        Flag flagForReport = flagService.getFlagById(savedCourseProgress.getFlag().getId());
        bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(provider.getRemediId(), callerId, uniqueId, sessionId,
                OK, POST, savedCourseProgress.getCourseStartTime(), savedCourseProgress.getTimeLeftToCompleteCourse(), courseStatus.getValue(), new BookmarkReport(flagForReport)));
        return response(callerId, uniqueId, sessionId, OK, POST, CREATED);
    }

    private String currentSession(String sessionId) {
        return isBlank(sessionId) ? sessions.create() : sessionId;
    }

    private ResponseEntity<MotechResponse> responseAfterLogging(Long callerId, String uniqueId, String currentSessionId, String remediId, BookmarkRequestType requestType, ResponseStatus status) {
        return responseAfterLogging(callerId, uniqueId, currentSessionId, remediId, status, requestType, HttpStatus.OK);
    }
    private ResponseEntity<MotechResponse> responseAfterLogging(Long callerId, String uniqueId, String currentSessionId, String remediId, ResponseStatus status, BookmarkRequestType requestType, HttpStatus httpStatus) {
        report(callerId, uniqueId, currentSessionId, remediId, requestType, status);
        return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, currentSessionId, uniqueId, status), httpStatus);
    }
    private ResponseEntity<MotechResponse> response(Long callerId, String uniqueId, String currentSessionId, ResponseStatus status, BookmarkRequestType requestType, HttpStatus httpStatus) {
        return new ResponseEntity<MotechResponse>(new BasicResponse(callerId, currentSessionId, uniqueId, status), httpStatus);
    }
    private BookmarkRequest report(Long callerId, String uniqueId, String currentSessionId, String remediId, BookmarkRequestType requestType, ResponseStatus status) {
        return bookmarkRequestService.createBookmarkRequest(new BookmarkRequest(callerId, uniqueId, currentSessionId, remediId, status, requestType));
    }

}
