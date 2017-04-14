package com.codepath.insync.models.parse;

import com.parse.LogInCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


@ParseClassName("User")
public class User extends ParseUser {
    public static final String NAME_KEY = "name";
    public static final String PROFILE_IMAGE_KEY = "profileImage";
    public static final String LOCATION_KEY = "location";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static User currentUser;

    public static User getCurrentUser() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (currentUser == null && parseUser != null) {
            currentUser = new User(parseUser);
        }
        return currentUser;
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public ParseFile getProfileImage() {
        return getParseFile(PROFILE_IMAGE_KEY);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION_KEY);
    }

    public String getPhoneNumber() {
        return getString(PHONE_NUMBER_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public void setProfileImage(ParseFile profileImage) {
        put(PROFILE_IMAGE_KEY, profileImage);
    }

    public void setLocation(ParseGeoPoint location) {
        put(LOCATION_KEY, location);
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



    public void signup(SignUpCallback callback) {
        super.signUpInBackground(callback);
    }

    public void login(String username, String password, LogInCallback callback) {
        super.logInInBackground(username, password, callback);
    }

}