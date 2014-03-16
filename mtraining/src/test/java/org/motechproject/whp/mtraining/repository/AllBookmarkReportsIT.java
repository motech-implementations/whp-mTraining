package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.domain.BookmarkReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class AllBookmarkReportsIT {

    @Autowired
    private AllBookmarkReports allBookmarkReports;

    @Test
    public void shouldAddABookmarkReport() {
        assertTrue(allBookmarkReports.all().isEmpty());
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
        String remedyId = "rmd001";
        BookmarkDto bookmarkDto = new BookmarkDto(remedyId, contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, DateTime.now());

        allBookmarkReports.add(new BookmarkReport(remedyId, bookmarkDto));

        List<BookmarkReport> allBookmarkReports = this.allBookmarkReports.all();
        assertThat(allBookmarkReports.size(), Is.is(1));
        assertThat(allBookmarkReports.get(0).getRemedyId(), Is.is(remedyId));
        assertThat(allBookmarkReports.get(0).getMessageId(), Is.is(contentIdentifierDto.getContentId()));
    }

    @Before
    @After
    public void deleteAllBookmarkReports() {
        allBookmarkReports.deleteAll();
    }

}
