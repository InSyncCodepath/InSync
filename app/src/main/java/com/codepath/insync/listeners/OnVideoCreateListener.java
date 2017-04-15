package com.codepath.insync.listeners;


import com.codepath.insync.models.parse.Event;

public interface OnVideoCreateListener {
    void onCreateSuccess(Event event, String videoUrl);
    void onCreateFailure(int status, String message);
}
