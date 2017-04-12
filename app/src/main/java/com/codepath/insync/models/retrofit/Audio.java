package com.codepath.insync.models.retrofit;


public class Audio {
    String id;
    int duration;
    String artist;
    String url;
    String title;
    String previewUrl;

    public String getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }
}
