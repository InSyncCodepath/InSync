package com.codepath.insync.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;


import com.codepath.insync.R;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.utils.CommonUtil;
import com.codepath.insync.utils.Constants;
import com.daimajia.androidanimations.library.Techniques;
import com.parse.Parse;
import com.parse.ParseUser;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

import static com.codepath.insync.utils.CommonUtil.sendInviteLink;
import static com.parse.ParseUser.logOut;

public class SplashActivity extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {

//Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.lighter_green); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(500); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_LEFT); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.ic_launch); //or any other drawable
        configSplash.setAnimLogoSplashDuration(500); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        configSplash.setTitleSplash(Constants.INSYNC_LOGO);
        configSplash.setTitleTextColor(R.color.primary_dark);
        configSplash.setTitleTextSize(36f); //float value
        configSplash.setAnimTitleDuration(500);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
        //configSplash.setTitleFont("fonts/myfont.ttf"); //TODO: ADD FONT
    }

    @Override
    public void animationsFinished() {

        User currentUser = User.getCurrentUser();
        Intent intent;

        if (currentUser != null) {
            intent = new Intent(SplashActivity.this, EventListActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();

    }

}//
