package org.motechproject.whp.mtraining.web.domain;

public enum ResponseStatus {
    OK(800, "OK"),
    UNKNOWN_PROVIDER(901, "Provider Number not recognised"),
    MISSING_CALLER_ID(902, "Missing Caller Id"),
    MISSING_UNIQUE_ID(903, "Missing Unique Id"),
    NOT_WORKING_PROVIDER(904, "Not Working Provider"),
    MISSING_SESSION_ID(905, "Missing Session Id"),
    INVALID_DATE_TIME(906, "Missing or Invalid Bookmark Modified Date"),
    MISSING_TIME(907, "Start time or end time must be present"),
    INVALID_NODE_TYPE(908, "Invalid node type"),
    MISSING_NODE(909, "Missing Node Id or Version"),
    MISSING_QUIZ(910, "Missing Quiz Id or Version"),
    INVALID_QUIZ(911, "Invalid Quiz Id or version"),
    MISSING_QUESTION(912, "No Questions Available");

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

    public boolean isValid() {
        return OK.equals(this);
    }
}
