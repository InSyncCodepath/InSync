package com.codepath.insync.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityLoginBinding;
import com.codepath.insync.fragments.LoginFragment;
import com.codepath.insync.fragments.SignupFragment;
import com.codepath.insync.listeners.OnLoginListener;
import com.crashlytics.android.Crashlytics;
import com.parse.ParseFile;

import java.io.File;

import io.fabric.sdk.android.Fabric;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity implements OnLoginListener {

    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        fragmentManager = getSupportFragmentManager();
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

        setUpToolbar();


        if (savedInstanceState == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            LoginFragment loginFragment = new LoginFragment();
            ft.replace(R.id.flLogin, loginFragment);
            ft.commit();
        }
    }
    /*
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
     */

    private void setUpToolbar() {
        setSupportActionBar(binding.tbLogin);
        binding.tbLogin.setTitle("Login");
    }


    @Override
    public void onSignup() {
        binding.tbLogin.setTitle("Signup");
        FragmentTransaction ft = fragmentManager.beginTransaction();
        SignupFragment signupFragment = new SignupFragment();
        ft.replace(R.id.flLogin, signupFragment);
        ft.commit();
    }

    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLogin() {
        binding.tbLogin.setTitle("Login");
        FragmentTransaction ft = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        ft.replace(R.id.flLogin, loginFragment);
        ft.commit();
    }

}
