package org.motechproject.whp.mtraining.web.domain;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.BookmarkBuilder;

import static org.junit.Assert.assertThat;

public class BookmarkTest {

    @Test
    public void shouldValidateDateModified() {
        Bookmark bookmarkWithValidDateModified = new BookmarkBuilder().withDateModified(ISODateTimeUtil.nowAsStringInTimeZoneUTC()).build();

        Bookmark bookmarkWithInvalidDateModified = new BookmarkBuilder().withDateModified("2011-32-33").build();

        Bookmark bookmarkWithEmptyDateModified = new BookmarkBuilder().withDateModified("").build();

        Bookmark bookmarkWithNullDateModified = new BookmarkBuilder().withDateModified(null).build();

        assertThat(bookmarkWithValidDateModified.hasValidModifiedDate(), Is.is(true));

        assertThat(bookmarkWithInvalidDateModified.hasValidModifiedDate(), Is.is(false));

        assertThat(bookmarkWithEmptyDateModified.hasValidModifiedDate(), Is.is(false));

        assertThat(bookmarkWithNullDateModified.hasValidModifiedDate(), Is.is(false));

    }
}
