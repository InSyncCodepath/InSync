package com.codepath.insync.listeners;

import com.codepath.insync.models.parse.Message;

/**
 * Created by usarfraz on 4/23/17.
 */

public interface OnMessageChangeListener {
    void onTextChange(int count);
    void onMessageCreated(Message message);
    void onFocused();

}
