package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.BookmarkRequestLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookmarkRequestLogs extends RepositorySupport<BookmarkRequestLog> {

    @Transactional
    public void record(BookmarkRequestLog bookmarkRequestLog) {
        add(bookmarkRequestLog);
    }

    @Override
    Class getType() {
        return BookmarkRequestLog.class;
    }
}
