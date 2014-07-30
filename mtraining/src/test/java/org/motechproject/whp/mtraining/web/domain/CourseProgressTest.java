package org.motechproject.whp.mtraining.web.domain;

import org.junit.Test;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.BookmarkBuilder;

import static junit.framework.Assert.assertTrue;

public class CourseProgressTest {

    @Test
    public void testValidationErrors() throws Exception {
        Bookmark bookmark = new BookmarkBuilder().build();
        CourseProgress courseProgressWithMissingStartTime = new CourseProgress(null, bookmark, 1000, "STARTED");
        CourseProgress courseProgressWithInvalidStartTime = new CourseProgress("fbdjsbfjdsbjkfdhjks", bookmark, 1000, "STARTED");
        CourseProgress courseProgressWithInvalidBookmark = new CourseProgress(ISODateTimeUtil.nowAsStringInTimeZoneUTC(), null, 1000, "STARTED");
        CourseProgress courseProgressWithBookmarkWithNullCourse = new CourseProgress(ISODateTimeUtil.nowAsStringInTimeZoneUTC(), new BookmarkBuilder().withCourse(0).build(), 1000, "STARTED");
        CourseProgress courseProgressWithInvalidStatus = new CourseProgress(null, bookmark, 1000, null);

        assertTrue(courseProgressWithMissingStartTime.validate().contains(new ValidationError(ResponseStatus.MISSING_COURSE_START_TIME)));
        assertTrue(courseProgressWithInvalidStartTime.validate().contains(new ValidationError(ResponseStatus.INVALID_DATE_TIME)));
        assertTrue(courseProgressWithInvalidBookmark.validate().contains(new ValidationError(ResponseStatus.INVALID_BOOKMARK)));
        assertTrue(courseProgressWithBookmarkWithNullCourse.validate().contains(new ValidationError(910, "Missing Content Id or Version for: Course")));
        assertTrue(courseProgressWithInvalidStatus.validate().contains(new ValidationError(ResponseStatus.INVALID_COURSE_STATUS)));
    }
}
