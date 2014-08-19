package org.motechproject.whp.mtraining.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Utility class to parse datetime in ISO 8601 format yyyy-MM-ddTHH:mm:ss.SSSZ
 * eg. 2011-01-19T18:30:01.001Z
 * yyyy : Year
 * MM : month of year
 * dd : day of month
 * HH : hour of day
 * mm : minute
 * ss : seconds
 * SSS : milliseconds
 * Z : denotes Zulu or UTC timezone
 */
public final class ISODateTimeUtil {

    private ISODateTimeUtil() {
    }

    /**
     * @param date (in ISO 8601 format)
     * @return DateTime instance
     */
    public static DateTime parse(String date) {
        if (isBlank(date)) {
            return null;
        }
        return ISODateTimeFormat.dateTime().parseDateTime(date);
    }

    /**
     * @param date (in ISO 8601 format)
     * @return DateTime instance in UTC time zone
     */
    public static DateTime parseWithTimeZoneUTC(String date) {
        if (isBlank(date)) {
            return null;
        }
        return ISODateTimeFormat.dateTime().withZoneUTC().parseDateTime(date);
    }

    /**
     * @param dateTime
     * @return String representation of dateTime in ISO 8601 format in UTC zone
     */
    public static String asStringInTimeZoneUTC(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        if (DateTimeZone.UTC.equals(dateTime.getZone())) {
            return ISODateTimeFormat.dateTime().print(dateTime);
        }
        return ISODateTimeFormat.dateTime().withZoneUTC().print(dateTime);
    }

    /**
     * @return current time in ISO8601 format and UTC time zone
     */
    public static DateTime nowInTimeZoneUTC() {
        return DateTime.now().withZone(DateTimeZone.UTC);
    }

    /**
     * @return current time in ISO8601 format as string
     * DateTime.now().withZone(DateTimeZone.UTC) gives dateTime which is not in the required format
     */
    public static String nowAsStringInTimeZoneUTC() {
        return nowInTimeZoneUTC().toString();
    }

    /**
     * Validates if given datetime is in ISO format
     *
     * @param dateAsString
     * @return
     */
    public static boolean validate(String dateAsString) {
        try {
            DateTime dateTime = parse(dateAsString);
            return dateTime != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
