package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.QuizHistory;
import org.springframework.stereotype.Repository;


@Repository
public class AllQuizHistories extends RepositorySupport<QuizHistory> {

    public Class getType() {
        return QuizHistory.class;
    }
}
