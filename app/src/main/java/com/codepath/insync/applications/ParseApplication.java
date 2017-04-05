package com.codepath.insync.applications;

import android.app.Application;

import com.codepath.insync.R;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.parse.interceptors.ParseStethoInterceptor;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Used in debugging. Needs to be removed for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Initialize parse with credentials
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(null)
                .addNetworkInterceptor(new ParseStethoInterceptor())
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(getString(R.string.parse_app_url)).build());
    }
}
