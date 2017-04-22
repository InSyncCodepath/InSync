package com.codepath.insync.applications;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.codepath.insync.R;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.Music;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.CommonUtil;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.interceptors.ParseStethoInterceptor;

import java.util.Calendar;
import java.util.Date;


public class ParseApplication extends Application {

    public static String TAG = "ParseApplication";
    @Override
    public void onCreate() {
        super.onCreate();

        // Used in debugging. Needs to be removed for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Registering parse models
        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Music.class);
        ParseObject.registerSubclass(UserEventRelation.class);

        // Initialize parse with credentials
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(null)
                .addNetworkInterceptor(new ParseStethoInterceptor())
                .addNetworkInterceptor(new ParseLogInterceptor())
                //.enableLocalDataStore()
                .server(getString(R.string.parse_app_url)).build());

        // Add support for live queries
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());

        // Connect to Parse server
        SubscriptionHandling<ParseUser> locationSubscriptionHandling = parseLiveQueryClient.subscribe(userParseQuery);

        // Listen for UPDATE events
        locationSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<ParseUser>() {
                    @Override
                    public void onEvent(ParseQuery<ParseUser> query, ParseUser object) {
                        Log.d(TAG, "User update received. Broadcasting.");
                        ParseGeoPoint location = object.getParseGeoPoint("location");
                        Intent intent = new Intent("com.codepath.insync.Users");
                        intent.putExtra("userId", object.getObjectId());
                        intent.putExtra("userLatitude", location.getLatitude());
                        intent.putExtra("userLongitude", location.getLongitude());
                        sendBroadcast(intent);
                    }
                });

        ParseQuery<Event> currentEventQuery = CommonUtil.getCurrentEventQuery();
        SubscriptionHandling<Event> cEventSubscriptionHandling = parseLiveQueryClient.subscribe(currentEventQuery);
        cEventSubscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<Event>() {
            @Override
            public void onEvents(ParseQuery<Event> query, SubscriptionHandling.Event event, Event object) {
                Calendar cal = Calendar.getInstance(); // creates calendar
                cal.setTime(new Date()); // sets calendar time/date
                cal.add(Calendar.HOUR_OF_DAY, -3); // adds three buffer hours
                if (object.hasEnded() || (object.getEndDate().compareTo(cal.getTime()) < 0)) {
                    return;
                }
                Log.d(TAG, "Current event: "+object.getName()+". Current highlights video: " + object.getHighlightsVideo());
                Intent intent = new Intent("com.codepath.insync.Messages");
                intent.putExtra("new_messages", true);
                sendBroadcast(intent);
            }
        });
    }
}
