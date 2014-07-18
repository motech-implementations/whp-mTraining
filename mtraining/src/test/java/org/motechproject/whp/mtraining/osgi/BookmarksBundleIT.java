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
import org.motechproject.whp.mtraining.constants.CourseStatus;
import org.motechproject.whp.mtraining.dto.BookmarkDto;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.dto.CourseConfigurationDto;
import org.motechproject.whp.mtraining.dto.CourseDto;
import org.motechproject.whp.mtraining.dto.EnrolleeCourseProgressDto;
import org.motechproject.whp.mtraining.dto.LocationDto;
import org.motechproject.whp.mtraining.dto.MessageDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.whp.mtraining.service.CourseService;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.whp.mtraining.CourseDTOBuilder;
import org.motechproject.whp.mtraining.IVRServer;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponse;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponseHandler;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.Bookmark;
import org.motechproject.whp.mtraining.web.domain.CourseProgress;
import org.motechproject.whp.mtraining.web.domain.CourseProgressPostRequest;
import org.motechproject.whp.mtraining.web.domain.CourseProgressResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.WORKING_PROVIDER;


public class BookmarksBundleIT extends AuthenticationAwareIT {

    static final String BOOKMARK_QUERY_WITH_SESSION_ID = "http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s&sessionId=%s";
    static final String BOOKMARK_QUERY_WITHOUT_SESSION_ID = "http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s";

    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 10);

    private List<Long> providersAdded = new ArrayList<>();

    private CourseService courseService;

    private ProviderService providerService;
    private CourseConfigurationService courseConfigService;
    private CourseProgressService courseProgressService;

    private Provider activeProvider;
    protected IVRServer ivrServer;
    private CourseDto course002;


    @Override
    public void onSetUp() throws InterruptedException, IOException {
        super.onSetUp();
        ivrServer = new IVRServer(8888, "/ivr-wgn").start();
        courseService = (CourseService) getService("courseService");
        assertNotNull(courseService);

        providerService = (ProviderService) getApplicationContext().getBean("providerService");
        assertNotNull(providerService);

        courseConfigService = (CourseConfigurationService) getApplicationContext().getBean("courseConfigService");
        assertNotNull(courseConfigService);

        courseProgressService = (CourseProgressService) getApplicationContext().getBean("courseProgressService");
        assertNotNull(courseProgressService);


        String courseName = String.format("CS002-%s", UUID.randomUUID());
        course002 = courseService.getCourse(createCourse(courseName));
        courseConfigService.addOrUpdateCourseConfiguration(new CourseConfigurationDto(course002.getName(), 60, new LocationDto("block", "district", "state")));

        removeAllProviders();
        activeProvider = addProvider("remediId1", 22222L, WORKING_PROVIDER);

        ModuleDto moduleDto = course002.firstActiveModule();
        BookmarkDto bookmarkDto = new BookmarkDto("remediId1", course002.toContentIdentifierDto(), moduleDto.toContentIdentifierDto(), moduleDto.findFirstActiveChapter().toContentIdentifierDto(), moduleDto.findFirstActiveChapter().findFirstActiveMessage().toContentIdentifierDto(), null, DateTime.now());
        EnrolleeCourseProgressDto courseProgressDto = new EnrolleeCourseProgressDto("remediId1", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);
        courseProgressService.addOrUpdateCourseProgress(courseProgressDto);
    }


    private ContentIdentifierDto createCourse(final String courseName) {
        return courseService.addOrUpdateCourse(new CourseDTOBuilder().withName(courseName).build());
    }

    public void testThatStatusURLDoesIsAvailableWithoutAuthentication() throws IOException, InterruptedException {
        HttpResponse response = httpClient.get(String.format("http://localhost:%s/mtraining/web-api/status", TestContext.getJettyPort()));
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

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

        ModuleDto moduleDto = course002.getModules().get(0);
        ChapterDto chapterDto = moduleDto.getChapters().get(0);
        MessageDto messageDto = chapterDto.getMessages().get(0);

        ContentIdentifierDto module = moduleDto.toContentIdentifierDto();
        ContentIdentifierDto chapter = chapterDto.toContentIdentifierDto();
        ContentIdentifierDto message = messageDto.toContentIdentifierDto();

        Bookmark bookmark = new Bookmark(new ContentIdentifierDto(course002.getContentId(), course002.getVersion()), module, chapter, message, null, DateTime.now().toString());
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), bookmark, 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(activeProvider.getCallerId(), "unk001", "ssn001", courseProgress);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(courseProgressPostRequest);
    }

    private Provider addProvider(String remediId, Long callerId, ProviderStatus providerStatus) {
        Provider provider = new Provider(remediId, callerId, providerStatus, new Location("block", "district", "state"));
        providersAdded.add(providerService.add(provider));
        return providerService.byCallerId(callerId);
    }

    private String getBookmarkRequestUrlWith(long callerId, String uniqueId, String sessionId) {
        if (isNotBlank(sessionId)) {
            return String.format(BOOKMARK_QUERY_WITH_SESSION_ID, TestContext.getJettyPort(), callerId, uniqueId, sessionId);
        }
        return String.format(BOOKMARK_QUERY_WITHOUT_SESSION_ID, TestContext.getJettyPort(), callerId, uniqueId);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("org.motechproject.commons.api");
        imports.add("org.apache.http.util");
        imports.add("org.mortbay.jetty");
        imports.add("org.mortbay.jetty.servlet");
        imports.add("javax.servlet");
        imports.add("javax.servlet.http");
        imports.add("org.apache.commons.io");
        imports.add("org.motechproject.whp.mtraining.domain");
        imports.add("org.motechproject.whp.mtraining.web.domain");
        imports.add("org.jasypt.encryption.pbe.config");
        imports.add("org.jasypt.encryption.pbe");
        imports.add("org.jasypt.spring.properties");
        imports.add("org.motechproject.whp.mtraining.mail");
        imports.add("com.google.common.collect");
        return imports;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"test-blueprint.xml"};
    }

    private Object getService(String serviceBeanName) {
        return getApplicationContext().getBean(serviceBeanName);
    }

    @Override
    protected void onTearDown() throws Exception {
        removeAllProviders();
        if (ivrServer != null) {
            ivrServer.stop();
        }
    }

    private void removeAllProviders() {
        for (Long providerId : providersAdded) {
            providerService.delete(providerId);
        }
    }

    private MotechResponse responseToJson(String response, Class<? extends MotechResponse> responseType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readValue(parser, responseType);
    }

}
