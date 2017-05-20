package com.codepath.insync.models.parse;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import static com.facebook.AccessToken.USER_ID_KEY;
//import static com.codepath.insync.models.parse.Event.USER_RELATION_KEY;

@ParseClassName("UserEventRelation")
public class UserEventRelation extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String EVENT_KEY = "event";
    public static final String IS_ATTENDEES_VISIBLE_KEY = "isAttendeesVisible";
    public static final String IS_HOSTING_KEY = "isHosting";
    public static final String RSVP_STATUS_KEY = "rsvpStatus";
    public static final String IS_LOCATION_VISIBLE_KEY = "isLocationVisible";
    public static final String CAN_GET_UPDATES_KEY = "canGetUpdates";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public Event getEvent() {
        return new Event(getParseObject(EVENT_KEY));
    }

//    public Event getUserEvent(String objectId) {
//        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
//        query.whereEqualTo("event", );
//    }

    public boolean isAttendeesVisbile() {
        return getBoolean(IS_ATTENDEES_VISIBLE_KEY);
    }
    public static String getEventPointerKey() {
        return EVENT_KEY;
    }
    public String getUserIdKey() {
        return USER_ID_KEY;
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
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

    public void setUser(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setEvent(Event event) {
        put(EVENT_KEY, event);
    }
    public void setIsAttendeesVisibleKey(boolean isAttendeesVisible) {
        put(IS_ATTENDEES_VISIBLE_KEY, isAttendeesVisible);
    }

    public void setIsHostingKey(boolean isHosting) {
        put(IS_HOSTING_KEY, isHosting);
    }

    public void setCanGetUpdatesKey(boolean canGetUpdates) {
        put(CAN_GET_UPDATES_KEY, canGetUpdates);
    }


    public void setIsLocationVisibleKey(boolean isLocationVisible){
        put(IS_LOCATION_VISIBLE_KEY, isLocationVisible);
    }


    public void setRsvpStatusKey(int rsvpStatus) {
        put(RSVP_STATUS_KEY, rsvpStatus);
    }


    public void updateRsvpStatus(SaveCallback saveCallback) {
        saveInBackground(saveCallback);
    }

    public static void findAttendees(Event event, FindCallback<UserEventRelation> findCallback) {
        ParseQuery<UserEventRelation> query = ParseQuery.getQuery(UserEventRelation.class);
        query.whereEqualTo(EVENT_KEY, event);
        // First try to find from the cache and only then go to network
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(findCallback);
    }

    public static void findUserEvent(Event event, FindCallback<UserEventRelation> findCallback) {
        ParseQuery<UserEventRelation> query = ParseQuery.getQuery(UserEventRelation.class);
        query.whereEqualTo(EVENT_KEY, event);
        query.whereEqualTo("userId", User.getCurrentUser().getObjectId());
        // First try to find from the cache and only then go to network
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(findCallback);
    }

    public UserEventRelation(){

    }

    public UserEventRelation(Event event, String user, boolean isHosting, boolean isAttendeesVisible, boolean isLocationVisible, boolean canGetUpdates, int rsvpStatus){
        this.setEvent(event);
        this.setUserId(user);
        this.setIsHostingKey(isHosting);
        this.setIsAttendeesVisibleKey(isAttendeesVisible);
        this.setIsLocationVisibleKey(isLocationVisible);
        this.setCanGetUpdatesKey(canGetUpdates);
        this.setRsvpStatusKey(rsvpStatus);
        //this.setProfileImage();

    }

}


