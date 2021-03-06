package com.codepath.insync.models.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.Date;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import static android.R.attr.description;


@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String NAME_KEY = "name";
    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";
    public static final String ADDRESS_KEY = "address";
    public static final String LOCATION_KEY = "location";
    public static final String DESCRIPTION_KEY = "description";
    public static final String PROFILE_IMAGE_KEY = "profileImage";
    public static final String MESSAGE_RELATION_KEY = "messageRelation";
    public static final String HIGHLIGHTS_VIDEO_KEY = "highlightsVideo";
    public static final String HAS_ENDED_KEY = "hasEnded";
    public static final String THEME_KEY = "theme";

    public String getName() {
        return getString(NAME_KEY);
    }

    public Date getStartDate() {
        return getDate(START_DATE_KEY);
    }

    public Date getEndDate() {
        return getDate(END_DATE_KEY);
    }

    public String getAddress() {
        return getString(ADDRESS_KEY);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION_KEY);
    }

    public String getDescription() {
        return getString(DESCRIPTION_KEY);
    }

    public ParseFile getProfileImage() {
        return getParseFile(PROFILE_IMAGE_KEY);
    }

    public ParseRelation<Message> getMessageRelation() {
        return getRelation(MESSAGE_RELATION_KEY);
    }

    public String getHighlightsVideo() {
        return getString(HIGHLIGHTS_VIDEO_KEY);
    }

    public String getTheme() {
        return getString(THEME_KEY);
    }

    public boolean hasEnded() {
        return getBoolean(HAS_ENDED_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public void setStartDate(Date startDate) {
        put(START_DATE_KEY, startDate);
    }

    public void setEndDate(Date endDate) {
        put(END_DATE_KEY, endDate);
    }

    public void setAddress(String address) {
        put(ADDRESS_KEY, address);
    }

    public void setLocation(ParseGeoPoint location) {
        put(LOCATION_KEY, location);
    }

    public void setDescription(String description) {
        put(DESCRIPTION_KEY, description);
    }

    public void setProfileImage(ParseFile profileImage) {
        put(PROFILE_IMAGE_KEY, profileImage);
    }

    public void setMessageRelation(ParseRelation<Message> messageRelation) {
        put(MESSAGE_RELATION_KEY, messageRelation);
    }

    public void setHighlightsVideo(String highlightsVideo) {
        put(HIGHLIGHTS_VIDEO_KEY, highlightsVideo);
    }

    public void setHasEnded(boolean hasEnded) {
        put(HAS_ENDED_KEY, hasEnded);
    }


    public void setTheme(String theme) {
        put(THEME_KEY, theme);
    }

    public Bitmap getProfileImageBitmap() {
        ParseFile profileImg = getProfileImage();
        Bitmap bitmap = null;
        if (profileImg != null) {
            try {
                bitmap = BitmapFactory.decodeStream(profileImg.getDataStream());
            } catch (ParseException | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public Event(){}

    public Event(ParseObject object) {
        super("Event");
        this.setName(object.getString(NAME_KEY));
        this.setAddress(object.getString(ADDRESS_KEY));
        this.setStartDate(object.getDate(START_DATE_KEY));
        this.setEndDate(object.getDate(END_DATE_KEY));
        this.setDescription(object.getString(DESCRIPTION_KEY));
        this.setLocation(object.getParseGeoPoint(LOCATION_KEY));
        //this.setProfileImage(object.getParseFile(PROFILE_IMAGE_KEY));
    }

    public void updateEvent(SaveCallback saveCallback) {
        saveInBackground(saveCallback);
    }

    // TODO: Call get, saveInBackground and pinInBackground here
    public static Event newEventInstance(String eventName, String address, Date startDate, Date endDate, String description, ParseGeoPoint location){
        Event newEvent = new Event();
        newEvent.setName(eventName);
        newEvent.setAddress(address);
        newEvent.setStartDate(startDate);
        newEvent.setEndDate(endDate);
        newEvent.setDescription(description);
        newEvent.setLocation(location);
        return newEvent;
    }
    public Event(String eventName, String address, Date startDate, Date endDate, String description, ParseGeoPoint location){
        this.setName(eventName);
        this.setAddress(address);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setDescription(description);
        this.setLocation(location);
        //this.setProfileImage();

    }

    public Event(String eventName, String address, Date startDate, Date endDate, String description, ParseGeoPoint location, ParseFile profilePic, String theme){
        this.setName(eventName);
        this.setAddress(address);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setDescription(description);
        this.setLocation(location);
        this.setProfileImage(profilePic);
        if (theme != null) {
            setTheme(theme);
        }

    }

    public static void findEvent(String eventId, GetCallback<Event> getCallback) {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        // First try to find from the cache and only then go to network
        /* Unsupported method when local data store is enabled */
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getInBackground(eventId, getCallback);

    }


}
