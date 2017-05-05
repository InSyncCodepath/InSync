package com.codepath.insync.listeners;

import com.codepath.insync.models.parse.Message;

public interface OnMessageChangeListener {
    void onMessageCreated(Message message);
    void onFocused();

}
