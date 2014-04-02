package org.motechproject.whp.mtraining.web.domain;

public interface MotechResponse {

    Long getCallerId();

    String getSessionId();

    /**
     * Unique Id sent by IVR
     *
     * @return
     */
    String getUniqueId();

    Integer getResponseCode();

    String getResponseMessage();
}
