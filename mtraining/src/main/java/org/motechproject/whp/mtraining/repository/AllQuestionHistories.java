package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.QuestionHistory;
import org.springframework.stereotype.Repository;


@Repository
public class AllQuestionHistories extends RepositorySupport<QuestionHistory> {

    public Class getType() {
        return QuestionHistory.class;
    }
}
