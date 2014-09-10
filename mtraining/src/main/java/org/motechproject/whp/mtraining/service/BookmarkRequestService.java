package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.dto.BookmarkRequestDto;

import java.util.List;

public interface BookmarkRequestService {

    BookmarkRequest createBookmarkRequest(BookmarkRequest bookmarkRequest);

    List<BookmarkRequest> getAllBookmarkRequests();

    BookmarkRequestDto convertToDto(BookmarkRequest bookmarkRequest);

    List<BookmarkRequestDto> convertToDtos(List<BookmarkRequest> bookmarkRequests);

}
