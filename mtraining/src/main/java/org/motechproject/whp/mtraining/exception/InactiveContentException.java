package org.motechproject.whp.mtraining.exception;

import java.util.UUID;

/**
 * Exception thrown when the content is inactive
 */

public class InactiveContentException extends RuntimeException {

    public InactiveContentException(UUID contentId) {
        super(String.format("Content with Id %s is inactive", contentId));
    }
}
