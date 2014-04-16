package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IVRResponseParserTest {

    private static final String SUCCESS_RESPONSE = "{\"responseCode\":800,\"responseMessage\":\"OK\"}";
    private static final String VALIDATION_FAILURE = "{\"responseCode\":1001,\"responseMessage\":\"hello.wav,ch01.wav\"}";

    @Test
    public void shouldParseSuccessfulResponse() throws UnsupportedEncodingException {

        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getEntity()).thenReturn(new StringEntity(SUCCESS_RESPONSE));

        IVRResponse response = new IVRResponseParser().parse(httpResponse);
        assertTrue(response.isSuccess());
    }

    @Test
    public void shouldParseValidationErrorResponse() throws UnsupportedEncodingException {

        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getEntity()).thenReturn(new StringEntity(VALIDATION_FAILURE));

        IVRResponse response = new IVRResponseParser().parse(httpResponse);
        assertFalse(response.isSuccess());
        assertThat(response.hasValidationErrors(), Is.is(true));
        assertThat(response.getResponseMessage(), Is.is("hello.wav,ch01.wav"));
    }
}
