package org.motechproject.whp.mtraining.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.domain.*;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.service.*;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.*;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FlagControllerTest {

    @Mock
    private DtoFactoryService dtoFactoryService;
    @Mock
    private FlagBuilder flagBuilder;
    @Mock
    private ProviderService providerService;
    @Mock
    private BookmarkRequestService bookmarkRequestService;
    @Mock
    private CourseProgressService courseProgressService;
    @Mock
    private CoursePublicationAttemptService coursePublicationAttemptService;
    @Mock
    private FlagService flagService;
    @Mock
    private Sessions sessions;

    private long callerId = 1234567890L;
    private String uniqueId = "uniqueId", sessionId = "sessionId";
    private FlagController flagController;
    private ContentIdentifier course, module, chapter, lesson, quiz;
    private Flag flag;
    private CourseProgress courseProgress;

    @Before
    public void setUp() {
        flagController = new FlagController(dtoFactoryService, flagBuilder, providerService, bookmarkRequestService,
                courseProgressService, coursePublicationAttemptService, flagService, sessions);
        course = new ContentIdentifier("16asvebd83-d1db-4c8c-ac92-1136c4601cd6", 1);
        module = new ContentIdentifier("0a37255a-57ff-4cc5-9c7e-2caba8968ef2", 1);
        chapter = new ContentIdentifier("6841ded4-6391-4c1c-8fa8-6635cf59a07d", 1);
        lesson = new ContentIdentifier("57bec5e2-8efb-411b-9d2d-7f8ae1b93b68", 1);
        quiz = null;
        flag = new Flag(course, module, chapter, lesson, quiz, "2014-04-10T10:50:10.796Z");
        courseProgress = new CourseProgress("2014-04-10T10:50:10.796Z", flag, 60, "ONGOING");
        when(providerService.getProviderByCallerId(callerId)).thenReturn(new Provider(null, callerId, null, null));
        when(flagService.getFlagById(anyLong())).thenReturn(flag);
    }

    @Test
    public void shouldConvertJsonToPostRequest() throws Exception {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("test-course-progress-post-request.json");
        assertNotNull(resourceAsStream);

        CourseProgressPostRequest courseProgressPostRequest = new ObjectMapper().readValue(resourceAsStream, CourseProgressPostRequest.class);
        assertNotNull(courseProgressPostRequest);
    }

    @Test
    public void shouldReturnSuccessWhenBookmarkIsAdded() {
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(callerId, uniqueId, sessionId, courseProgress);
        when(courseProgressService.createCourseProgress(any(CourseProgress.class))).thenReturn(courseProgress);

        ResponseEntity<? extends MotechResponse> response = flagController.postBookmark(courseProgressPostRequest);

        assertTrue(ResponseStatus.OK.getCode().equals(response.getBody().getResponseCode()));
    }

    @Test
    public void shouldReturnBookmark() {
        CourseProgressGetRequest courseProgressGetRequest = new CourseProgressGetRequest(callerId, sessionId, uniqueId);
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(0L, true);
        when(dtoFactoryService.getCoursePlanDtoById(anyLong())).thenReturn(new CoursePlanDto());
        when(courseProgressService.getCourseProgressForProvider(anyLong())).thenReturn(courseProgress);

        ResponseEntity<? extends MotechResponse> response = flagController.getBookmark(courseProgressGetRequest);

        assertTrue(ResponseStatus.OK.getCode().equals(response.getBody().getResponseCode()));
    }


}
