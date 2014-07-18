package org.motechproject.whp.mtraining.exception;

import java.util.UUID;

/**
 * Exception thrown when quiz is not found in a chapter
 */

public class InvalidQuizException extends IllegalStateException {

    public InvalidQuizException(UUID quizId) {
        super(String.format("No Quiz with id :%s found ", quizId));
    }
}
