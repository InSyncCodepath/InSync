package com.codepath.insync.models.parse;

import java.util.Date;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;


@ParseClassName("Event")
public class Event extends ParseObject {
    private static final String NAME_KEY = "name";
    private static final String START_DATE_KEY = "startDate";
    private static final String END_DATE_KEY = "endDate";
    private static final String ADDRESS_KEY = "address";
    private static final String LOCATION_KEY = "location";
    private static final String DESCRIPTION_KEY = "description";
    private static final String PROFILE_IMAGE_KEY = "profileImage";
    private static final String MESSAGE_RELATION_KEY = "messageRelation";
    private static final String HIGHLIGHTS_VIDEO_KEY = "highlightsVideo";
    private static final String HAS_ENDED_KEY = "hasEnded";
    private static final String THEME_KEY = "theme";

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


    public void setHighlightsVideo(String highlightsVideo) {
        put(HIGHLIGHTS_VIDEO_KEY, highlightsVideo);
    }

    public void setHasEnded(boolean hasEnded) {
        put(HAS_ENDED_KEY, hasEnded);
    }


    public void setTheme(String theme) {
        put(THEME_KEY, theme);
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
    }

    public void updateEvent(SaveCallback saveCallback) {
        saveInBackground(saveCallback);
    }

    public Event(String eventName, String address, Date startDate, Date endDate, String description, ParseGeoPoint location){
        this.setName(eventName);
        this.setAddress(address);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setDescription(description);
        this.setLocation(location);

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
        query.getInBackground(eventId, getCallback);

    }


}
