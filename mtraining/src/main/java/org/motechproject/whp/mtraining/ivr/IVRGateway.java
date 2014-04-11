package org.motechproject.whp.mtraining.ivr;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.WebClient;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.exception.MTrainingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
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

    @Autowired
    public IVRGateway(SettingsFacade settingsFacade, WebClient webClient, IVRResponseParser ivrResponseParser) {
        this.settingsFacade = settingsFacade;
        this.webClient = webClient;
        this.ivrResponseParser = ivrResponseParser;
    }

    public IVRResponse postCourse(Course course) {
        try {
            LOGGER.info(String.format("Publishing Course to IVR Course name %s and version %s", course.getName(),
                    course.getVersion()));
            String courseToPublish = toJson(course);
            LOGGER.debug("Publishing course ...");
            LOGGER.debug(courseToPublish);
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
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, obj);
        } catch (IOException exception) {
            throw new MTrainingException("Error while converting course to JSON", exception);
        }
        return writer.toString();
    }

    //Expect IVR to return 200 as per doc but handling for 201 as well
    private boolean wasCoursePosted(Integer httpStatusCode) {
        return httpStatusCode.equals(HttpStatus.SC_OK) || httpStatusCode.equals(HttpStatus.SC_CREATED);
    }
}
