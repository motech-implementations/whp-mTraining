package org.motechproject.whp.mtraining.osgi;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponse;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponseHandler;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.ACTIVE_RHP;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.ELIMINATED_RHP;

public class WHPmTrainingBundleIT extends BaseOsgiIT {

    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 10);

    private List<Long> providersToBeDeleted = new ArrayList<>();

    public void ThatStatusUrlIsAccessible() throws IOException, InterruptedException {
        HttpResponse httpResponse = httpClient.get(String.format("http://localhost:%s/mtraining/web-api/status", TestContext.getJettyPort()));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    public void testThatProviderServiceIsAvailable() {
        ProviderService providerService = (ProviderService) getApplicationContext().getBean("providerService");
        assertNotNull(providerService);
    }


    public void ThatBookmarkUrlIsAvailableWhenProviderIsKnown() throws IOException, InterruptedException {
        CustomHttpResponse responseForUnknownUser = httpClient.get(String.format("http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s",
                TestContext.getJettyPort(), 9988776655L, "un1qId"), new CustomHttpResponseHandler());
        assertEquals(HttpStatus.SC_OK, responseForUnknownUser.getStatusCode());

        ErrorResponse bookmarkForUnknownUser = (ErrorResponse) responseToJson(responseForUnknownUser.getContent(), ErrorResponse.class);

        assertEquals(new Long(9988776655l), bookmarkForUnknownUser.getCallerId());
        assertEquals("un1qId", bookmarkForUnknownUser.getUniqueId());
        assertNotNull(bookmarkForUnknownUser.getSessionId());
        assertEquals(ResponseStatus.UNKNOWN_PROVIDER.getCode(), bookmarkForUnknownUser.getResponseStatusCode());


        Long callerId = 102l;
        addProvider(callerId, ACTIVE_RHP);

        String url = String.format("http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s&sessionId=s001",
                TestContext.getJettyPort(), callerId, "un1qId");
        CustomHttpResponse responseForKnownUser =
                httpClient.get(url, new CustomHttpResponseHandler());
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

    public void ThatResponseIs902WhenTheActivationStatusOfProviderIsInvalid() throws IOException, InterruptedException {
        Long callerId = 102l;
        addProvider(callerId, ELIMINATED_RHP);

        CustomHttpResponse responseForNotWorkingProvider = httpClient.get(String.format("http://localhost:%s/mtraining/web-api/bookmark?callerId=%s&uniqueId=%s",
                TestContext.getJettyPort(), callerId, "un1qId"), new CustomHttpResponseHandler());
        ErrorResponse bookmarkForNotWorkingProvider = (ErrorResponse) responseToJson(responseForNotWorkingProvider.getContent(), ErrorResponse.class);

        assertEquals(HttpStatus.SC_OK, responseForNotWorkingProvider.getStatusCode());
        assertEquals(ResponseStatus.NOT_WORKING_PROVIDER.getCode(), bookmarkForNotWorkingProvider.getResponseStatusCode());

    }

    private void addProvider(Long callerId, ActivationStatus activationStatus) {
        ProviderService providerService = (ProviderService) getApplicationContext().getBean("providerService");
        Provider provider = new Provider(callerId, new Location("block", "district", "state"), activationStatus);
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

    private MotechResponse responseToJson(String response, Class<? extends MotechResponse> responseType) throws IOException {
        System.out.println(response);
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readValue(parser, responseType);
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
