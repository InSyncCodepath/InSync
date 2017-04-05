package com.codepath.insync.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.codepath.insync.R;
import com.crashlytics.android.Crashlytics;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Make sure the Fabric.with() line is after all other 3rd-party SDKs that set an UncaughtExceptionHandler

        Fabric.with(this, new Crashlytics());

        // TODO: Move this to where you establish a user session logUser();

        /*private void logUser() {
            // TODO: Use the current user's information
            // You can call any combination of these three methods
            Crashlytics.setUserIdentifier("12345");
            Crashlytics.setUserEmail("user@fabric.io");
            Crashlytics.setUserName("Test User");
        }*/
    }
    public void forceCrash(View view){
        HashMap<String, String> payload = new HashMap<>();
        payload.put("customData", "My message");
        ParseCloud.callFunctionInBackground("pushChannelTest", payload, new FunctionCallback<Object>() {

            @Override
            public void done(Object object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error sending push to cloud: "+e.toString());
                } else {
                    Log.d(TAG, "Push sent successfully!");
                }
            }
        });
    }
}
