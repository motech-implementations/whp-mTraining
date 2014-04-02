package org.motechproject.whp.mtraining;

import org.joda.time.DateTime;
import org.motechproject.mtraining.constants.CourseStatus;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.Bookmark;

import java.util.UUID;

public class BookmarkBuilder {

    private ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto quiz = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private String dateModified = ISODateTimeUtil.nowInTimeZoneUTC().toString();
    private String externalId = "RMD001";
    private String courseStatus = "ONGOING";


    public BookmarkBuilder withDateModified(String dateModified) {
        this.dateModified = dateModified;
        return this;
    }

    public BookmarkBuilder withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public Bookmark build() {
        return new Bookmark(course, module, chapter, message, quiz, dateModified, courseStatus);
    }

    public BookmarkDto buildDTO() {
        return new BookmarkDto(externalId, course, module, chapter, message, quiz, DateTime.now(), CourseStatus.enumFor(courseStatus));
    }
}
