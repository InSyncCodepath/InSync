package com.codepath.insync.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.graphics.Palette;
import android.widget.Toast;

import com.codepath.insync.R;
import com.codepath.insync.adapters.MessageAdapter;
import com.codepath.insync.databinding.ActivityEventDetailBinding;
import com.codepath.insync.models.Message;
import com.codepath.insync.models.User;
import com.codepath.insync.utils.Constants;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class EventDetailActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailActivity";

    ActivityEventDetailBinding binding;
    CollapsingToolbarLayout collapsingToolbar;
    List<Message> messages;
    MessageAdapter messageAdapter;
    LinearLayoutManager linearLayoutManager;
    boolean mFirstLoad;
    BroadcastReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);

        setupToolbar();
        setupUI(binding.clED);
        setupRecyclerView();

        //get Intent
        Intent intent = getIntent();
        String objectId = intent.getStringExtra("ObjectId");


        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }

    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.codepath.insync.Messages");
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("new_messages", false)) {
                    refreshMessages();
                }

            }
        };
        registerReceiver(messageReceiver, filter);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText || view instanceof FloatingActionButton)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.etEDMessage.clearFocus();
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

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
        refreshMessages();
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    private void setupRecyclerView() {
        binding.rvChat.setAdapter(messageAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(linearLayoutManager);

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

    void setupMessagePosting() {
        mFirstLoad = true;

        // When send button is clicked, create message object on Parse
        binding.fabEDSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = binding.etEDMessage.getText().toString();

                // Using new `Message` Parse-backed model now
                Message message = new Message();
                message.setBody(data);


                message.setSender(new User(ParseUser.getCurrentUser()));

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(getApplicationContext(), "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
                binding.etEDMessage.setText(null);
            }
        });
    }

    private void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include("sender");
        // Configure limit and sort order
        query.setLimit(50);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> newMessages, ParseException e) {
                if (e == null) {
                    messages.clear();
                    messages.addAll(newMessages);
                    messageAdapter.notifyDataSetChanged();
        /*
        TODO: ADD THIS INSTEAD
        int curSize = tweetsArrayAdapter.getItemCount();
        tweets.addAll(newTweets);
        int newSize = newTweets.size();
        tweetsArrayAdapter.notifyItemRangeInserted(curSize, newSize);
         */
                    // Scroll to the bottom of the list on initial load 
                    if (mFirstLoad) {
                        Log.d(TAG, "Loading messages for the first time.");
                        linearLayoutManager.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e(TAG, "Error Loading Messages" + e);
                }
            }
        });
    }

    public static Intent newIntent(Activity callingActivity){
        Intent intent = new Intent(callingActivity, EventDetailActivity.class);
        return intent;
    }
}
