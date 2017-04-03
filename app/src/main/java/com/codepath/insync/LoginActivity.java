package com.codepath.insync;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

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
        throw new RuntimeException("This is a crash");
    }
}
