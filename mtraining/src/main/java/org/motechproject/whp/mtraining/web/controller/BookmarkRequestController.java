package org.motechproject.whp.mtraining.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.motechproject.whp.mtraining.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.domain.views.PropertyFilterMixIn;
import org.motechproject.whp.mtraining.dto.BookmarkRequestDto;
import org.motechproject.whp.mtraining.exception.MTrainingException;
import org.motechproject.whp.mtraining.service.BookmarkReportService;
import org.motechproject.whp.mtraining.service.BookmarkRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.print.Book;
import java.util.List;

@Controller
public class BookmarkRequestController {

    @Autowired
    BookmarkRequestService bookmarkRequestService;

    @Autowired
    BookmarkReportService bookmarkReportService;

    @RequestMapping("/bookmarkRequests")
    @ResponseBody
    public List<BookmarkRequest> getAllBookmarkRequests() {
        List<BookmarkRequest> bookmarkRequests = bookmarkRequestService.getAllBookmarkRequests();
        for(BookmarkRequest request : bookmarkRequests) {
            if (request.getBookmarkReport() != null) {
                request.setBookmarkReport(bookmarkReportService.getBookmarkReport(request.getBookmarkReport().getId()));
            }
        }
        return bookmarkRequests;
    }
    
}
