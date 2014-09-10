package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.BookmarkReport;
import org.motechproject.whp.mtraining.repository.BookmarkReportDataService;
import org.motechproject.whp.mtraining.service.BookmarkReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bookmarkReportService")
public class BookmarkReportServiceImpl implements BookmarkReportService {

    @Autowired
    BookmarkReportDataService bookmarkReportDataService;

    @Override
    public BookmarkReport getBookmarkReport(long id) {
        return bookmarkReportDataService.findById(id);
    }

}