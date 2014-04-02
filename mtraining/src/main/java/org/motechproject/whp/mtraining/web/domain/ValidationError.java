package org.motechproject.whp.mtraining.web.domain;

public class ValidationError {
    private final Integer errorCode;

    public ValidationError(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
