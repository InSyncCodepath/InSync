package com.codepath.insync.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;


@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String SENDER_KEY = "sender";
    public static final String BODY_KEY = "body";
    public static final String MEDIA_KEY = "media";

    public String getSender() {
        return getString(SENDER_KEY);
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

    // TODO: Call get, saveInBackground and pinInBackground here
}
