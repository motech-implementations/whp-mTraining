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
        Bookmark bookmarkWithNoCourse = new BookmarkBuilder().withCourse(null).build();
        Bookmark bookmarkWithNoModule = new BookmarkBuilder().withModule(null).build();
        Bookmark bookmarkWithNoChapter= new BookmarkBuilder().withChapter(null).build();
        Bookmark bookmarkWithNoMessageOrQuiz = new BookmarkBuilder().withMessage(null).withQuiz(null).build();

        assertFalse(bookmarkWithValidDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
        assertTrue(bookmarkWithInvalidDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
        assertTrue(bookmarkWithEmptyDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
        assertTrue(bookmarkWithNullDateModified.validate().contains(new ValidationError(INVALID_DATE_TIME)));
        assertTrue(bookmarkWithNoCourse.validate().contains(new ValidationError(910,"Missing Content Id or Version for: Course")));
        assertTrue(bookmarkWithNoModule.validate().contains(new ValidationError(910,"Missing Content Id or Version for: Module")));
        assertTrue(bookmarkWithNoChapter.validate().contains(new ValidationError(910,"Missing Content Id or Version for: Chapter")));
        assertTrue(bookmarkWithNoMessageOrQuiz.validate().contains(new ValidationError(910,"Quiz or Message should be present")));
    }
}
