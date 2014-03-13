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
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.whp.mtraining.CourseBuilder;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponse;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponseHandler;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ActivationStatus;
import org.motechproject.whp.mtraining.web.domain.Bookmark;
import org.motechproject.whp.mtraining.web.domain.BookmarkPostRequest;
import org.motechproject.whp.mtraining.web.domain.BookmarkResponse;
import org.motechproject.whp.mtraining.web.domain.ErrorResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.ACTIVE_RHP;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.ELIMINATED_RHP;

public class BookmarksBundleIT extends AuthenticationAwareIT {

    static final String BOOKMARK_QUERY_WITH_SESSION_ID = "http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s&sessionId=%s";
    static final String BOOKMARK_QUERY_WITHOUT_SESSION_ID = "http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s";
    static final Long CALLER_ID_FOR_BOOKMARK = 67576576l;

    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 10);

    List<Long> providersToBeDeleted = new ArrayList<>();

    private CourseService courseService;

    private BookmarkService bookmarkService;

    private ProviderService providerService;

    private ContentIdentifierDto courseIdentifier;

    @Override
    public void onSetUp() throws InterruptedException, IOException {
        super.onSetUp();

        bookmarkService = (BookmarkService) getService("bookmarkService");
        assertNotNull(bookmarkService);

        courseService = (CourseService) getService("courseService");
        assertNotNull(courseService);

        providerService = (ProviderService) getApplicationContext().getBean("providerService");
        assertNotNull(providerService);

        courseIdentifier = courseService.addOrUpdateCourse(new CourseBuilder().build());

        bookmarkService.addBookmark(CALLER_ID_FOR_BOOKMARK.toString(), courseIdentifier);
    }


    public void testThatStatusUrlIsAccessible() throws IOException, InterruptedException {
        HttpUriRequest httpRequestWithAuthHeaders = httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/status", TestContext.getJettyPort()), "Get");
        HttpResponse httpResponse = httpClient.execute(httpRequestWithAuthHeaders);
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    public void testThatBookmarkUrlIsAvailableWhenProviderIsKnown() throws IOException, InterruptedException {
        String bookmarkURLForUnknownUser = getBookmarkRequestUrlWith(9988776655L, "un1qId", null);
        HttpUriRequest httpGetRequest = httpRequestWithAuthHeaders(bookmarkURLForUnknownUser, "Get");

        CustomHttpResponse responseForUnknownUser = httpClient.execute(httpGetRequest, new CustomHttpResponseHandler());
        assertEquals(HttpStatus.SC_OK, responseForUnknownUser.getStatusCode());

        ErrorResponse bookmarkForUnknownUser = (ErrorResponse) responseToJson(responseForUnknownUser.getContent(), ErrorResponse.class);

        assertEquals(new Long(9988776655l), bookmarkForUnknownUser.getCallerId());
        assertEquals("un1qId", bookmarkForUnknownUser.getUniqueId());
        assertNotNull(bookmarkForUnknownUser.getSessionId());
        assertEquals(ResponseStatus.UNKNOWN_PROVIDER.getCode(), bookmarkForUnknownUser.getResponseStatusCode());


        Long callerId = 102l;
        addProvider(callerId, ACTIVE_RHP);

        String bookmarkRequestURLForAKnownUser = getBookmarkRequestUrlWith(callerId, "un1qId", "s001");
        CustomHttpResponse responseForKnownUser = httpClient.execute(httpRequestWithAuthHeaders(bookmarkRequestURLForAKnownUser, "Get"), new CustomHttpResponseHandler());
        assertEquals(HttpStatus.SC_OK, responseForKnownUser.getStatusCode());

        BookmarkResponse bookmarkForKnownUser = (BookmarkResponse) responseToJson(responseForKnownUser.getContent(), BookmarkResponse.class);

        assertEquals(ResponseStatus.OK.getCode(), bookmarkForKnownUser.getResponseStatusCode());
        assertEquals(callerId, bookmarkForKnownUser.getCallerId());
        assertEquals("un1qId", bookmarkForKnownUser.getUniqueId());
        assertEquals("s001", bookmarkForKnownUser.getSessionId());
        assertEquals("state", bookmarkForKnownUser.getLocation().getState());
        assertEquals("block", bookmarkForKnownUser.getLocation().getBlock());
        assertEquals("district", bookmarkForKnownUser.getLocation().getDistrict());
    }

    public void testThatResponseIs902WhenTheActivationStatusOfProviderIsInvalid() throws IOException, InterruptedException {
        Long callerId = 103l;
        addProvider(callerId, ELIMINATED_RHP);

        String bookmarkURL = getBookmarkRequestUrlWith(callerId, "un1qId", null);
        CustomHttpResponse responseForNotWorkingProvider = httpClient.execute(httpRequestWithAuthHeaders(bookmarkURL, "Get"), new CustomHttpResponseHandler());
        ErrorResponse bookmarkForNotWorkingProvider = (ErrorResponse) responseToJson(responseForNotWorkingProvider.getContent(), ErrorResponse.class);

        assertEquals(HttpStatus.SC_OK, responseForNotWorkingProvider.getStatusCode());
        assertEquals(ResponseStatus.NOT_WORKING_PROVIDER.getCode(), bookmarkForNotWorkingProvider.getResponseStatusCode());

    }

    public void testBookmarkPosting() throws IOException, InterruptedException {
        HttpPost httpPost = (HttpPost) httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/bookmark", TestContext.getJettyPort()), "POST");
        String bookmarkAsJSON = getBookmarkAsJSON();
        httpPost.setEntity(new StringEntity(bookmarkAsJSON));
        CustomHttpResponse response = httpClient.execute(httpPost, new CustomHttpResponseHandler());
        assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());
    }

    private String getBookmarkAsJSON() throws IOException {
        CourseDto courseDto = courseService.getCourse(courseIdentifier);

        ModuleDto moduleDto = courseDto.getModules().get(0);
        ChapterDto chapterDto = moduleDto.getChapters().get(0);
        MessageDto messageDto = chapterDto.getMessages().get(0);

        ContentIdentifierDto module = moduleDto.toContentIdentifierDto();
        ContentIdentifierDto chapter = chapterDto.toContentIdentifierDto();
        ContentIdentifierDto message = messageDto.toContentIdentifierDto();

        Bookmark bookmark = new Bookmark(courseIdentifier, module, chapter, message);
        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(CALLER_ID_FOR_BOOKMARK, "unk001", "ssn001", bookmark);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(bookmarkPostRequest);
    }

    private void addProvider(Long callerId, ActivationStatus activationStatus) {
        Provider provider = new Provider(callerId, new Location("block", "district", "state"), activationStatus);
        Long providerId = providerService.add(provider);
        markForDeletion(providerId);
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
        imports.add("org.apache.http.util");
        imports.add("org.motechproject.whp.mtraining.domain");
        imports.add("org.motechproject.whp.mtraining.web.domain");
        return imports;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"test-blueprint.xml"};
    }

    @Override
    public void onTearDown() throws InterruptedException {
        ProviderService providerService = (ProviderService) getService("providerService");
        assertNotNull(providerService);

        for (Long providerId : providersToBeDeleted) {
            providerService.delete(providerId);
        }
    }

    private Object getService(String serviceBeanName) {
        return getApplicationContext().getBean(serviceBeanName);
    }


    private MotechResponse responseToJson(String response, Class<? extends MotechResponse> responseType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readValue(parser, responseType);
    }

    private void markForDeletion(Long id) {
        providersToBeDeleted.add(id);
    }


}
