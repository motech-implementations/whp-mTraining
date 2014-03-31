package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.CourseBuilder;
import org.motechproject.whp.mtraining.WebClient;
import org.motechproject.whp.mtraining.domain.Course;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IVRGatewayTest {

    SettingsFacade settingsFacade;
    IVRResponseParser ivrResponseHandler;
    WebClient webClient;

    IVRGateway ivrGateway;

    @Before
    public void before() {
        settingsFacade = mock(SettingsFacade.class);
        ivrResponseHandler = mock(IVRResponseParser.class);
        webClient = mock(WebClient.class);
        ivrGateway = new IVRGateway(settingsFacade, webClient, ivrResponseHandler);

        when(settingsFacade.getProperty("ivr.api.key.name")).thenReturn("X-ApiKey");
        when(settingsFacade.getProperty("ivr.api.key.value")).thenReturn("Abcd1234!");
        when(settingsFacade.getProperty("ivr.url")).thenReturn("http://ivr.url");
    }

    @Test
    public void shouldReturnNetworkErrorResponse() throws IOException {
        when(webClient.post(anyString(), anyString(), any(Properties.class))).thenThrow(new IOException("IO exception thrown in tests"));

        Course someCourse = new CourseBuilder().build();
        IVRResponse ivrResponse = ivrGateway.postCourse(someCourse);

        assertThat(ivrResponse.isNetworkFailure(), Is.is(true));
    }

    @Test
    public void shouldDelegateToResponseHandlerToHandleIVRResponse() throws IOException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"responseCode\":800,\"responseMessage\":\"OK\"}"));

        when(settingsFacade.getProperty("ivr.url")).thenReturn("http://ivr.url");

        when(webClient.post(anyString(), anyString(), any(Properties.class))).thenReturn(httpResponse);

        when(ivrResponseHandler.parse(httpResponse)).thenReturn(new IVRResponse());

        Course someCourse = new CourseBuilder().build();

        ivrGateway.postCourse(someCourse);

        verify(ivrResponseHandler).parse(httpResponse);
    }


    @Test
    public void shouldHandleHttpResponseOtherThanOk() throws IOException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(401);
        when(statusLine.getReasonPhrase()).thenReturn("Not Authenticated");

        when(httpResponse.getStatusLine()).thenReturn(statusLine);

        when(webClient.post(anyString(), anyString(), any(Properties.class))).thenReturn(httpResponse);

        Course someCourse = new CourseBuilder().build();
        IVRResponse response = ivrGateway.postCourse(someCourse);

        assertThat(response.getResponseCode(), Is.is(401));
        assertThat(response.getResponseMessage(), Is.is("Not Authenticated"));
    }

    @Test
    public void shouldSendIvrAPIKeyAsHttpHeaders() throws IOException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"responseCode\":800,\"responseMessage\":\"OK\"}"));


        when(settingsFacade.getProperty("ivr.api.key.name")).thenReturn("X-ApiKey");
        when(settingsFacade.getProperty("ivr.api.key.value")).thenReturn("RRRAbcd12345!");

        Properties headers = new Properties();
        headers.put("X-ApiKey", "RRRAbcd12345!");

        when(webClient.post(anyString(), anyString(), eq(headers))).thenReturn(httpResponse);


        Course someCourse = new CourseBuilder().build();
        ivrGateway.postCourse(someCourse);


        verify(webClient).post(eq("http://ivr.url"), anyString(), eq(headers));
    }


}
