package org.motechproject.whp.mtraining.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Deserializer for DateTime representation in UI
 */
public class CustomDateDeserializer extends JsonDeserializer<DateTime> {

    private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern("yyyy-MM-dd H:mm:ss");

    @Override
    public DateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        return formatter.parseDateTime(parser.getText());
    }

}
