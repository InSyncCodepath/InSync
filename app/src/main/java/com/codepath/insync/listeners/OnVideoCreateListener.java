package com.codepath.insync.listeners;


public interface OnVideoCreateListener {
    void onCreateSuccess(String videoUrl);
    void onCreateFailure(int status, String message);
}
