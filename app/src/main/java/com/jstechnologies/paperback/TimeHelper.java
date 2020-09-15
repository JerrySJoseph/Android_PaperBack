package com.jstechnologies.paperback;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class TimeHelper {
    public static String getTimeStringFromMillis(long millis)
    {
        return new DateTime(millis).toString(DateTimeFormat.shortDateTime());
    }

}
