package com.codepath.insync.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public  class AppReferrerReceiver extends BroadcastReceiver {
    private static final String TAG = "AppReferrerReceiver";
    public static final String installAction = "com.android.vending.INSTALL_REFERRER";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(installAction)) {
            Object referrer = intent.getExtras().get("referrer");
            if (referrer == null) {
                return;
            }
            Log.d(TAG, "Install action received: "+referrer.toString());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            String[] newUserData = referrer.toString().split("&");
            editor.putString("phoneNum", newUserData[0]);
            editor.putString("eventId", newUserData[1]);

            editor.apply();
        }
    }
}
