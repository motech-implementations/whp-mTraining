package org.motechproject.whp.mtraining.web.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProviderStatusTest {

    @Test
    public void verifyForInvalidProviderStatus() {
        assertFalse(ProviderStatus.isInvalid(ProviderStatus.WORKING_PROVIDER.getStatus()));
        assertFalse(ProviderStatus.isInvalid(null));
        assertTrue(ProviderStatus.isInvalid(ProviderStatus.NOT_WORKING_PROVIDER.getStatus()));
    }

    @Test
    public void verificationOfStatusShouldBeCaseInsensitive() {
        assertFalse(ProviderStatus.isInvalid("working provider"));
        assertTrue(ProviderStatus.isInvalid("not working provider"));
    }
}
