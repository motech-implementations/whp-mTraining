package org.motechproject.whp.mtraining.web.domain;

public enum ResponseStatus {
    OK(200, "OK"), UNKNOWN_PROVIDER(401, "Provider Number not recognised");
    private final int code;
    private final String message;

    private ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
