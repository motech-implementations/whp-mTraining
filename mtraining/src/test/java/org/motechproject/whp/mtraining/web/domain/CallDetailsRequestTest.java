package org.motechproject.whp.mtraining.web.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;


public class CallDetailsRequestTest {

    @Test
    public void shouldMarkErrorIfCallerIdIsMissing() {
        ResponseStatus response = new CallDetailsRequest(null, "uni", null).validate();
        assertEquals(response, MISSING_CALLER_ID);
    }

    @Test
    public void shouldMarkErrorIfUniqueIdIsMissing() {
        ResponseStatus response = new CallDetailsRequest(123l, "", "ssn001").validate();
        assertEquals(response, MISSING_UNIQUE_ID);
    }

}
