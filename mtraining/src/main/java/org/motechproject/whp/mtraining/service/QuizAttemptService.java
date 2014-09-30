package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;

import java.util.List;

public interface QuizAttemptService {

    QuizAttempt createQuizAttempt(QuizAttempt QuizAttempt);

    QuizAttempt updateQuizAttempt(QuizAttempt QuizAttempt);

    void deleteQuizAttempt(QuizAttempt QuizAttempt);

    List<QuizAttempt> getAllQuizAttempts();

}
