package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Question;
import org.springframework.stereotype.Repository;

@Repository
public class AllTestQuestions extends RepositorySupport<Question> {
    @Override
    Class getType() {
        return Question.class;
    }
}
