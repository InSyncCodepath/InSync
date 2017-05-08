package com.codepath.insync.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import com.codepath.insync.R;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.models.parse.Event;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.codepath.insync.R.id.rlEventDetail;
import static com.parse.ParseQuery.or;


public class CommonUtil {
    private static String messageTimeFormat = "hh:mm a";
    private static String eventDateTimeFormat = "EEEEEEEEE, MMM dd yyyy 'at' hh:mma";
    private static int trackBufferHours = 1;
    private static int currentBufferHours = -3;

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

    public static ParseQuery<Event> getCurrentEventQuery() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, currentBufferHours); // adds three buffer hours
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereGreaterThanOrEqualTo("endDate", cal.getTime());
        query.whereNotEqualTo("hasEnded", true);
        return query;
    }

    public static ParseQuery<Event> getPastEventQuery() {
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

    public static String getRelativeTimeAgo(Date inputDate) {


        String relativeDate = "";

        long dateMillis = inputDate.getTime();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long currentMidnightMillis = c.getTimeInMillis();
        if (dateMillis > currentMidnightMillis) {
            return "Today";
        }
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }

    public static boolean compareDates(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date1).equals(sdf.format(date2));

    }

    public static void sendInviteLink(String phoneNum, String eventId) {
        String inviteLink =  "https://play.google.com/store/apps/details?id=com.codepath.insync&referrer=phone_num%3D"+phoneNum+"%26event_id%3D"+eventId;
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("phoneNumber", phoneNum);
        payload.put("message", inviteLink);
        ParseCloud.callFunctionInBackground("sendUserMessage", payload, new FunctionCallback<Object>() {

            @Override
            public void done(Object object, ParseException e) {
                if (e != null) {
                    Log.e("sendInviteLink", "Error sending sms push to cloud: " + e.toString());
                } else {
                    Log.d("sendInviteLink", "SMS Push sent successfully!");
                }
            }
        });
    }

    public static void createSnackbar(View parentView, Context context, String message) {
        Snackbar snackBar = Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackBar.getView();
        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.accent));
        snackBar.show();
    }

    public static void createSnackbar(View parentView, Context context, String message, int color) {
        Snackbar snackBar = Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackBar.getView();
        layout.setBackgroundColor(ContextCompat.getColor(context, color));
        snackBar.show();
    }
}
