package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.repository.BookmarkRequestDataService;
import org.motechproject.whp.mtraining.service.BookmarkRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bookmarkRequestService")
public class BookmarkRequestServiceImpl implements BookmarkRequestService {

    @Autowired
    BookmarkRequestDataService bookmarkRequestDataService;

    @Override
    public BookmarkRequest createBookmarkRequest(BookmarkRequest bookmarkRequest) {
        return bookmarkRequestDataService.create(bookmarkRequest);
    }

}
