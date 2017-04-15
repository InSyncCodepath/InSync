package com.codepath.insync.utils;

import com.codepath.insync.models.parse.Event;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.parse.ParseQuery.or;


public class DateUtil {
    private static String messageTimeFormat = "hh:mm a";
    private static String eventDateTimeFormat = "EEEEEEEEE, MMM dd yyyy 'at' hh:mma";
    private static int trackBufferHours = 1;
    private static int currentBufferHours = 3;

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
        cal.add(Calendar.HOUR_OF_DAY, -trackBufferHours);
        return (cal.getTime().compareTo(now) <= 0 && endDate.compareTo(now) >= 0);
    }

    public static ParseQuery<Event> getParstEventQuery() {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, currentBufferHours); // adds three buffer hours
        ParseQuery<Event> queryTime = ParseQuery.getQuery(Event.class);
        queryTime.whereLessThan("endDate", cal.getTime());

        ParseQuery<Event> queryEnded = ParseQuery.getQuery(Event.class);
        queryEnded.whereEqualTo("hasEnded", true);

        List<ParseQuery<Event>> queryList = new ArrayList<ParseQuery<Event>>();
        queryList.add(queryTime);
        queryList.add(queryEnded);

        return ParseQuery.or(queryList);
    }

}
