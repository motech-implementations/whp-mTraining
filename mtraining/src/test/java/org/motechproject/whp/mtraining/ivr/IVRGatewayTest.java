package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.WebClient;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
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
    }

    @Test
    public void shouldReturnNetworkErrorResponse() throws IOException {
        when(webClient.post(anyString(), anyString())).thenThrow(new IOException("IO exception thrown in tests"));

        CourseDto someCourse = new CourseDto("CS001", "desc", new ContentIdentifierDto(UUID.randomUUID(), 1), null);
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

        when(webClient.post(anyString(), anyString())).thenReturn(httpResponse);

        when(ivrResponseHandler.parse(httpResponse)).thenReturn(new IVRResponse());

        CourseDto someCourse = new CourseDto("CS001", "desc", new ContentIdentifierDto(UUID.randomUUID(), 1), null);

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

        when(webClient.post(anyString(), anyString())).thenReturn(httpResponse);

        IVRResponse response = ivrGateway.postCourse(new CourseDto("CS001", "desc", new ContentIdentifierDto(UUID.randomUUID(), 1), null));

        assertThat(response.getResponseCode(), Is.is(401));
        assertThat(response.getResponseMessage(), Is.is("Not Authenticated"));
    }

}
