package com.codepath.insync.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;


@ParseClassName("User")
public class User extends ParseUser {
    public static final String PROFILE_IMAGE_KEY = "profileImage";
    public static final String LOCATION_KEY = "location";
    public static final String EVENT_RELATION_KEY = "eventRelation";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";

    public ParseFile getProfileImage() {
        return getParseFile(PROFILE_IMAGE_KEY);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION_KEY);
    }

    public ParseRelation getEventRelation() {
        return getRelation(EVENT_RELATION_KEY);
    }

    public String getPhoneNumber() {
        return getString(PHONE_NUMBER_KEY);
    }

    public void setProfileImage(ParseFile profileImage) {
        put(PROFILE_IMAGE_KEY, profileImage);
    }

    public void setLocation(ParseGeoPoint location) {
        put(LOCATION_KEY, location);
    }

    public void setEventRelation(ParseRelation eventRelation) {
        put(EVENT_RELATION_KEY, eventRelation);
    }

    public void setPhoneNumber(String phoneNumber) {
        put(PHONE_NUMBER_KEY, phoneNumber);
    }

    public User() {
        super();
    }

    public User(ParseUser parseUser) {
        setUsername(parseUser.getUsername());
        //setEmail(parseUser.getEmail());
        setObjectId(parseUser.getObjectId());
    }
    // TODO: Call get, saveInBackground and pinInBackground here
}