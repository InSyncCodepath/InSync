package com.codepath.insync.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.codepath.insync.Manifest;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityLoginBinding;
import com.codepath.insync.fragments.LoginFragment;
import com.codepath.insync.fragments.PhoneLoginFragment;
import com.codepath.insync.fragments.SignupFragment;
import com.codepath.insync.listeners.OnLoginListener;
import com.codepath.insync.listeners.OnImageUploadClickListener;
import com.codepath.insync.utils.LocationService;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity implements OnLoginListener, OnImageUploadClickListener {

    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    FragmentManager fragmentManager;
    private static final int LOCATION_ACCESS_PERMISSION = 20;
    private static final int CAMERA_REQUEST_CODE = 1024;
    SignupFragment signupFragment;
    PhoneLoginFragment phoneLoginFragment;
    boolean isSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        fragmentManager = getSupportFragmentManager();

        Fabric.with(this, new Crashlytics());

        FragmentTransaction ft = fragmentManager.beginTransaction();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String referrerStr = sharedPreferences.getString("referrer", null);
        String phoneNum = null;
        String eventId = null;
        boolean isInvite = false;
        if (referrerStr != null) {
            String[] split = referrerStr.split("&");
            if (split.length == 2) {
                String[] phoneSplit = split[0].split("=");
                if (phoneSplit[0].equals("phone_num") && phoneSplit.length > 1) {
                    isInvite = true;
                    String[] eventSplit = split[1].split("=");
                    eventId = eventSplit[1];
                    phoneNum = phoneSplit[1];
                    if (!eventSplit[0].equals("event_id")) {
                        isInvite = false;
                    }
                }
            }
        }

        if (!isInvite) {
            LoginFragment loginFragment = new LoginFragment();
            ft.replace(R.id.flLogin, loginFragment);
        } else {
            Log.d(TAG, "Phone based login attempt from phone_num: "+phoneNum+" for eventId: "+eventId);
            phoneLoginFragment = PhoneLoginFragment.newInstance(phoneNum, eventId);
            isSignup = false;
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
        signupFragment = new SignupFragment();
        isSignup = true;
        ft.replace(R.id.flLogin, signupFragment);
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_ACCESS_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent lsIntent = new Intent(this, LocationService.class);
                    // Start the service
                    startService(lsIntent);
                }
                break;
            default:
                break;

        }
        startActivity();
    }

    private void startLocationService() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_ACCESS_PERMISSION
            );
        } else {
            Intent lsIntent = new Intent(this, LocationService.class);
            // Start the service
            startService(lsIntent);
            startActivity();
        }
    }

    @Override
    public void onLoginSuccess() {
        startLocationService();
    }

    private void startActivity() {
        Bundle animationBundle =
                ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
        startActivity(intent, animationBundle);
        finish();

    }
    @Override
    public void onLogin() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        ft.replace(R.id.flLogin, loginFragment);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getStringExtra("filePath");
                if (isSignup) {
                    signupFragment.updateProfilePic(filePath);
                } else {
                    phoneLoginFragment.updateProfilePic(filePath);
                }

            }
        }
    }

    @Override
    public void onImageUploadClick(boolean isCamera) {
        Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
        intent.putExtra("is_profile_pic", true);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
}
