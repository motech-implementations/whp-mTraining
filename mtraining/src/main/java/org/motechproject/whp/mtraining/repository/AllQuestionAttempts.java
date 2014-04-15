package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.QuestionAttempt;
import org.springframework.stereotype.Repository;


@Repository
public class AllQuestionAttempts extends RepositorySupport<QuestionAttempt> {

    public Class getType() {
        return QuestionAttempt.class;
    }
}
