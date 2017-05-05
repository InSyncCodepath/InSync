package com.codepath.insync.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.codepath.insync.BuildConfig;
import com.codepath.insync.R;
import com.codepath.insync.activities.EventDetailChatActivity;
import com.codepath.insync.activities.EventListActivity;
import com.codepath.insync.activities.LoginActivity;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public  class ParseCBroadcastReceiver extends BroadcastReceiver {
    public static final String intentAction = "com.parse.push.intent.RECEIVE";
    public static final String yesRsvp = "RSVP_YES";
    public static final String noRsvp = "RSVP_NO";
    private static final String TAG = "ParseCBroadcastReceiver";
    private static final String KEY_NOTIFICATION_GROUP = "InSync Notifications";
    private static final int GROUP_ID = 1;

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
        String eventId = intent.getStringExtra("eventId");

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
                    //String value = json.getString(key);
                    //Log.d(TAG, "..." + key + " => " + value);
                    // Extract custom push data
                    switch (key) {
                        case "customdata":
                            // create a local notification
                            createNotification(context, json.getJSONObject(key));
                            break;
                        case "launch":
                            // Handle push notification by invoking activity directly
                            launchSomeActivity(context, json.getString(key));
                            break;
                        case "broadcast":
                            // OR trigger a broadcast to activity
                            triggerBroadcastToActivity(context, json.getString(key));
                            break;
                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        } else if (action.equals(yesRsvp)) {
            Log.d(TAG, "GOT RSVP YES");
            updateRsvp(eventId, Constants.ATTENDING);
            // Get the notification manager system service
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(intent.getIntExtra("noti_id", -1));

        } else if (action.equals(noRsvp)) {
            Log.d(TAG, "GOT RSVP NO");
            updateRsvp(eventId, Constants.DECLINE);
            // Get the notification manager system service
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(intent.getIntExtra("noti_id", -1));
        }
    }

    private void updateRsvp(String eventId, final int rsvpStatus) {
        Event event = new Event();
        event.setObjectId(eventId);
        UserEventRelation.findUserEvent(event, new FindCallback<UserEventRelation>() {
            @Override
            public void done(List<UserEventRelation> objects, ParseException e) {
                Log.d(TAG, "Got "+objects.size()+" UserEventRelation");
                UserEventRelation userEventRelation = objects.get(0);
                userEventRelation.setRsvpStatusKey(rsvpStatus);
                objects.get(0).updateRsvpStatus(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(TAG, "RSVP updated successfully");
                    }
                });
            }
        });
    }

    // Create a local dashboard notification to tell user about the event
    // See: http://guides.codepath.com/android/Notifications
    private void createNotification(Context context, JSONObject notiObj) {
        int notiType = notiObj.optInt("notificationType");

        switch (notiType) {
            case Constants.NEW_EVENT:
                createNewEventNoti(context, notiObj, Constants.NEW_EVENT);
                break;
            case Constants.RSVP_REMINDER:
                createNewEventNoti(context, notiObj, Constants.RSVP_REMINDER);
                break;
            default:
                break;
        }
    }

    // Handle push notification by invoking activity directly
    // TODO: add activity to launch
    // See: http://guides.codepath.com/android/Using-Intents-to-Create-Flows
    private void createNewEventNoti(Context context, JSONObject notiObj, int notiType) {
        String eventId = notiObj.optString("eventId");
        // Create request ID
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int requestID = sharedPreferences.getInt(eventId, -1);
        if (requestID == -1) {
            requestID = (int)System.currentTimeMillis();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(eventId, requestID);
            editor.apply();
        }

        // Define custom views
        RemoteViews contentView = new RemoteViews(BuildConfig.APPLICATION_ID, R.layout.notification_invite);
        contentView.setImageViewResource(R.id.ivInviteNoti, R.mipmap.ic_insync_launch);
        contentView.setTextViewText(R.id.tvInviteTitle, notiObj.optString("title"));
        contentView.setTextViewText(R.id.tvInviteText, notiObj.optString("text"));

        // Define event list activity to trigger when group notification is selected
        Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
        PendingIntent eventListIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Define the intent to trigger when notification is selected
        Intent detailIntent = new Intent(context.getApplicationContext(), EventDetailChatActivity.class);
        detailIntent.putExtra("eventId", eventId);
        detailIntent.putExtra("isCurrent", true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
        // Adds the back stack
        stackBuilder.addParentStack(EventDetailChatActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(detailIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent detailPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Yes intent
        Intent yesReceive = new Intent();
        yesReceive.putExtra("eventId", eventId);
        yesReceive.putExtra("noti_id", requestID);
        yesReceive.setAction(yesRsvp);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 20, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        //No intent
        Intent noReceive = new Intent();
        noReceive.putExtra("eventId", eventId);
        noReceive.putExtra("noti_id", requestID);
        noReceive.setAction(noRsvp);
        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(context, 20, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        // Now we can attach the pendingIntent to a new notification using setContentIntent
        Notification groupNotification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_lock_screen_noti)
                //.setCustomContentView(contentView)
                .setStyle(new NotificationCompat.InboxStyle())
                .setColor(ContextCompat.getColor(context, R.color.tealish))
                .setContentTitle(notiObj.optString("title"))
                .setSubText(User.getCurrentUser().getName())
                .setAutoCancel(true) // Hides the notification after its been selected
                //.setDefaults(Notification.DEFAULT_ALL)
                //.setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(eventListIntent)
                //.setFullScreenIntent(detailPendingIntent, false) // Sets the notification for heads up display
                .setGroupSummary(true)
                .setGroup(KEY_NOTIFICATION_GROUP)
                .setWhen(System.currentTimeMillis())

                .build();

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_lock_screen_noti)
                //.setCustomContentView(contentView)
                .setStyle(new NotificationCompat.InboxStyle())
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setContentTitle(notiObj.optString("title"))
                .setContentText(notiObj.optString("text"))
                .setSubText(User.getCurrentUser().getName())
                .setAutoCancel(true) // Hides the notification after its been selected
                //.setDefaults(Notification.DEFAULT_ALL)
                //.setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(detailPendingIntent)
                //.setFullScreenIntent(detailPendingIntent, false) // Sets the notification for heads up display
                .setGroup(KEY_NOTIFICATION_GROUP)

                .addAction(R.mipmap.ic_lock_screen_noti, "Attending", pendingIntentYes)
                .addAction(R.drawable.ic_decline_unselected, "Regret", pendingIntentNo)
                .addAction(R.drawable.icon, "Details", detailPendingIntent)
                .setWhen(System.currentTimeMillis())

                .build();
            // Get the notification manager system service
            //NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify(GROUP_ID, groupNotification);
            manager.notify(requestID, notification);
    }
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
