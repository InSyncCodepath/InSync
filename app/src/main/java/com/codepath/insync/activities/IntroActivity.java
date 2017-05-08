package com.codepath.insync.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codepath.insync.Manifest;
import com.codepath.insync.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

import static android.os.Build.VERSION_CODES.M;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.insync_grey)
                        .buttonsColor(R.color.accent)
                        .image(R.drawable.past_list)
                        .title("Event List")
                        .description("List of your Events separated as upcoming and past")
                        .build())
//                ,
//                new MessageButtonBehaviour(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showMessage("We provide solutions to make you love your work");
//                    }
//                }, "Work with love"))
        ;

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.insync_grey)
                .buttonsColor(R.color.accent)
                .image(R.drawable.create)
                .title("Create events")
                .description("Simple event creation flow")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.insync_grey)
                .buttonsColor(R.color.accent)
                .image(R.drawable.chat)
                .title("Chat for Upcoming events")
                .description("Interact with participants to get event updates")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.insync_grey)
                .buttonsColor(R.color.accent)
                .image(R.drawable.location)
                .title("Location tracking")
                .description("Track invitees before the event")
                        .build()
//                ,
//                new MessageButtonBehaviour(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startEventListActivity();
//                    }
//                }, "InSync")
        );



    }
    public void startEventListActivity(){
        Bundle animationBundle =
                ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(intent, animationBundle);
        finish();
    }

    @Override
    public void onFinish() {

        super.onFinish();
        startEventListActivity();
    }
}
