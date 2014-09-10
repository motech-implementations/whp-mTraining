package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.BookmarkReport;
import org.motechproject.whp.mtraining.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.dto.BookmarkReportDto;
import org.motechproject.whp.mtraining.dto.BookmarkRequestDto;
import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.repository.BookmarkRequestDataService;
import org.motechproject.whp.mtraining.service.BookmarkRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("bookmarkRequestService")
public class BookmarkRequestServiceImpl implements BookmarkRequestService {

    @Autowired
    BookmarkRequestDataService bookmarkRequestDataService;

    @Override
    public BookmarkRequest createBookmarkRequest(BookmarkRequest bookmarkRequest) {
        return bookmarkRequestDataService.create(bookmarkRequest);
    }

    @Override
    public List<BookmarkRequest> getAllBookmarkRequests() {
        return bookmarkRequestDataService.retrieveAll();
    }

    private ContentIdentifierDto convertToDto(ContentIdentifier contentIdentifier) {
        return new ContentIdentifierDto(contentIdentifier.getId(), contentIdentifier.getCreationDate(), contentIdentifier.getModificationDate(),
                contentIdentifier.getUnitId(), contentIdentifier.getContentId(), contentIdentifier.getVersion());
    }

    private BookmarkReportDto convertToDto(BookmarkReport bookmarkReport) {
        return new BookmarkReportDto(bookmarkReport.getId(), bookmarkReport.getCreationDate(), bookmarkReport.getModificationDate(),
                convertToDto(bookmarkReport.getCourseIdentifier()), convertToDto(bookmarkReport.getModuleIdentifier()), convertToDto(bookmarkReport.getChapterIdentifier()),
                        convertToDto(bookmarkReport.getMessageIdentifier()), convertToDto(bookmarkReport.getQuizIdentifier()), bookmarkReport.getDateModified());
    }

    @Override
    public BookmarkRequestDto convertToDto(BookmarkRequest request) {
        return new BookmarkRequestDto(request.getId(), request.getCreationDate(), request.getModificationDate(), request.getCallerId(), request.getUniqueId(),
                request.getSessionId(), request.getResponseCode(), request.getResponseMessage(), request.getRemediId(), request.getRequestType(),
                request.getCourseStartTime(), request.getTimeLeftToCompleteCourseInHrs(), request.getCourseStatus(), convertToDto(request.getBookmarkReport()));
    }

    @Override
    public List<BookmarkRequestDto> convertToDtos(List<BookmarkRequest> bookmarkRequests) {
        List<BookmarkRequestDto> requestDtos = new ArrayList<>();
        for(BookmarkRequest bookmarkRequest : bookmarkRequests) {
            requestDtos.add(convertToDto(bookmarkRequest));
        }
        return requestDtos;
    }

}
