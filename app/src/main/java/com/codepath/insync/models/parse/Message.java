package com.codepath.insync.models.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;


@ParseClassName("Message")
public class Message extends ParseObject {
    private static final String SENDER_KEY = "sender";
    private static final String BODY_KEY = "body";
    private static final String MEDIA_KEY = "media";
    private static final String CREATED_AT_KEY = "createdAt";


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

    public static Message newInstance(ParseObject object) {
        Message message = new Message();
        message.setBody(object.getString(BODY_KEY));
        message.setMedia(object.getParseFile(MEDIA_KEY));
        message.setSender(new User(object.getParseUser(SENDER_KEY)));
        message.put(CREATED_AT_KEY, object.getDate(CREATED_AT_KEY));
        return message;
    }
}
