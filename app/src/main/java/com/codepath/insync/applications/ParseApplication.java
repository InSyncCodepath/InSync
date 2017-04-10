package com.codepath.insync.applications;

import android.app.Application;
import android.content.Intent;

import com.codepath.insync.R;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.User;
import com.parse.Parse;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.interceptors.ParseStethoInterceptor;


public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Used in debugging. Needs to be removed for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Registering parse models
        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Message.class);

        // Initialize parse with credentials
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(null)
                .addNetworkInterceptor(new ParseStethoInterceptor())
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(getString(R.string.parse_app_url)).build());

        // Add support for live queries
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE AND UPDATE events
        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<Message>() {
            @Override
            public void onEvents(ParseQuery<Message> query, SubscriptionHandling.Event event, Message object) {
                if (event.equals(SubscriptionHandling.Event.CREATE) ||
                        event.equals(SubscriptionHandling.Event.UPDATE)) {
                    Intent intent = new Intent("com.codepath.insync.Messages");
                    intent.putExtra("new_messages", true);
                    sendBroadcast(intent);
                }
            }
        });
    }


}
