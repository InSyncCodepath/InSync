package com.codepath.insync.listeners;


public interface OnEventClickListener {
    void onItemClick(String eventId, boolean isCurrent, boolean canTrack);
}
