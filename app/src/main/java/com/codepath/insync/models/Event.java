package com.codepath.insync.models;

import java.util.Date;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;


@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String NAME_KEY = "name";
    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";
    public static final String ADDRESS_KEY = "address";
    public static final String LOCATION_KEY = "location";
    public static final String DESCRIPTION_KEY = "description";
    public static final String PROFILE_IMAGE_KEY = "profileImage";
    public static final String USER_RELATION_KEY = "userRelation";
    public static final String MESSAGE_RELATION_KEY = "messageRelation";
    public static final String ALBUM_RELATION_KEY = "albumRelation";
    public static final String HIGHLIGHTS_VIDEO_KEY = "highlightsVideo";

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

    public ParseRelation getUserRelation() {
        return getRelation(USER_RELATION_KEY);
    }

    public ParseRelation getMessageRelation() {
        return getRelation(MESSAGE_RELATION_KEY);
    }

    public ParseRelation getAlbumRelation() {
        return getRelation(ALBUM_RELATION_KEY);
    }

    public ParseFile getHighlightsVideo() {
        return getParseFile(HIGHLIGHTS_VIDEO_KEY);
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

    public void setUserRelation(ParseRelation userRelation) {
        put(USER_RELATION_KEY, userRelation);
    }

    public void setMessageRelation(ParseRelation messageRelation) {
        put(MESSAGE_RELATION_KEY, messageRelation);
    }

    public void setAlbumRelation(ParseRelation albumRelation) {
        put(ALBUM_RELATION_KEY, albumRelation);
    }

    public void setHighlightsVideo(ParseFile highlightsVideo) {
        put(HIGHLIGHTS_VIDEO_KEY, highlightsVideo);
    }

    // TODO: Call get, saveInBackground and pinInBackground here
}
