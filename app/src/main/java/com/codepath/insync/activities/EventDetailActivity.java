package com.codepath.insync.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.graphics.Palette;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventDetailBinding;
import com.codepath.insync.fragments.MessageSendFragment;
import com.codepath.insync.fragments.PastEventDetailFragment;
import com.codepath.insync.fragments.UpcomingEventDetailFragment;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.utils.Constants;
import com.codepath.insync.utils.DateUtil;
import com.parse.GetCallback;
import com.parse.ParseException;
import java.util.Date;



public class EventDetailActivity extends AppCompatActivity implements UpcomingEventDetailFragment.OnViewTouchListener {
    private static final String TAG = "EventDetailActivity";

    ActivityEventDetailBinding binding;
    CollapsingToolbarLayout collapsingToolbar;
    Event event;
    MessageSendFragment messageSendFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);


        setupToolbar();
        processIntent();
    }

    private void processIntent() {
        Intent intent = getIntent();
        Event.findEvent(intent.getStringExtra("objectId"), new GetCallback<Event>() {
            @Override
            public void done(Event eventObj, ParseException e) {
                if (e == null) {
                    event = eventObj;
                    loadViews();
                    loadFragments();
                } else {
                    event = null;
                    Log.e(TAG, "Error finding event.");
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        Bitmap bitmap = event.getProfileImageBitmap();
        if (bitmap != null) {
            binding.ivEDProfile.setImageBitmap(bitmap);
        }
        binding.tvEDName.setText(event.getName());
        binding.tvEDDescription.setText(event.getDescription());
        binding.tvEDDate.setText(DateUtil.getDateTimeInFormat(event.getStartDate()));
        binding.tvEDLocation.setText(event.getAddress());
    }

    private void setupToolbar() {
        collapsingToolbar = binding.ctlEventDetail;

        setSupportActionBar(binding.tbEventDetail);
        getSupportActionBar().setTitle("");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.palette);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_light);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.accent);
            }
        });


        binding.abEventDetail.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                Log.d(TAG, "Appbar offset changed to: "+verticalOffset);

                if (Math.abs(verticalOffset) > 650) {
                    TextView tvEventName = (TextView) collapsingToolbar.findViewById(R.id.tvEDName);
                    collapsingToolbar.setTitle(tvEventName.getText().toString());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    binding.rlToolbar.setVisibility(View.INVISIBLE);
                } else {
                    collapsingToolbar.setTitle(Constants.EMPTY_STR);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    binding.rlToolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Date now = new Date();
        String videoUrl;
        if (event.getHighlightsVideo() != null) {
            videoUrl = event.getHighlightsVideo().getUrl();
        } else {
            videoUrl = null;
        }
        if (event.getEndDate().compareTo(now) < 0) {
            PastEventDetailFragment pastEventDetailFragment =
                    PastEventDetailFragment.newInstance(event.getObjectId(), videoUrl);
            ft.replace(R.id.flMessages, pastEventDetailFragment);
        } else {
            // Load current and upcoming event detail
            UpcomingEventDetailFragment upcomingEventDetailFragment = new UpcomingEventDetailFragment();
            ft.replace(R.id.flMessages, upcomingEventDetailFragment);
            messageSendFragment = new MessageSendFragment();
            ft.replace(R.id.flMessageSend, messageSendFragment);

            setupUI(binding.clED);
        }


        ft.commit();

    }

    @Override
    public void onViewTouch(View v) {
        hideAndClearFocus(v);
    }
}
