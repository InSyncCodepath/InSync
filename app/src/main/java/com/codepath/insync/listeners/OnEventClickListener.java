package com.codepath.insync.listeners;


import android.widget.ImageView;

public interface OnEventClickListener {
    void onItemClick(String eventId, boolean isCurrent, boolean canTrack, ImageView imageView);
}
