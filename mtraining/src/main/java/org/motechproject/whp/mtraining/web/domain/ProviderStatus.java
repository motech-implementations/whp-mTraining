package org.motechproject.whp.mtraining.web.domain;

public enum ProviderStatus {
    NOT_WORKING_PROVIDER("not working provider"),
    WORKING_PROVIDER("working provider");

    private final String status;

    private ProviderStatus(String status) {
        this.status = status.toLowerCase();
    }

    public String getStatus() {
        return status;
    }

    public static boolean isInvalid(String status) {
        return NOT_WORKING_PROVIDER.getStatus().equalsIgnoreCase(status);
    }

    public static ProviderStatus from(String status) {
        ProviderStatus[] values = ProviderStatus.values();
        for (ProviderStatus value : values) {
            if (value.getStatus().equalsIgnoreCase(status))
                return value;
        }
        return null;
    }
}
