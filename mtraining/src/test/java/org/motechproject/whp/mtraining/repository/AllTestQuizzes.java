package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Quiz;
import org.springframework.stereotype.Repository;

@Repository
public class AllTestQuizzes extends RepositorySupport<Quiz> {
    @Override
    Class getType() {
        return Quiz.class;
    }
}
