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
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
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
import org.motechproject.whp.mtraining.web.domain.BookmarkPostRequest;
import org.motechproject.whp.mtraining.web.domain.BookmarkResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private ContentIdentifierDto courseIdentifier;
    private Provider activeProvider;
    protected IVRServer ivrServer;


    @Override
    public void onSetUp() throws InterruptedException, IOException {
        super.onSetUp();
        ivrServer = new IVRServer(8888, "/ivr-wgn").start();
        courseService = (CourseService) getService("courseService");
        assertNotNull(courseService);

        providerService = (ProviderService) getApplicationContext().getBean("providerService");
        assertNotNull(providerService);

        courseIdentifier = courseService.addOrUpdateCourse(new CourseDTOBuilder().build());
        removeAllProviders();
        activeProvider = addProvider("remediId1", 22222L, WORKING_PROVIDER);
    }


    public void testThatStatusUrlIsAccessible() throws IOException, InterruptedException {
        HttpUriRequest httpRequestWithAuthHeaders = httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/status", TestContext.getJettyPort()), "Get");
        HttpResponse httpResponse = httpClient.execute(httpRequestWithAuthHeaders);
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    public void testThatResponseIs800WhenProviderIsKnown() throws IOException, InterruptedException {
        String bookmarkRequestURLForAKnownUser = getBookmarkRequestUrlWith(activeProvider.getCallerId(), "un1qId", "s001");
        CustomHttpResponse responseForKnownUser = httpClient.execute(httpRequestWithAuthHeaders(bookmarkRequestURLForAKnownUser, "Get"), new CustomHttpResponseHandler());
        BookmarkResponse bookmarkForKnownUser = (BookmarkResponse) responseToJson(responseForKnownUser.getContent(), BookmarkResponse.class);

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
        BookmarkResponse bookmarkForKnownUser = (BookmarkResponse) responseToJson(responseForKnownUser.getContent(), BookmarkResponse.class);
        assertNotNull(bookmarkForKnownUser.getBookmark());
    }

    private String getBookmarkAsJSON() throws IOException {
        CourseDto courseDto = courseService.getCourse(courseIdentifier);

        ModuleDto moduleDto = courseDto.getModules().get(0);
        ChapterDto chapterDto = moduleDto.getChapters().get(0);
        MessageDto messageDto = chapterDto.getMessages().get(0);

        ContentIdentifierDto module = moduleDto.toContentIdentifierDto();
        ContentIdentifierDto chapter = chapterDto.toContentIdentifierDto();
        ContentIdentifierDto message = messageDto.toContentIdentifierDto();

        Bookmark bookmark = new Bookmark(courseIdentifier, module, chapter, message, null, "ONGOING");
        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(activeProvider.getCallerId(), "unk001", "ssn001", bookmark);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(bookmarkPostRequest);
    }

    private Provider addProvider(String remediId, Long callerId, ProviderStatus providerStatus) {
        //this provider copy gets detached once saved,hence need to retrieve
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
