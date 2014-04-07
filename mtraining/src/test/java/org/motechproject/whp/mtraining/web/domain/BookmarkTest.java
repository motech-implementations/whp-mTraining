package org.motechproject.whp.mtraining.web.domain;

import org.junit.Test;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.BookmarkBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;

public class BookmarkTest {

    @Test
    public void shouldValidateDateModified() {
        Bookmark bookmarkWithValidDateModified = new BookmarkBuilder().withDateModified(ISODateTimeUtil.nowAsStringInTimeZoneUTC()).build();
        Bookmark bookmarkWithInvalidDateModified = new BookmarkBuilder().withDateModified("2011-32-33").build();
        Bookmark bookmarkWithEmptyDateModified = new BookmarkBuilder().withDateModified("").build();
        Bookmark bookmarkWithNullDateModified = new BookmarkBuilder().withDateModified(null).build();

        assertFalse(bookmarkWithValidDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
        assertTrue(bookmarkWithInvalidDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
        assertTrue(bookmarkWithEmptyDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
        assertTrue(bookmarkWithNullDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
    }
}
