package org.motechproject.whp.mtraining.osgi;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponse;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponseHandler;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.BookmarkResponse;
import org.motechproject.whp.mtraining.web.domain.ErrorCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WHPmTrainingBundleIT extends BaseOsgiIT {

    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 10);

    private List<Long> providersToBeDeleted = new ArrayList<>();

    public void testThatStatusUrlIsAccessible() throws IOException, InterruptedException {
        HttpResponse httpResponse = httpClient.get(String.format("http://localhost:%s/mtraining/web-api/status", TestContext.getJettyPort()));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    public void testThatProviderServiceIsAvailable() {
        ProviderService providerService = (ProviderService) getApplicationContext().getBean("providerService");
        assertNotNull(providerService);
    }


    public void testThatBookmarkUrlIsAvailableWhenProviderIsKnown() throws IOException, InterruptedException {
        CustomHttpResponse responseForUnknownUser = httpClient.get(String.format("http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s&sessionId=s001",
                TestContext.getJettyPort(), 9988776655L, "un1qId"), new CustomHttpResponseHandler());
        assertEquals(HttpStatus.SC_OK, responseForUnknownUser.getStatusCode());

        BookmarkResponse bookmarkForUnknownUser = responseToJson(responseForUnknownUser.getContent());

        assertEquals(new Long(9988776655l), bookmarkForUnknownUser.getCallerId());
        assertEquals("un1qId", bookmarkForUnknownUser.getUniqueId());
        assertEquals("s001", bookmarkForUnknownUser.getSessionId());
        assertEquals(ErrorCode.UNKNOWN.name(), bookmarkForUnknownUser.getErrorCode());


        Long callerId = 102l;
        addProvider(callerId);

        String url = String.format("http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s&sessionId=s001",
                TestContext.getJettyPort(), callerId, "un1qId");
        CustomHttpResponse responseForKnownUser =
                httpClient.get(url, new CustomHttpResponseHandler());
        assertEquals(HttpStatus.SC_OK, responseForKnownUser.getStatusCode());

        BookmarkResponse bookmarkForKnownUser = responseToJson(responseForKnownUser.getContent());

        assertEquals(callerId, bookmarkForKnownUser.getCallerId());
        assertEquals("un1qId", bookmarkForKnownUser.getUniqueId());
        assertEquals("s001", bookmarkForKnownUser.getSessionId());
        assertEquals("state", bookmarkForKnownUser.getState());
        assertEquals("block", bookmarkForKnownUser.getBlock());
        assertEquals("district", bookmarkForKnownUser.getDistrict());
        assertNull(bookmarkForKnownUser.getErrorCode());
    }

    private void addProvider(Long callerId) {
        ProviderService providerService = (ProviderService) getApplicationContext().getBean("providerService");

        Provider provider = new Provider(callerId);
        provider.setLocation(new Location("vill", "post", "block", "district", "state", 561900));
        Long providerId = providerService.add(provider);
        markForDeletion(providerId);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("org.apache.http.util");
        imports.add("org.motechproject.whp.mtraining.web.domain");
        return imports;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"test-blueprint.xml"};
    }

    private BookmarkResponse responseToJson(String response) throws IOException {
        System.out.println(response);
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readValue(parser, BookmarkResponse.class);
    }

    private void markForDeletion(Long id) {
        providersToBeDeleted.add(id);
    }

    @Override
    protected void onTearDown() throws Exception {
        ProviderService providerService = (ProviderService) getApplicationContext().getBean("providerService");
        if (providerService != null) {
            for (Long providerId : providersToBeDeleted) {
                providerService.delete(providerId);
            }
            providersToBeDeleted.clear();
        }
    }
}
