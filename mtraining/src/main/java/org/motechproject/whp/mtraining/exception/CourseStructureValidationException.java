package org.motechproject.whp.mtraining.exception;

/**
 * Exception thrown when there is a validation error in the course structure
 */

public class CourseStructureValidationException extends RuntimeException {
    public CourseStructureValidationException(String message) {
        super(message);
    }
}
