package com.codepath.insync.models.parse;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


@ParseClassName("Music")
public class Music extends ParseObject {
    private static final String NAME_KEY = "name";
    private static final String THEME_KEY = "theme";
    private static final String AUDIO_KEY = "audio";
    private static final String DURATION_KEY = "duration";

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
