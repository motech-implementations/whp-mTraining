package org.motechproject.whp.mtraining.web.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class IVRRequestTest {

    @Test
    public void shouldValidateIVRRequest() {
        IVRRequest ivrRequest = new IVRRequest(11234l, "sessionId", "uniqueId");
        List<ValidationError> validationErrors = ivrRequest.validate();
        assertTrue(validationErrors.isEmpty());
    }

    @Test
    public void shouldReturnValidationError() {
        IVRRequest ivrRequest = new IVRRequest(null, "", "");
        List<ValidationError> validationErrors = ivrRequest.validate();
        assertThat(validationErrors.size(), Is.is(3));
        assertThat(validationErrors.get(0).getErrorCode(), Is.is(ResponseStatus.MISSING_CALLER_ID.getCode()));
        assertThat(validationErrors.get(1).getErrorCode(), Is.is(ResponseStatus.MISSING_SESSION_ID.getCode()));
        assertThat(validationErrors.get(2).getErrorCode(), Is.is(ResponseStatus.MISSING_UNIQUE_ID.getCode()));
    }

}
