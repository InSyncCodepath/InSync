package com.codepath.insync.applications;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.codepath.insync.R;
import com.codepath.insync.listeners.OnVideoCreateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.Music;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.DateUtil;
import com.codepath.insync.utils.MediaClient;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.interceptors.ParseStethoInterceptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ParseApplication extends Application implements OnVideoCreateListener {

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

        ParseQuery<Message> messageParseQuery = ParseQuery.getQuery(Message.class);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());

        // Connect to Parse server
        SubscriptionHandling<Message> messageSubscriptionHandling = parseLiveQueryClient.subscribe(messageParseQuery);

        ParseQuery<Event> currentEventQuery = DateUtil.getCurrentEventQuery();
        SubscriptionHandling<Event> cEventSubscriptionHandling = parseLiveQueryClient.subscribe(currentEventQuery);
        cEventSubscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<Event>() {
            @Override
            public void onEvents(ParseQuery<Event> query, SubscriptionHandling.Event event, Event object) {

                Log.d(TAG, "Current event: "+object.getName()+". Current highlights video: " + object.getHighlightsVideo());
                Intent intent = new Intent("com.codepath.insync.Messages");
                intent.putExtra("new_messages", true);
                sendBroadcast(intent);
            }
        });

        ParseQuery<Event> eventParseQuery = DateUtil.getPastEventQuery();
        SubscriptionHandling<Event> pEventSubscriptionHandling = parseLiveQueryClient.subscribe(eventParseQuery);
        pEventSubscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<Event>() {
            @Override
            public void onEvents(ParseQuery<Event> query, SubscriptionHandling.Event event, Event object) {
                Calendar cal = Calendar.getInstance(); // creates calendar
                cal.setTime(new Date()); // sets calendar time/date
                cal.add(Calendar.HOUR_OF_DAY, -3); // adds three buffer hours
                if(!object.hasEnded() && (object.getEndDate().compareTo(cal.getTime()) > 0)) {
                    return;
                }
                Log.d(TAG, "Past event: "+object.getName()+". Current highlights video: " + object.getHighlightsVideo());
                if (object.getHighlightsVideo() == null || object.getHighlightsVideo().trim().length() <= 0) {
                    Log.d(TAG, object.getEndDate().toString());
                    createHighlights(object);
                }
            }
        });
    }

    private void createHighlights(Event event) {
        final List<ParseFile> edImages = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = event.getAlbumRelation().getQuery();

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    for (ParseObject imageObject: objects) {
                        edImages.add(imageObject.getParseFile("image"));
                    }
                } else {
                    Log.e(TAG, "Error fetching event album");
                }
            }
        });
        MediaClient mediaClient = new MediaClient(this, this, event, edImages, "party");
        mediaClient.createHighlights();

    }


    @Override
    public void onCreateSuccess(final Event event, String videoUrl) {
        Log.d(TAG, "Video created successfully. Url: "+videoUrl);
        event.setHasEnded(true);
        event.setHighlightsVideo(videoUrl);
        event.updateEvent(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Event highlights have been successfully created and updated");
                    Intent intent = new Intent("com.codepath.insync.Events");
                    intent.putExtra("event_has_ended", true);
                    intent.putExtra("event_highlights", event.getHighlightsVideo());
                    sendBroadcast(intent);
                } else {
                    Log.d(TAG, "Event highlights creation failed with error: "+e.getLocalizedMessage());
                }
            }
        });


    }

    @Override
    public void onCreateFailure(int status, String message) {
        Log.e(TAG, "Video could not be created. status: "+status+", message: "+message);
        Intent intent = new Intent("com.codepath.insync.Events");
        intent.putExtra("event_has_ended", true);
        sendBroadcast(intent);
    }
}
