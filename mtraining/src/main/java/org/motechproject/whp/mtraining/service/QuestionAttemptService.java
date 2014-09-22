package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.reports.domain.QuestionAttempt;

import java.util.List;

public interface QuestionAttemptService {

    QuestionAttempt createQuestionAttempt(QuestionAttempt QuestionAttempt);

    QuestionAttempt updateQuestionAttempt(QuestionAttempt QuestionAttempt);

    void deleteQuestionAttempt(QuestionAttempt QuestionAttempt);

    List<QuestionAttempt> getAllQuestionAttempts();

}
