package com.codepath.insync.models.retrofit;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Video {
    String id;
    String name;
    String status;
    @SerializedName("video")
    Map<Integer, VideoInfo> videoInfo;

    public Video() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public VideoInfo getVideoInfo(int res) {
        return videoInfo.get(res);
    }
}
