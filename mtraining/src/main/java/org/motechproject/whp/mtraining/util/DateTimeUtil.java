package org.motechproject.whp.mtraining.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static org.apache.commons.lang.StringUtils.isBlank;

public final class DateTimeUtil {

    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS";

    private DateTimeUtil() {
    }

    public static DateTime parse(String date) {
        if (isBlank(date)) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        return DateTime.parse(date, dateTimeFormatter);
    }

    public static String formatDateTime(DateTime dateTime) {
        return dateTime == null ? null : dateTime.toString(DATE_TIME_FORMAT);
    }
}
