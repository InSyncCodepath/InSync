package com.codepath.insync.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.codepath.insync.R;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.utils.Constants;
import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;


public class SplashActivity extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.primary);
        configSplash.setAnimCircularRevealDuration(500); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);
        configSplash.setRevealFlagY(Flags.REVEAL_TOP);

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.group_15); //logo drawable
        configSplash.setAnimLogoSplashDuration(500); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        configSplash.setTitleSplash(Constants.INSYNC_LOGO);
        configSplash.setTitleTextColor(R.color.insync_white);
        configSplash.setTitleTextSize(36f); //float value
        configSplash.setAnimTitleDuration(500);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
    }

    @Override
    public void animationsFinished() {

        SharedPreferences sharedPreferences = getSharedPreferences("pref",Context.MODE_PRIVATE);
        User currentUser = User.getCurrentUser();
        Intent intent;

        Bundle animationBundle =
                ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        if (currentUser != null) {
            intent = new Intent(SplashActivity.this, EventListActivity.class);
        } else {
            if(sharedPreferences.getBoolean("hasOnboarded", false)){
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            else {
                //check shared Pref if walkthrough done
                intent = new Intent(SplashActivity.this, IntroActivity.class);
                SharedPreferences sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("hasOnboarded", true);
                editor.apply();
            }
        }

        startActivity(intent, animationBundle);
        finish();
    }
}
