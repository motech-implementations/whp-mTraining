package org.motechproject.whp.mtraining;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.Bookmark;

import java.util.UUID;

public class BookmarkBuilder {

    private ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private String dateModified = ISODateTimeUtil.nowInTimeZoneUTC().toString();


    public BookmarkBuilder withDateModified(String dateModified) {
        this.dateModified = dateModified;
        return this;
    }

    public Bookmark build() {
        return new Bookmark(course, module, chapter, message, dateModified);
    }
}
