package com.codepath.insync.models.parse;


import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


@ParseClassName("User")
public class User extends ParseUser {
    private static final String NAME_KEY = "name";
    private static final String PROFILE_IMAGE_KEY = "profileImage";
    private static final String LOCATION_KEY = "location";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static final String USERNAME_KEY = "username";
    private static User currentUser;

    public static String getUsernameKey() {
        return USERNAME_KEY;
    }

    public static User getUser(String name) throws ParseException {

        ParseQuery<ParseUser> query = User.getQuery();
        query.whereEqualTo("name", name);
        List<ParseUser> objects = query.find();
        ParseUser parseUser = objects.get(0);
        return new User(parseUser);
    }

    public User(ParseObject parseObject) {
        setObjectId(parseObject.getObjectId());

    }

    public static User getCurrentUser() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (currentUser == null && parseUser != null) {
            currentUser = new User(parseUser);
        }
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
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
        setObjectId(parseUser.getObjectId());
        setUsername(parseUser.getUsername());
        if (parseUser.getEmail() != null) {
            setEmail(parseUser.getEmail());
        }

        if (parseUser.getString(NAME_KEY) != null) {
            setName(parseUser.getString(NAME_KEY));
        }

        if (parseUser.getParseFile(PROFILE_IMAGE_KEY) != null) {
            setProfileImage(parseUser.getParseFile(PROFILE_IMAGE_KEY));
        }
        if (parseUser.getParseGeoPoint(LOCATION_KEY) != null) {
            setLocation(parseUser.getParseGeoPoint(LOCATION_KEY));
        }
        if (parseUser.getString(PHONE_NUMBER_KEY) != null) {
            setPhoneNumber(parseUser.getString(PHONE_NUMBER_KEY));
        }
    }

    public void signup(SignUpCallback callback) {
        super.signUpInBackground(callback);
    }

    public void login(String username, String password, LogInCallback callback) {
        logInInBackground(username, password, callback);
    }

    public static void findUser(String objectId, GetCallback<ParseUser> getCallback) {
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.getInBackground(objectId, getCallback);
    }

}