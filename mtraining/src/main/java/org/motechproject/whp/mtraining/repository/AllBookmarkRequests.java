package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.BookmarkRequest;
import org.springframework.stereotype.Repository;

@Repository
public class AllBookmarkRequests extends RepositorySupport<BookmarkRequest> {

    @Override
    Class getType() {
        return BookmarkRequest.class;
    }
}
