package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IVRResponseParserTest {

    private static final String SUCCESS_RESPONSE = "{\"success\":true}";
    private static final String VALIDATION_FAILURE = "{\"success\":false,\"errors\":{\"missingFiles\":\"hello.wav,ch01.wav\"}}";

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
        List<String> missingFiles = response.getMissingFiles();
        assertThat(missingFiles.size(), Is.is(2));
        assertThat(missingFiles.get(0), Is.is("hello.wav"));
        assertThat(missingFiles.get(1), Is.is("ch01.wav"));
    }
}
