package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.WebClient;

import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IVRGatewayTest {

    @Test
    public void shouldReturnNetworkErrorResponse() throws IOException {
        SettingsFacade settingsFacade = mock(SettingsFacade.class);

        WebClient webClient = mock(WebClient.class);
        IVRResponseParser ivrResponseHandler = mock(IVRResponseParser.class);

        when(webClient.post(anyString(), anyString())).thenThrow(new IOException("IO exception thrown in tests"));

        IVRGateway ivrGateway = new IVRGateway(settingsFacade, webClient, ivrResponseHandler);

        CourseDto someCourse = new CourseDto();
        IVRResponse ivrResponse = ivrGateway.postCourse(someCourse);

        assertThat(ivrResponse.isNetworkFailure(), Is.is(true));
    }

    @Test
    public void shouldDelegateToResponseHandlerForNonExceptionalCases() throws IOException {
        IVRResponseParser ivrResponseHandler = mock(IVRResponseParser.class);

        WebClient webClient = mock(WebClient.class);

        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"error\":true}"));

        when(webClient.post(anyString(), anyString())).thenReturn(httpResponse);

        when(ivrResponseHandler.parse(httpResponse)).thenReturn(new IVRResponse());

        IVRGateway ivrGateway = new IVRGateway(mock(SettingsFacade.class), webClient, ivrResponseHandler);

        CourseDto someCourse = new CourseDto();

        ivrGateway.postCourse(someCourse);

        verify(ivrResponseHandler).parse(httpResponse);
    }

}
