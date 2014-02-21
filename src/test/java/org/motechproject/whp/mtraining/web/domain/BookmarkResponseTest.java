package org.motechproject.whp.mtraining.web.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class BookmarkResponseTest {

    @Test
    public void shouldReturnTrueIfBookmarkResponseHasError() {
        BookmarkResponse bookmarkResponse = new BookmarkResponse(101l, "s001", "uniqueId");
        assertThat(bookmarkResponse.hasError(), Is.is(false));

        bookmarkResponse.setErrorCode(ErrorCode.UNKNOWN);
        assertThat(bookmarkResponse.hasError(), Is.is(true));

    }
}
