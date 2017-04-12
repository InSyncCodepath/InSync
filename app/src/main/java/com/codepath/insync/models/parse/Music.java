package com.codepath.insync.models.parse;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


@ParseClassName("Music")
public class Music extends ParseObject {
    public static final String NAME_KEY = "name";
    public static final String THEME_KEY = "theme";
    public static final String AUDIO_KEY = "audio";
    public static final String DURATION_KEY = "duration";

    public String getName() {
        return getString(NAME_KEY);
    }

    public String getTheme() {
        return getString(THEME_KEY);
    }

    public ParseFile getAudio() {
        return getParseFile(AUDIO_KEY);
    }

    public int getDuration() {
        return getInt(DURATION_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public void setTheme(String theme) {
        put(THEME_KEY, theme);
    }

    public void setAudio(ParseFile audio) {
        put(AUDIO_KEY, audio);
    }

    public void setDuration(int duration) {
        put(DURATION_KEY, duration);
    }

    public static void findMusic(String theme, FindCallback<Music> findCallback) {
        ParseQuery<Music> query = ParseQuery.getQuery(Music.class);
        query.whereEqualTo("theme", theme);
        query.findInBackground(findCallback);
    }
}
