package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.reports.domain.BookmarkReport;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class AllBookmarkRequestsIT {

    @Autowired
    private AllBookmarkRequests allBookmarkRequests;

    @Before
    public void before() {
        allBookmarkRequests.deleteAll();
    }

    @Test
    public void shouldAddBookmarkRequest() {
        List<BookmarkRequest> all = allBookmarkRequests.all();
        assertThat(all.size(), Is.is(0));

        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto bookmarkDto = new BookmarkDto("rmd001", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, null, contentIdentifierDto, ISODateTimeUtil.nowInTimeZoneUTC());
        allBookmarkRequests.add(new BookmarkRequest("rmd001", 1234567L, "UNQ1", "session01", ResponseStatus.OK, BookmarkRequestType.GET, new BookmarkReport(bookmarkDto)));

        List<BookmarkRequest> savedBookmarkRequestses = allBookmarkRequests.all();
        assertThat(savedBookmarkRequestses.size(), Is.is(1));
        BookmarkRequest bookmarkRequest = savedBookmarkRequestses.get(0);
        assertThat(bookmarkRequest.getCallerId(), Is.is(1234567L));
        assertThat(bookmarkRequest.getSessionId(), Is.is("session01"));
        assertThat(bookmarkRequest.getUniqueId(), Is.is("UNQ1"));
    }

    @After
    public void After() {
        allBookmarkRequests.deleteAll();
    }

}
