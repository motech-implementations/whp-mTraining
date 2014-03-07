package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.WebClient;
import org.motechproject.whp.mtraining.exception.MTrainingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

@Component
public class IVRGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(IVRGateway.class);

    public static final String IVR_URL = "ivr.url";
    private SettingsFacade settingsFacade;
    private WebClient webClient;
    private IVRResponseParser ivrResponseParser;

    @Autowired
    public IVRGateway(SettingsFacade settingsFacade, WebClient webClient, IVRResponseParser ivrResponseParser) {
        this.settingsFacade = settingsFacade;
        this.webClient = webClient;
        this.ivrResponseParser = ivrResponseParser;
    }

    public IVRResponse postCourse(CourseDto course) {
        try {
            HttpResponse response = webClient.post(getIVRUrl(), toJson(course));
            return ivrResponseParser.parse(response);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            IVRResponse ivrResponse = new IVRResponse();
            ivrResponse.markNetworkFailure();
            return ivrResponse;
        }
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
}
