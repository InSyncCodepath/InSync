package com.codepath.insync.listeners;


public interface OnEventClickListener {
    void onItemClick(String eventId, String eventName, boolean isCurrent, boolean canTrack);
}
