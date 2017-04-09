package com.codepath.insync.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtil {
    private static String messageTimeFormat = "hh:mm a";

    private static String eventDateTimeFormat = "EEEEEEEEE, MMM dd yyyy 'at' hh:mma";

    public static String getTimeInFormat(Date inputDate) {
        SimpleDateFormat osdf = new SimpleDateFormat(messageTimeFormat, Locale.US);
        return osdf.format(inputDate);
    }

    public static String getDateTimeInFormat(Date inputDate) {
        SimpleDateFormat osdf = new SimpleDateFormat(eventDateTimeFormat, Locale.US);
        return osdf.format(inputDate);
    }

}
