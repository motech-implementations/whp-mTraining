package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;
import org.springframework.stereotype.Repository;


@Repository
public class AllQuizAttempts extends RepositorySupport<QuizAttempt> {

    public Class getType() {
        return QuizAttempt.class;
    }
}
