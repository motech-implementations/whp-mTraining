package org.motechproject.whp.mtraining.ivr;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.WebClient;
import org.motechproject.whp.mtraining.domain.views.PublishCourseView;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.exception.MTrainingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class IVRGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(IVRGateway.class);

    public static final String IVR_URL = "ivr.url";
    public static final String IVR_API_KEY_NAME = "ivr.api.key.name";
    public static final String IVR_API_KEY_VALUE = "ivr.api.key.value";
    private SettingsFacade settingsFacade;
    private WebClient webClient;
    private IVRResponseParser ivrResponseParser;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public IVRGateway(SettingsFacade settingsFacade, WebClient webClient, IVRResponseParser ivrResponseParser) {
        this.settingsFacade = settingsFacade;
        this.webClient = webClient;
        this.ivrResponseParser = ivrResponseParser;
    }

    public IVRResponse postCourse(CoursePlanDto course) {
        try {
            LOGGER.info(String.format("Publishing Course to IVR Course name %s", course.getName()));
            String courseToPublish = toJson(course);
            LOGGER.info("Publishing course ...");
            LOGGER.info(courseToPublish);
            HttpResponse response = webClient.post(getIVRUrl(), courseToPublish, getHeaders());
            StatusLine statusLine = response.getStatusLine();
            LOGGER.info(String.format("Course publish response status : %s", statusLine.getStatusCode()));
            if (!wasCoursePosted(statusLine.getStatusCode())) {
                return new IVRResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            return ivrResponseParser.parse(response);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return new IVRResponse(IVRResponseCodes.NETWORK_FAILURE, "Network Failure while trying to connect to " + getIVRUrl() + ".");
        }
    }

    private Properties getHeaders() {
        Properties properties = new Properties();
        String headerName = settingsFacade.getProperty(IVR_API_KEY_NAME);
        String headerValue = settingsFacade.getProperty(IVR_API_KEY_VALUE);
        properties.put(headerName, headerValue);
        return properties;
    }

    private String getIVRUrl() {
        return settingsFacade.getProperty(IVR_URL);
    }

    private String toJson(Object obj) {
        objectMapper.configure(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION, false);
        ObjectWriter objectWriter = objectMapper.writerWithView(PublishCourseView.class);
        try {
            String json = objectWriter.writeValueAsString(obj);
            return json;
        } catch (Exception exception) {
            throw new MTrainingException("Error while converting course to JSON", exception);
        }
    }

    //Expect IVR to return 200 as per doc but handling for 201 as well
    private boolean wasCoursePosted(Integer httpStatusCode) {
        return httpStatusCode.equals(HttpStatus.SC_OK) || httpStatusCode.equals(HttpStatus.SC_CREATED);
    }
}
