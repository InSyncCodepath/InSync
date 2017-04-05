package com.codepath.insync.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codepath.insync.R;
import com.codepath.insync.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public  class ParseCBroadcastReceiver extends BroadcastReceiver {
    public static final String intentAction = "com.parse.push.intent.RECEIVE";
    private static final String TAG = "ParseCBroadcastReceiver";

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
        if (action.equals(intentAction)) {
            String channel = intent.getExtras().getString("com.parse.Channel");
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.d(TAG, "got action " + action + " on channel " + channel + " with:" + json.toString());
                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();
                while (itr.hasNext()) {
                    String key = itr.next();
                    String value = json.getString(key);
                    Log.d(TAG, "..." + key + " => " + value);
                    // Extract custom push data
                    switch (key) {
                        case "customdata":
                            // create a local notification
                            createNotification(context, value);
                            break;
                        case "launch":
                            // Handle push notification by invoking activity directly
                            launchSomeActivity(context, value);
                            break;
                        case "broadcast":
                            // OR trigger a broadcast to activity
                            triggerBroadcastToActivity(context, value);
                            break;
                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }
    }

    // Create a local dashboard notification to tell user about the event
    // See: http://guides.codepath.com/android/Notifications
    private void createNotification(Context context, String datavalue) {
        // Define the intent to trigger when notification is selected
        Intent intent = new Intent(context, LoginActivity.class);
        // Next, let's turn this into a PendingIntent using
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(context, requestID, intent, flags);
        // Now we can attach the pendingIntent to a new notification using setContentIntent
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.messenger_bubble_large_blue)
                .setContentTitle("Notification: " + datavalue)
                .setContentText("Pushed!")
                .setContentIntent(pIntent)
                .setAutoCancel(true) // Hides the notification after its been selected
                //.setDefaults(Notification.DEFAULT_ALL)
                //.setPriority(Notification.PRIORITY_HIGH)
                .setFullScreenIntent(pIntent, true) // Sets the notification for heads up display
                .build();
        // Get the notification manager system service
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, notification);
    }

    // Handle push notification by invoking activity directly
    // TODO: add activity to launch
    // See: http://guides.codepath.com/android/Using-Intents-to-Create-Flows
    private void launchSomeActivity(Context context, String datavalue) {
        Intent pupInt = new Intent(context, LoginActivity.class);
        pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pupInt.putExtra("data", datavalue);
        context.getApplicationContext().startActivity(pupInt);
    }

    // Handle push notification by sending a local broadcast
    // to which the activity subscribes to
    // TODO: check if this is needed
    // See: http://guides.codepath.com/android/Starting-Background-Services#communicating-with-a-broadcastreceiver
    private void triggerBroadcastToActivity(Context context, String datavalue) {
        Intent intent = new Intent(intentAction);
        intent.putExtra("data", datavalue);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
