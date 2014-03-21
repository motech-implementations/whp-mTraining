package org.motechproject.whp.mtraining.web.domain;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.UUID;

import static org.junit.Assert.assertThat;

public class BookmarkTest {

    @Test
    public void shouldCreateBookmarkFromBookmarkDto() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
        DateTime dateModified = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("rmd001", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, dateModified);
        Bookmark bookmark = new Bookmark(bookmarkDto);
        assertThat(bookmark.getCourseIdentifierDto(), Is.is(bookmarkDto.getCourse()));
        assertThat(bookmark.getModuleIdentifierDto(), Is.is(bookmarkDto.getModule()));
        assertThat(bookmark.getChapterIdentifierDto(), Is.is(bookmarkDto.getChapter()));
        assertThat(bookmark.getMessageIdentifierDto(), Is.is(bookmarkDto.getMessage()));
        assertThat(bookmark.getDateModified(), Is.is(bookmarkDto.getDateModified()));
    }
}
