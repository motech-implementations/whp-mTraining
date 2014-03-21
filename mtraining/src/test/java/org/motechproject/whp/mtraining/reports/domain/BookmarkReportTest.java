package org.motechproject.whp.mtraining.reports.domain;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.UUID;

import static org.junit.Assert.assertThat;


public class BookmarkReportTest {

    @Test
    public void shouldCreateBookmarkReportFromBookmarkDto() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
        DateTime dateModified = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("rmd001", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, dateModified);
        BookmarkReport bookmarkReport = new BookmarkReport(bookmarkDto);
        assertThat(bookmarkReport.getCourseId(), Is.is(bookmarkDto.getCourse().getContentId()));
        assertThat(bookmarkReport.getDateModified(), Is.is(dateModified));
    }
}
