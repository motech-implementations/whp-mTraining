package org.motechproject.whp.mtraining.web.domain;

import java.util.Arrays;

public enum ActivationStatus {
    ELIMINATED_RHP("Eliminated RHP"),
    ELIMINATED_TPC("Eliminated TPC"),
    ACTIVE_TPC("Active TPC"),
    ACTIVE_RHP("Active RHP"),
    RHP_TO_TPC("RHP converted to TPC"),
    RHP_TO_RHP("RHP converted to RHP"),
    RHP_TO_SKY("RHP converted to SKY"),
    LEFT_NETWORK_TP("Left from network TP"),
    LEFT_NETWORK_RH("Left from network RH"),
    NOT_ACTIVATED_TPC("Not activated TPC"),
    NOT_ACTIVATED_RHP("Not activated RHP");

    private final String status;

    private ActivationStatus(String status) {
        this.status = status.toLowerCase();
    }

    public String getStatus() {
        return status;
    }

    public static boolean isInvalid(String status) {
        return Arrays.asList(ELIMINATED_RHP, ELIMINATED_TPC, LEFT_NETWORK_RH, LEFT_NETWORK_TP).contains(from(status));
    }

    private static ActivationStatus from(String status) {
        ActivationStatus[] values = ActivationStatus.values();
        for (ActivationStatus value : values) {
            if (value.getStatus().equalsIgnoreCase(status))
                return value;
        }
        return null;
    }
}
