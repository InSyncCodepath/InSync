package com.codepath.insync.listeners;


public interface OnVideoCreateListener {
    void onPrepare();
    void onCreateSuccess(String videoUrl);
    void onCreateFailure(int status, String message);
}
