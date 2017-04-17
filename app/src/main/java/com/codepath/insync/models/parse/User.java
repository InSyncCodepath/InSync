package com.codepath.insync.models.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


@ParseClassName("User")
public class User extends ParseUser {
    public static final String NAME_KEY = "name";
    public static final String PROFILE_IMAGE_KEY = "profileImage";
    public static final String LOCATION_KEY = "location";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    public static final String USERNAME_KEY = "username";
    private static User currentUser;
    private static User user;

    public static String getUsernameKey() {
        return USERNAME_KEY;
    }

    public static User getUser(String username) throws ParseException {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
//        query.whereEqualTo("user", user);
//        List<ParseObject> objects = query.find();
//        ParseObject parseObject = objects.get(0);
        //user = new User(parseObject);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        List<ParseUser> objects = query.find();
        ParseUser parseUser = objects.get(0);
        user = new User(parseUser);

//                findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> objects, ParseException e) {
//                user = (User) objects.get(0);
//            }
//
//        });
        return user;
    }
    public User(ParseObject parseObject) {
        //setEmail(parseUser.getEmail());
        setObjectId(parseObject.getObjectId());

    }
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

    public Bitmap getProfileImageBitmap() {
        ParseFile profileImg = getProfileImage();
        Bitmap bitmap = null;
        if (profileImg != null) {
            try {
                bitmap = BitmapFactory.decodeStream(profileImg.getDataStream());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void signup(SignUpCallback callback) {
        super.signUpInBackground(callback);
    }

    public void login(String username, String password, LogInCallback callback) {
        super.logInInBackground(username, password, callback);
    }

}