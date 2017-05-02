package com.codepath.insync.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityLoginBinding;
import com.codepath.insync.fragments.LoginFragment;
import com.codepath.insync.fragments.PhoneLoginFragment;
import com.codepath.insync.fragments.SignupFragment;
import com.codepath.insync.listeners.OnLoginListener;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


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


        FragmentTransaction ft = fragmentManager.beginTransaction();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String referrerStr = sharedPreferences.getString("referrer", null);

        if (referrerStr == null || referrerStr.split("&").length < 2) {
            LoginFragment loginFragment = new LoginFragment();
            ft.replace(R.id.flLogin, loginFragment);
        } else {
            String[] userData = referrerStr.split("&");
            PhoneLoginFragment phoneLoginFragment = PhoneLoginFragment.newInstance(userData[0], userData[1]);
            ft.replace(R.id.flLogin, phoneLoginFragment);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("referrer");
            editor.apply();
        }
        ft.commit();
    }


    @Override
    public void onSignup() {
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
        FragmentTransaction ft = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        ft.replace(R.id.flLogin, loginFragment);
        ft.commit();
    }

}
