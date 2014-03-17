package org.motechproject.whp.mtraining.exception;

public class MTrainingException extends RuntimeException {

    public MTrainingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MTrainingException(String message) {
        super(message);
    }
}
