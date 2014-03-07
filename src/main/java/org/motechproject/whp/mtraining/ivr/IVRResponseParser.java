package org.motechproject.whp.mtraining.ivr;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.whp.mtraining.exception.MTrainingException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IVRResponseParser {
    public IVRResponse parse(HttpResponse httpResponse) {
        try {
            String ivrResponseAsString = EntityUtils.toString(httpResponse.getEntity());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonFactory jsonFactory = objectMapper.getJsonFactory();
            JsonParser jsonParser = jsonFactory.createJsonParser(ivrResponseAsString);
            IVRResponse response = jsonParser.readValueAs(IVRResponse.class);
            if(!response.hasValidationErrors()){
                response.markSuccess();
            }
            return response;
        } catch (IOException e) {
            throw new MTrainingException("Could not parse httpResponse", e);
        }
    }
}
