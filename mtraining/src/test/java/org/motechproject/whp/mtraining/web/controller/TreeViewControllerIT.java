package org.motechproject.whp.mtraining.web.controller;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context-including-mock-osgi-services.xml")
public class TreeViewControllerIT {

    @Autowired
    TreeViewController treeViewController;

    MockMvc mockMvc;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.standaloneSetup(treeViewController).build();
    }

    @Test
    public void shouldPostRelations() throws Exception {

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("test-relations.json");
        assertNotNull(resourceAsStream);
        byte[] postContent = IOUtils.toByteArray(resourceAsStream);

        mockMvc.perform(MockMvcRequestBuilders.post("/updateRelations/1").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .body(postContent)
        ).andExpect(status().isOk());

    }

}
