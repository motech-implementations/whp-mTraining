package org.motechproject.whp.mtraining.ivr;

public enum PublishingResult {
    SUCCESS(true);
    private boolean isSuccess;

    private PublishingResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
