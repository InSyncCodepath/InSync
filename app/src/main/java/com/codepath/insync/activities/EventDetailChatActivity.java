package com.codepath.insync.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.codepath.insync.Manifest;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventDetailBinding;
import com.codepath.insync.fragments.ConfirmationFragment;
import com.codepath.insync.fragments.MessageSendFragment;
import com.codepath.insync.fragments.PastEventDetailFragment;
import com.codepath.insync.fragments.PastEventWaitFragment;
import com.codepath.insync.fragments.UpcomingEventDetailFragment;
import com.codepath.insync.listeners.OnImageClickListener;
import com.codepath.insync.listeners.OnMessageChangeListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.utils.CommonUtil;
import com.parse.GetCallback;
import com.parse.ParseException;

import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class EventDetailChatActivity extends AppCompatActivity implements
        UpcomingEventDetailFragment.OnViewTouchListener,
        ConfirmationFragment.UpdateDraftDialogListener,
        OnImageClickListener,
        OnMessageChangeListener{

    private static final String TAG = "EventDetailChatActivity";


    ActivityEventDetailBinding binding;
    CollapsingToolbarLayout collapsingToolbar;
    Event event;
    String eventId;
    boolean isCurrent;
    boolean canTrack;
    MessageSendFragment messageSendFragment;
    PastEventWaitFragment pastEventWaitFragment;
    PastEventDetailFragment pastEventDetailFragment;
    FragmentManager fragmentManager;
    RelativeLayout rlEventDetail;
    UpcomingEventDetailFragment upcomingEventDetailFragment;
    final int EVENT_DETAIL_RQ = 1002;
    Handler tbHintHandler = new Handler();

    Runnable tbHintRunnable =new Runnable() {

        @Override
        public void run() {
            binding.tvTapHint.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);

        fragmentManager = getSupportFragmentManager();
        rlEventDetail = binding.rlEventDetail;

        postponeEnterTransition();
        processIntent();
        setupToolbar();

        //tbHintHandler.removeCallbacks(tbHintRunnable);
        //tbHintHandler.postDelayed(tbHintRunnable, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for the current event
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return super.onCreateOptionsMenu(menu);
        }
        if (canTrack) {
            getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void processIntent() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        isCurrent = intent.getBooleanExtra("isCurrent", false);
        canTrack = intent.getBooleanExtra("canTrack", false);
        binding.ivEDProfile.setTransitionName(intent.getStringExtra("transition_name"));
        Event.findEvent(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event eventObj, ParseException e) {
                if (e == null) {
                    event = eventObj;
                    loadViews();
                    loadFragments();
                } else {
                    event = null;
                    Log.e(TAG, "Error finding event.");
                    supportFinishAfterTransition();
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
            case R.id.action_track:
                Intent intent = new Intent(EventDetailChatActivity.this, LocationTrackerActivity.class);
                intent.putExtra("eventId", event.getObjectId());
                intent.putExtra("eventLatitude", event.getLocation().getLatitude());
                intent.putExtra("eventLongitude", event.getLocation().getLongitude());
                startActivity(intent);
                break;
            case R.id.action_highlights:
                handleHighlightsAction();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleHighlightsAction() {
        String message = "Are you sure you want to end this event? This will prevent you from posting any information or media to this event.";
        ConfirmationFragment confirmationFragment = ConfirmationFragment.newInstance(message, "End Event Now", "Cancel");
        confirmationFragment.show(fragmentManager, "fragment_confirmation");
    }
    @Override
    public void onConfirmUpdateDialog(int position) {
        if (position == DialogInterface.BUTTON_POSITIVE) {


            event.setHasEnded(true);
            event.updateEvent(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Past event status updated successfully!");
                        loadViews();
                        //loadFragments();
                    } else {
                        Log.e(TAG, "Could not update past event flag");
                    }
                }
            });

            Intent intent = new Intent();
            intent.putExtra("hasEnded", true);
            intent.putExtra("eventName", event.getName());
            intent.putExtra("eventId", event.getObjectId());
            intent.putExtra("eventDate", event.getStartDate().getTime());
            intent.putExtra("eventAddress", event.getAddress());
            intent.putExtra("eventImage", event.getProfileImage().getUrl());
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText || view instanceof FloatingActionButton)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideAndClearFocus(v);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideAndClearFocus(View v) {
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        messageSendFragment.clearViewFocus();
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

        //collapsingToolbar = binding.ctlEventDetail;

        setSupportActionBar(binding.tbEventDetail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.tbEventDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle animationBundle =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_left).toBundle();


                Intent eventDetailMoreIntent = new Intent(EventDetailChatActivity.this, EventDetailMoreActivity.class);
                eventDetailMoreIntent.putExtra("eventId", eventId);
                eventDetailMoreIntent.putExtra("isCurrent", isCurrent);
                eventDetailMoreIntent.putExtra("canTrack", canTrack);
                startActivity(eventDetailMoreIntent, animationBundle);

            }
        });



        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.palette);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_light);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.accent);
            }
        });*/



    }




    private void loadFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (isCurrent) {
            // Load current and upcoming event detail
            upcomingEventDetailFragment = UpcomingEventDetailFragment.newInstance(eventId);
            ft.replace(R.id.flMessages, upcomingEventDetailFragment);
            messageSendFragment = MessageSendFragment.newInstance(eventId);
            ft.replace(R.id.flMessageSend, messageSendFragment);
            setupUI(binding.rlEventDetail);
            //setupChatClickListeners();
        } else {
            binding.flMessageSend.setVisibility(View.GONE);
            //binding.famEDMedia.setVisibility(View.GONE);
            pastEventDetailFragment =
                    PastEventDetailFragment.newInstance(event.getObjectId(), event.getName(), event.getTheme());
            ft.replace(R.id.flMessages, pastEventDetailFragment);
        }

        ft.commit();

    }

    @Override
    public void onViewTouch(View v) {
        hideAndClearFocus(v);
    }

    @Override
    public void onItemClick(ArrayList<String> images, int position) {
        Transition changeTransform = TransitionInflater.from(this).
                inflateTransition(R.transition.change_image_transform);
        Transition explodeTransform = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        // Find the shared element (in Fragment A)
        // Setup exit transition on first fragment
        if (pastEventDetailFragment != null) {
            pastEventDetailFragment.setSharedElementReturnTransition(changeTransform);
            pastEventDetailFragment.setExitTransition(explodeTransform);
        } else {
            upcomingEventDetailFragment.setSharedElementReturnTransition(changeTransform);
            upcomingEventDetailFragment.setExitTransition(explodeTransform);

        }

        Bundle animationBundle =
                ActivityOptions.makeCustomAnimation(this, R.anim.do_not_move, R.anim.do_not_move).toBundle();

        Intent fullScreenImageIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenImageIntent.putExtra("images", images);
        fullScreenImageIntent.putExtra("position", position);
        fullScreenImageIntent.putExtra("eventName", event.getName());

        startActivity(fullScreenImageIntent,animationBundle);

    }



    @Override
    public void onMessageCreated(Message message) {
        upcomingEventDetailFragment.updateMessage(message);
    }

    @Override
    public void onFocused() {
        upcomingEventDetailFragment.updateScroll();
    }
}
