package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Chapter;
import org.springframework.stereotype.Repository;

@Repository
public class AllTestChapters extends RepositorySupport<Chapter> {
    @Override
    Class getType() {
        return Chapter.class;
    }
}
