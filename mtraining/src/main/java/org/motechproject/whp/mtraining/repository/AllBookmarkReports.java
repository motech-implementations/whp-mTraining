package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.BookmarkReport;
import org.springframework.stereotype.Repository;

@Repository
public class AllBookmarkReports extends RepositorySupport<BookmarkReport> {

    @Override
    Class getType() {
        return BookmarkReport.class;
    }
}
