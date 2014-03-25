package org.motechproject.whp.mtraining.ivr;


import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.IVRServer;
import org.motechproject.whp.mtraining.RequestInfo;
import org.motechproject.whp.mtraining.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class IVRGatewayIT {


    private IVRServer ivrServer;

    @Autowired
    private SettingsFacade settingsFacade;

    IVRResponseParser ivrResponseHandler = new IVRResponseParser();

    @Before
    public void before() {
        //port should be same as given in url in test-mtraining.properties
        ivrServer = new IVRServer(8888, "/ivr");
        ivrServer.start();
    }

    @Test
    public void shouldPostCourseJSONToIVR() throws IOException {
        List<ModuleDto> modules = new ArrayList<>();
        CourseDto course = new CourseDto(UUID.randomUUID(), 1, true, "CS001", "Description", "Created By", modules);

        new IVRGateway(settingsFacade, new WebClient(), ivrResponseHandler).postCourse(course);

        RequestInfo requestInfo = ivrServer.detailForRequest("/ivr");
        String post_data = requestInfo.getRequestData().get("post_body");
        assertThat(requestInfo, IsNull.notNullValue());
        assertThat(post_data, Is.is(toJson(course)));
    }

    @After
    public void after() throws InterruptedException {
        ivrServer.stop();
    }

    private String toJson(Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        objectMapper.writeValue(writer, obj);
        return writer.toString();
    }

}
