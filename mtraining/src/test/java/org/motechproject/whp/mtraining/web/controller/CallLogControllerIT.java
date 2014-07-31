package org.motechproject.whp.mtraining.web.controller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.TestContext;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.CallDurationService;
import org.motechproject.whp.mtraining.service.CallLogService;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallLogControllerIT extends BasePaxIT {

    @Inject
    private ProviderService providerService;

    @Inject
    private CallLogService callLogService;

    @Inject
    private CallDurationService callDurationService;

    private Long providerId;

    private static final String USER_NAME = "test-username";
    private static final String USER_PASSWORD = "test-password";
    private static final String CALLLOG_URL = "http://localhost:%d/motech-platform-server/module/mtraining/web-api/callLog";
    @Test
    public void shouldCreateProvider() throws Exception {
        Provider provider = providerService.createProvider(new Provider("r003", 9934793802l, ProviderStatus.WORKING_PROVIDER, new Location("Bihar")));
        assertThat(provider, IsNot.not(null));
    }

    @Test
    public void shouldPostCallLogs() throws Exception {

        assertThat(callDurationService.getAllCallDurations().size(), Is.is(0));
        assertThat(callLogService.getAllCallLog().size(), Is.is(0));

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("test-call-log-post.json");
        assertNotNull(resourceAsStream);
        String postContent = IOUtils.toString(resourceAsStream);

        HttpPost request = new HttpPost(String.format(CALLLOG_URL, TestContext.getJettyPort()));
        addAuthHeader(request, USER_NAME, USER_PASSWORD);

        StringEntity entity = new StringEntity(postContent, "application/json", "UTF-8");
        request.setEntity(entity);

        HttpResponse response = getHttpClient().execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());

        assertThat(callDurationService.getAllCallDurations().size(), Is.is(1));
        assertThat(callLogService.getAllCallLog().size(), Is.is(3));
    }

    private void addAuthHeader(HttpUriRequest request, String userName, String password) {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

}
