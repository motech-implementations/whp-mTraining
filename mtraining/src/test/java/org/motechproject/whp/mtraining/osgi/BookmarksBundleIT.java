package org.motechproject.whp.mtraining.osgi;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.CourseBuilder;
import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.domain.*;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.whp.mtraining.IVRServer;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponse;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponseHandler;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.web.domain.CourseProgressPostRequest;
import org.motechproject.whp.mtraining.web.domain.CourseProgressResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.WORKING_PROVIDER;

@Ignore
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class BookmarksBundleIT {

    static final String BOOKMARK_QUERY_WITH_SESSION_ID = "http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s&sessionId=%s";
    static final String BOOKMARK_QUERY_WITHOUT_SESSION_ID = "http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s";

    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 10);

    private List<Long> providersAdded = new ArrayList<>();

    @Inject
    private MTrainingService mTrainingService;

    @Inject
    private ProviderService providerService;

    @Inject
    private CourseConfigurationService courseConfigService;

    private Provider activeProvider;
    protected IVRServer ivrServer;
    private Course course002;


    @Before
    public void setUp() throws InterruptedException, IOException {
        ivrServer = new IVRServer(8888, "/ivr-wgn").start();

        String courseName = String.format("CS002-%s", UUID.randomUUID());
        course002 = mTrainingService.getCourseById(createCourse(courseName).getId());
        courseConfigService.createCourseConfiguration(new CourseConfiguration(course002.getId(), 60, new Location("block", "district", "state")));

        removeAllProviders();
        activeProvider = addProvider("remediId1", 22222L, WORKING_PROVIDER);
    }


    private Course createCourse(final String courseName) {
        return mTrainingService.createCourse(new CourseBuilder().withName(courseName).build());
    }

    public void testThatStatusURLDoesIsAvailableWithoutAuthentication() throws IOException, InterruptedException {
        HttpResponse response = httpClient.get(String.format("http://localhost:%s/mtraining/web-api/status", TestContext.getJettyPort()));
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    public HttpUriRequest httpRequestWithAuthHeaders(String url, String method) { return null; }

    public void testThatResponseIs800WhenProviderIsKnown() throws IOException, InterruptedException {
        String bookmarkRequestURLForAKnownUser = getBookmarkRequestUrlWith(activeProvider.getCallerId(), "un1qId", "s001");
        CustomHttpResponse responseForKnownUser = httpClient.execute(httpRequestWithAuthHeaders(bookmarkRequestURLForAKnownUser, "Get"), new CustomHttpResponseHandler());
        CourseProgressResponse bookmarkForKnownUser = (CourseProgressResponse) responseToJson(responseForKnownUser.getContent(), CourseProgressResponse.class);

        assertEquals(HttpStatus.SC_OK, responseForKnownUser.getStatusCode());
        assertEquals(ResponseStatus.OK.getCode(), bookmarkForKnownUser.getResponseCode());
        assertEquals(activeProvider.getCallerId(), bookmarkForKnownUser.getCallerId());
        assertEquals("un1qId", bookmarkForKnownUser.getUniqueId());
        assertEquals("s001", bookmarkForKnownUser.getSessionId());
        assertEquals("state", bookmarkForKnownUser.getLocation().getState());
        assertEquals("block", bookmarkForKnownUser.getLocation().getBlock());
        assertEquals("district", bookmarkForKnownUser.getLocation().getDistrict());
    }

    public void testThatResponseIs901WhenProviderIsInvalid() throws IOException, InterruptedException {
        String bookmarkURLForUnknownUser = getBookmarkRequestUrlWith(9988776655L, "un1qId", null);
        HttpUriRequest httpGetRequest = httpRequestWithAuthHeaders(bookmarkURLForUnknownUser, "Get");

        CustomHttpResponse responseForUnknownUser = httpClient.execute(httpGetRequest, new CustomHttpResponseHandler());
        BasicResponse bookmarkForUnknownUser = (BasicResponse) responseToJson(responseForUnknownUser.getContent(), BasicResponse.class);

        assertEquals(HttpStatus.SC_OK, responseForUnknownUser.getStatusCode());
        assertEquals(new Long(9988776655l), bookmarkForUnknownUser.getCallerId());
        assertEquals("un1qId", bookmarkForUnknownUser.getUniqueId());
        assertNotNull(bookmarkForUnknownUser.getSessionId());
        assertEquals(ResponseStatus.UNKNOWN_PROVIDER.getCode(), bookmarkForUnknownUser.getResponseCode());

    }

    public void testThatResponseIs902WhenProviderStatusIsInvalid() throws IOException, InterruptedException {
        Long callerId = 103l;
        addProvider("remediId2", callerId, NOT_WORKING_PROVIDER);

        String bookmarkURL = getBookmarkRequestUrlWith(callerId, "un1qId", null);
        CustomHttpResponse responseForNotWorkingProvider = httpClient.execute(httpRequestWithAuthHeaders(bookmarkURL, "Get"), new CustomHttpResponseHandler());
        BasicResponse bookmarkForNotWorkingProvider = (BasicResponse) responseToJson(responseForNotWorkingProvider.getContent(), BasicResponse.class);

        assertEquals(HttpStatus.SC_OK, responseForNotWorkingProvider.getStatusCode());
        assertEquals(ResponseStatus.NOT_WORKING_PROVIDER.getCode(), bookmarkForNotWorkingProvider.getResponseCode());

    }

    public void testBookmarkPosting() throws IOException, InterruptedException {
        HttpPost httpPost = (HttpPost) httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/bookmark", TestContext.getJettyPort()), "POST");
        String bookmarkAsJSON = getBookmarkAsJSON();
        httpPost.setEntity(new StringEntity(bookmarkAsJSON));
        CustomHttpResponse response = httpClient.execute(httpPost, new CustomHttpResponseHandler());
        assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());
    }

    public void testThatInitialBookmarkIsReturnedIfBookmarkForExternalIdDoesNotExist() throws IOException, InterruptedException {
        String bookmarkRequestURLForAKnownUser = getBookmarkRequestUrlWith(activeProvider.getCallerId(), "un1qId", "s001");
        CustomHttpResponse responseForKnownUser = httpClient.execute(httpRequestWithAuthHeaders(bookmarkRequestURLForAKnownUser, "Get"), new CustomHttpResponseHandler());
        CourseProgressResponse courseProgressResponseForKnownUser = (CourseProgressResponse) responseToJson(responseForKnownUser.getContent(), CourseProgressResponse.class);
        assertNotNull(courseProgressResponseForKnownUser.getCourseProgress());
    }


    private String getBookmarkAsJSON() throws IOException {

        Chapter chapter = course002.getChapters().get(0);
        Lesson lesson = chapter.getLessons().get(0);

        Flag bookmark = new Flag(String.valueOf(course002.getId()), null, new ContentIdentifier(course002.getId(), UUID.randomUUID().toString(), 1),
                new ContentIdentifier(chapter.getId(), UUID.randomUUID().toString(), 1),
                new ContentIdentifier(lesson.getId(), UUID.randomUUID().toString(), 1), null);
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), bookmark, 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(activeProvider.getCallerId(), "unk001", "ssn001", courseProgress);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(courseProgressPostRequest);
    }

    private Provider addProvider(String remediId, Long callerId, ProviderStatus providerStatus) {
        Provider provider = new Provider(remediId, callerId, providerStatus, new Location("block", "district", "state"));
        providersAdded.add(providerService.createProvider(provider).getId());
        return providerService.getProviderByCallerId(callerId);
    }

    private String getBookmarkRequestUrlWith(long callerId, String uniqueId, String sessionId) {
        if (isNotBlank(sessionId)) {
            return String.format(BOOKMARK_QUERY_WITH_SESSION_ID, TestContext.getJettyPort(), callerId, uniqueId, sessionId);
        }
        return String.format(BOOKMARK_QUERY_WITHOUT_SESSION_ID, TestContext.getJettyPort(), callerId, uniqueId);
    }

    private void removeAllProviders() {
        Provider provider;
        for (Long providerId : providersAdded) {
            provider = providerService.getProviderById(providerId);
            providerService.deleteProvider(provider);
        }
    }

    private MotechResponse responseToJson(String response, Class<? extends MotechResponse> responseType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readValue(parser, responseType);
    }

}
