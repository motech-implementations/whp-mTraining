package org.motechproject.whp.mtraining.web.controller;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.AllCallDurations;
import org.motechproject.whp.mtraining.repository.AllCallLogs;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context-including-mock-osgi-services.xml")
public class CallLogControllerIT {

    @Autowired
    AllCallLogs allCallLogs;

    @Autowired
    AllCallDurations allCallDurations;

    @Autowired
    CallLogController callLogController;

    @Autowired
    ProviderService providerService;


    MockMvc mockMvc;
    private Long providerId;

    @Before
    public void before() {
        clear();
        providerId = providerService.add(new Provider("r002", 9934793809l, ProviderStatus.WORKING_PROVIDER, new Location("Bihar")));
        mockMvc = MockMvcBuilders.standaloneSetup(callLogController).build();
    }

    @Test
    public void shouldPostCallLogs() throws Exception {

        assertThat(allCallDurations.all().size(), Is.is(0));
        assertThat(allCallLogs.all().size(), Is.is(0));

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("test-call-log-post.json");
        assertNotNull(resourceAsStream);
        byte[] postContent = IOUtils.toByteArray(resourceAsStream);

        mockMvc.perform(MockMvcRequestBuilders.post("/callLog").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .body(postContent)
        ).andExpect(status().isOk());

        assertThat(allCallDurations.all().size(), Is.is(1));
        assertThat(allCallLogs.all().size(), Is.is(3));

    }

    private void clear() {
        allCallLogs.deleteAll();
        allCallDurations.deleteAll();
    }

    @After
    public void after() {
        clear();
        if (providerId != null) {
            providerService.delete(providerId);
        }
    }
}
