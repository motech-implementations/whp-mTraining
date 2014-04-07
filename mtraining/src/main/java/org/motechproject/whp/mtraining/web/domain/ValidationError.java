package org.motechproject.whp.mtraining.web.domain;

public class ValidationError {
    private Integer errorCode;
    private String message;

    public ValidationError(ResponseStatus status) {
        this(status.getCode(), status.getMessage());
    }

    public ValidationError(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object object) {
        ValidationError other = (ValidationError) object;
        return this.errorCode.equals(other.errorCode);
    }

    @Override
    public int hashCode() {
        return errorCode != null ? errorCode.hashCode() : 0;
    }
}
