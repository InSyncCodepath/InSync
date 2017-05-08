package com.codepath.insync.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventDetailPastBinding;
import com.codepath.insync.fragments.PastEventDetailFragment;
import com.codepath.insync.listeners.OnImageClickListener;
import com.codepath.insync.models.parse.Event;
import com.parse.GetCallback;
import com.parse.ParseException;

import com.parse.ParseFile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class EventDetailPastActivity extends AppCompatActivity implements OnImageClickListener {

    private static final String TAG = "EventDetailPastActivity";


    ActivityEventDetailPastBinding binding;
    Event event;
    String eventId;
    FragmentManager fragmentManager;
    PastEventDetailFragment pastEventDetailFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail_past);

        fragmentManager = getSupportFragmentManager();
        postponeEnterTransition();
        processIntent();
        setupToolbar();
    }

    private void processIntent() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        binding.ivEDProfile.setTransitionName(intent.getStringExtra("transition_name"));
        Event.findEvent(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event eventObj, ParseException e) {
                if (e == null) {
                    event = eventObj;
                    loadViews();
                } else {
                    event = null;
                    Log.e(TAG, "Error finding event.");
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_right);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadViews() {
        ParseFile profileImage = event.getProfileImage();
        if (profileImage != null) {
            /*Glide.with(this)
                    .load(profileImage.getUrl())
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            scheduleStartPostponedTransition(binding.ivEDProfile);
                        }
                    });*/
            Picasso.with(this).load(profileImage.getUrl())
                    .placeholder(R.drawable.ic_camera_alt_white_48px)
                    .resize(binding.ivEDProfile.getWidth(), 0)
                    .transform(new RoundedCornersTransformation(10, 0)).into(binding.ivEDProfile,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            // Call the "scheduleStartPostponedTransition()" method
                            // below when you know for certain that the shared element is
                            // ready for the transition to begin.
                            scheduleStartPostponedTransition(binding.ivEDProfile);
                            loadFragments();
                        }

                        @Override
                        public void onError() {
                            // ...
                        }
                    });

        }

        binding.tvEDName.setText(event.getName());

    }
    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        binding.ivEDProfile.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );
    }

    private void setupToolbar() {
        setSupportActionBar(binding.tbEventDetail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    private void loadFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Load current and upcoming event detail
        pastEventDetailFragment = PastEventDetailFragment.newInstance(
                eventId, event.getName(), event.getTheme(), event.getDescription(),
                event.getStartDate().getTime(), event.getAddress());
        ft.replace(R.id.flPastDetails, pastEventDetailFragment);

        ft.commit();
    }

    @Override
    public void onItemClick(ArrayList<String> images, int position) {
        Transition changeTransform = TransitionInflater.from(this).
                inflateTransition(R.transition.change_image_transform);
        Transition explodeTransform = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        // Find the shared element (in Fragment A)
        // Setup exit transition on first fragment

        pastEventDetailFragment.setSharedElementReturnTransition(changeTransform);
        pastEventDetailFragment.setExitTransition(explodeTransform);

        Bundle animationBundle =
                ActivityOptions.makeCustomAnimation(this, R.anim.do_not_move, R.anim.do_not_move).toBundle();

        Intent fullScreenImageIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenImageIntent.putExtra("images", images);
        fullScreenImageIntent.putExtra("position", position);
        fullScreenImageIntent.putExtra("eventName", event.getName());

        startActivity(fullScreenImageIntent,animationBundle);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_right);
    }

}