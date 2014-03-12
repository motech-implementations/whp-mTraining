package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.whp.mtraining.exception.MTrainingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IVRResponseParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(IVRResponseParser.class);

    public IVRResponse parse(HttpResponse httpResponse) {
        String ivrResponseAsString = "not parsed yet";
        try {
            ivrResponseAsString = EntityUtils.toString(httpResponse.getEntity());
            LOGGER.info(String.format("Attempting to parse IVR response %s", ivrResponseAsString));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonFactory jsonFactory = objectMapper.getJsonFactory();
            JsonParser jsonParser = jsonFactory.createJsonParser(ivrResponseAsString);
            return jsonParser.readValueAs(IVRResponse.class);
        } catch (IOException e) {
            LOGGER.error(String.format("Response returned %s", ivrResponseAsString));
            throw new MTrainingException("Could not parse httpResponse", e);
        }
    }
}
