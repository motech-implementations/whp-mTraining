package org.motechproject.whp.mtraining.ivr;

public class IVRResponse {
    private String response;
    private boolean isNetworkFailure;
    private boolean isValidationFailure;

    public IVRResponse(String response) {
        this.response = response;
    }

    public void markNetworkFailure() {
        this.isNetworkFailure = true;
    }

    public Boolean isNetworkFailure() {
        return isNetworkFailure;
    }

    public boolean isValidationFailure() {
        return isValidationFailure;
    }

    public void markValidationFailure() {
        this.isValidationFailure = true;
    }

    public boolean isSuccess() {
        return !isNetworkFailure && !isValidationFailure;
    }
}
