package org.motechproject.whp.mtraining.exception;

import java.util.UUID;

/**
 * Exception thrown when question is not found for a quiz
 */

public class InvalidQuestionException extends IllegalStateException {

    public InvalidQuestionException(UUID quizId, UUID questionId) {
        super(String.format("No Question with id :%s found For quiz with id %s", questionId, quizId));
    }
}
