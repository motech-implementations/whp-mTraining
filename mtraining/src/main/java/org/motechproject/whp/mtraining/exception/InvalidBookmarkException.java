package org.motechproject.whp.mtraining.exception;

/**
 * Exception thrown when there is an error in the bookmark
 */

public class InvalidBookmarkException extends IllegalStateException {
    public InvalidBookmarkException(String message) {
        super(message);
    }
}
