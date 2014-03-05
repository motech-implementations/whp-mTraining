package org.motechproject.whp.mtraining.web.domain;

public enum ResponseStatus {
    OK(800, "OK"),
    UNKNOWN_PROVIDER(901, "Provider Number not recognised"),
    MISSING_CALLER_ID(902, "Missing Caller Id"),
    MISSING_UNIQUE_ID(903, "Missing Unique Id"),
    NOT_WORKING_PROVIDER(902, "Not a working provider");

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
