package com.codepath.insync.models.parse;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.util.Date;

import static android.R.attr.description;
import static com.codepath.insync.R.string.location;
//import static com.codepath.insync.models.parse.Event.USER_RELATION_KEY;

@ParseClassName("UserEventRelation")
public class UserEventRelation extends ParseObject {
    public static final String USER_KEY = "user";
    public static final String EVENT_KEY = "event";
    public static final String IS_ATTENDEES_VISIBLE_KEY = "isAttendeesVisible";
    public static final String IS_HOSTING_KEY = "isHosting";
    public static final String RSVP_STATUS_KEY = "rsvpStatus";
    public static final String IS_LOCATION_VISIBLE_KEY = "isLocationVisible";
    public static final String CAN_GET_UPDATES_KEY = "canGetUpdates";

    public User getUser() {
        return new User(getParseUser(USER_KEY));
    }

    public Event getEvent() {
        return new Event(getParseObject(EVENT_KEY));
    }

    public boolean isAttendeesVisbile() {
        return getBoolean(IS_ATTENDEES_VISIBLE_KEY);
    }

    public boolean isHosting() {
        return getBoolean(IS_HOSTING_KEY);
    }

    public int getRsvpStatus() {
        return getInt(RSVP_STATUS_KEY);
    }

    public boolean isLocationVisible() {
        return getBoolean(IS_LOCATION_VISIBLE_KEY);
    }

    public boolean canGetUpdates() {
        return getBoolean(CAN_GET_UPDATES_KEY);
    }

    public void setUser(User user) {
        put(USER_KEY, user);
    }

    public void setEvent(Event event) {
        put(EVENT_KEY, event);
    }

    public void setIsAttendeesVisible(boolean isAttendeesVisible) {
        put(IS_ATTENDEES_VISIBLE_KEY, isAttendeesVisible);
    }

    public void setIsHosting(boolean isHosting) {
        put(IS_HOSTING_KEY, isHosting);
    }

    public void setRsvpStatus(int rsvpStatus) {
        put(RSVP_STATUS_KEY, rsvpStatus);
    }

    public void setIsLocationVisible(boolean isLocationVisible) {
        put(IS_LOCATION_VISIBLE_KEY, isLocationVisible);
    }

    public void setCanGetUpdates(boolean canGetUpdates) {
        put(CAN_GET_UPDATES_KEY, canGetUpdates);
    }

    public void updateRsvpStatus(SaveCallback saveCallback) {
        saveInBackground(saveCallback);
    }

    public static void findAttendees(Event event, FindCallback<UserEventRelation> findCallback) {
        ParseQuery<UserEventRelation> query = ParseQuery.getQuery(UserEventRelation.class);
        query.whereEqualTo(EVENT_KEY, event);
        query.include(USER_KEY);
        // First try to find from the cache and only then go to network
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(findCallback);
    }


}


