package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.Bookmark;
import org.motechproject.whp.mtraining.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class CallLogsIT {

    @Autowired
    private CallLogs callLogs;

    @Before
    public void before() {
        callLogs.deleteAll();
    }

    @Test
    public void shouldAddCallLogRecord() {
        List<CallLog> all = callLogs.all();
        assertThat(all.size(), Is.is(0));
        Bookmark bookmark = new Bookmark();
        callLogs.record(new CallLog(1234567L, "UNQ1", "session01", null));

        List<CallLog> savedCallLogs = callLogs.all();
        assertThat(savedCallLogs.size(), Is.is(1));
        CallLog callLog = savedCallLogs.get(0);
        assertThat(callLog.getCallerId(), Is.is(1234567L));
        assertThat(callLog.getSessionId(), Is.is("session01"));
        assertThat(callLog.getUniqueId(), Is.is("UNQ1"));
    }
}
