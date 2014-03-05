package org.motechproject.whp.mtraining.web.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActivationStatusTest {

    @Test
    public void verifyForInvalidActivationStatus() {
        assertFalse(ActivationStatus.isInvalid(ActivationStatus.ACTIVE_RHP.getStatus()));
        assertFalse(ActivationStatus.isInvalid(ActivationStatus.ACTIVE_TPC.getStatus()));
        assertFalse(ActivationStatus.isInvalid(ActivationStatus.NOT_ACTIVATED_RHP.getStatus()));
        assertFalse(ActivationStatus.isInvalid(ActivationStatus.NOT_ACTIVATED_TPC.getStatus()));
        assertFalse(ActivationStatus.isInvalid(ActivationStatus.RHP_TO_RHP.getStatus()));
        assertFalse(ActivationStatus.isInvalid(ActivationStatus.RHP_TO_TPC.getStatus()));
        assertFalse(ActivationStatus.isInvalid(ActivationStatus.RHP_TO_SKY.getStatus()));
        assertFalse(ActivationStatus.isInvalid(null));
        assertTrue(ActivationStatus.isInvalid(ActivationStatus.ELIMINATED_RHP.getStatus()));
        assertTrue(ActivationStatus.isInvalid(ActivationStatus.ELIMINATED_TPC.getStatus()));
        assertTrue(ActivationStatus.isInvalid(ActivationStatus.LEFT_NETWORK_RH.getStatus()));
        assertTrue(ActivationStatus.isInvalid(ActivationStatus.LEFT_NETWORK_TP.getStatus()));
    }

    @Test
    public void verificationOfStatusShouldBeCaseInsensitive() {
        assertFalse(ActivationStatus.isInvalid("active rhp"));
        assertTrue(ActivationStatus.isInvalid("eliminated rhp"));
    }
}
