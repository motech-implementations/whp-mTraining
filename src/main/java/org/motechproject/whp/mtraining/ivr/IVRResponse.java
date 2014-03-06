package org.motechproject.whp.mtraining.ivr;

public class IVRResponse {
    private String response;
    private boolean isNetworkFailure;
    private boolean validationFailure;

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
        return validationFailure;
    }

    public void markValidationFailure() {
        this.validationFailure = true;
    }
}
