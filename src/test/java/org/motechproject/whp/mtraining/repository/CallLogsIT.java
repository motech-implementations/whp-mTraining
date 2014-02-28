package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.BookmarkRequestLog;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class CallLogsIT {

    @Autowired
    private BookmarkRequestLogs callLogs;

    @Before
    public void before() {
        callLogs.deleteAll();
    }

    @Test
    public void shouldAddCallLogRecord() {
        List<BookmarkRequestLog> all = callLogs.all();
        assertThat(all.size(), Is.is(0));
        callLogs.record(new BookmarkRequestLog(1234567L, "UNQ1", "session01", ResponseStatus.OK));

        List<BookmarkRequestLog> savedBookmarkRequestLogs = callLogs.all();
        assertThat(savedBookmarkRequestLogs.size(), Is.is(1));
        BookmarkRequestLog bookmarkRequestLog = savedBookmarkRequestLogs.get(0);
        assertThat(bookmarkRequestLog.getCallerId(), Is.is(1234567L));
        assertThat(bookmarkRequestLog.getSessionId(), Is.is("session01"));
        assertThat(bookmarkRequestLog.getUniqueId(), Is.is("UNQ1"));
    }
}
