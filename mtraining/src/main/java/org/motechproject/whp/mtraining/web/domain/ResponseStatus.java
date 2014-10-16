package org.motechproject.whp.mtraining.web.domain;

public enum ResponseStatus {
    OK(800, "OK"),
    UNKNOWN_PROVIDER(901, "Provider Number not recognised"),
    MISSING_CALLER_ID(902, "Missing Caller Id"),
    MISSING_UNIQUE_ID(903, "Missing Unique Id"),
    NOT_WORKING_PROVIDER(904, "Not Working Provider"),
    MISSING_SESSION_ID(905, "Missing Session Id"),
    INVALID_DATE_TIME(906, "Invalid Date specified"),
    MISSING_TIME(907, "Start time or end time must be present"),
    INVALID_CALL_LOG_TYPE(908, "Invalid content type"),
    MISSING_COURSE_ID(909, "Missing Course Id"),
    MISSING_NODE(910, "Missing Content Id or Version"),
    MISSING_QUIZ(911, "Missing Quiz Id or Version"),
    QUIZ_NOT_FOUND(912, "Invalid Quiz Id or version"),
    MISSING_QUESTION(913, "No Questions Available"),
    INVALID_FLAG(914, "Invalid flag"),
    INVALID_CALL_STATUS(915, "Restarted flag must be set to true or false"),
    MISSING_CONTENT_ID(916, "Missing Content Id"),
    MISSING_VERSION(917, "Missing Version"),
    MISSING_QUESTION_ID(918, "Missing Question Id or version"),
    INVALID_QUESTION(919, "Could not find some of the questions"),
    INVALID_COURSE_STATUS(920, "Invalid course status"),
    MISSING_CALL_LOG_CONTENT(921, "Missing contents for call log"),
    INVALID_COURSE(922, "Invalid Course Id or version"),
    INVALID_MODULE(923, "Invalid Module Id or version"),
    INVALID_CHAPTER(924, "Invalid Chapter Id or version"),
    MISSING_COURSE_START_TIME(925, "Course Start Time must be present"),
    COURSE_NOT_FOUND(926, "No course available"),
    LOCATION_NOT_ASSOCIATED_WITH_PROVIDER(927, "Location is not associated with Provider"),
    COURSE_NOT_PUBLISHED(928, "Course is not published");


    private int code;
    private String message;

    private ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid() {
        return OK.equals(this);
    }

    public static ResponseStatus statusFor(Integer code) {
        for (ResponseStatus responseStatus : values()) {
            if (responseStatus.getCode().equals(code)) {
                return responseStatus;
            }
        }
        return null;
    }
}
