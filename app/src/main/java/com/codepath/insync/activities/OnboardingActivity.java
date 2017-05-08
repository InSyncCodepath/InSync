package com.codepath.insync.activities;

import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.codepath.insync.R;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AhoyOnboarderActivity {
    int iconWidth = 300, iconHeight = 650, marginTop = 16, marginLeft = 16, marginRight = 16, marginBottom = 16;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        //Past List card
        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Event List", "Description", R.drawable.past_list);
        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard1.setTitleColor(R.color.accent);
        ahoyOnboarderCard1.setDescriptionColor(R.color.accent);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard1.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        //Upcoming details card
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Chat for Upcoming events", "Description", R.drawable.past_list);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setTitleColor(R.color.accent);
        ahoyOnboarderCard2.setDescriptionColor(R.color.accent);
        ahoyOnboarderCard2.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard2.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard1.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        //Highlights
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Title", "Description", R.drawable.past_list);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setTitleColor(R.color.accent);
        ahoyOnboarderCard3.setDescriptionColor(R.color.accent);
        ahoyOnboarderCard3.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard3.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard1.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);


        //Location Tracking
        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard("Title", "Description", R.drawable.past_list);
        ahoyOnboarderCard4.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard4.setTitleColor(R.color.accent);
        ahoyOnboarderCard4.setDescriptionColor(R.color.accent);
        ahoyOnboarderCard4.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard4.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard1.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);
        setOnboardPages(pages);

        setGradientBackground();

//        List<Integer> colorList = new ArrayList<>();
//        colorList.add(R.color.accent);
//        setColorBackground(colorList);
    }

    @Override
    public void onFinishButtonPressed() {

    }
}
