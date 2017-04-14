package com.codepath.insync.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtil {
    private static String messageTimeFormat = "hh:mm a";
    private static String eventDateTimeFormat = "EEEEEEEEE, MMM dd yyyy 'at' hh:mma";
    private static int bufferHours = 1;

    public static String getTimeInFormat(Date inputDate) {
        SimpleDateFormat osdf = new SimpleDateFormat(messageTimeFormat, Locale.US);
        return osdf.format(inputDate);
    }

    public static String getDateTimeInFormat(Date inputDate) {
        SimpleDateFormat osdf = new SimpleDateFormat(eventDateTimeFormat, Locale.US);
        return osdf.format(inputDate);
    }

    public static boolean canTrackGuests(Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(startDate);
        cal.add(Calendar.HOUR_OF_DAY, -bufferHours);
        return (cal.getTime().compareTo(now) <= 0 && endDate.compareTo(now) >= 0);
    }

}
