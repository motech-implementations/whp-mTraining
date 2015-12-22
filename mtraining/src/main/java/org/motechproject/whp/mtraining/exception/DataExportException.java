package org.motechproject.whp.mtraining.exception;

public class DataExportException extends RuntimeException {

    public DataExportException() {
    }

    public DataExportException(String message) {
        super(message);
    }

    public DataExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
