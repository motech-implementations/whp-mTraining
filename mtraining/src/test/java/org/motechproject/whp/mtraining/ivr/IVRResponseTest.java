package org.motechproject.whp.mtraining.ivr;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class IVRResponseTest {

    @Test
    public void shouldIdentifyResponseAsNetworkFailure() {
        IVRResponse response = new IVRResponse(IVRResponseCodes.NETWORK_FAILURE);
        assertTrue(response.isNetworkFailure());
    }
}
