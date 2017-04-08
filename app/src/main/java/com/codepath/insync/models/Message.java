package com.codepath.insync.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String SENDER_KEY = "sender";
    public static final String BODY_KEY = "body";
    public static final String MEDIA_KEY = "media";

    public User getSender() {
        return new User(getParseUser(SENDER_KEY));
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public ParseFile getMedia() {
        return getParseFile(MEDIA_KEY);
    }

    public void setSender(User sender) {
        put(SENDER_KEY, sender);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setMedia(ParseFile media) {
        put(MEDIA_KEY, media);
    }

    public Bitmap getProfileImageBitmap() {
        ParseFile profileImg = getSender().getProfileImage();
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

    public Bitmap getMediaImageBitmap() {
        ParseFile mediaImg = getMedia();
        Bitmap bitmap = null;
        if (mediaImg != null) {
            try {
                bitmap = BitmapFactory.decodeStream(mediaImg.getDataStream());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    // TODO: Call get, saveInBackground and pinInBackground here
}
