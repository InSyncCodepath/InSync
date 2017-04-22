package com.codepath.insync.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.codepath.insync.R;
import com.codepath.insync.adapters.MessageAdapter;
import com.codepath.insync.databinding.FragmentUpcomingEventDetailBinding;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class UpcomingEventDetailFragment extends Fragment {
    private static String TAG = "UpcomingEventDFragment";
    FragmentUpcomingEventDetailBinding binding;
    List<Message> messages;
    MessageAdapter messageAdapter;
    LinearLayoutManager linearLayoutManager;
    boolean mFirstLoad = true;
    BroadcastReceiver messageReceiver;
    OnViewTouchListener viewTouchListener;
    Event event;

    public static UpcomingEventDetailFragment newInstance(String eventId) {

        Bundle args = new Bundle();

        UpcomingEventDetailFragment upcomingEventDetailFragment = new UpcomingEventDetailFragment();
        args.putString("eventId", eventId);

        upcomingEventDetailFragment.setArguments(args);
        return upcomingEventDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(getActivity(), messages);
        event = new Event();
        event.setObjectId(getArguments().getString("eventId"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming_event_detail, container, false);
        viewTouchListener = (OnViewTouchListener) getActivity();
        setupUI(binding.rvChat);
        setupRecyclerView();
        refreshMessages();
        return binding.getRoot();
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText || view instanceof FloatingActionButton)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    viewTouchListener.onViewTouch(v);
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

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
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
        getActivity().registerReceiver(messageReceiver, filter);
    }

    private void setupRecyclerView() {
        binding.rvChat.setAdapter(messageAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(linearLayoutManager);

    }
    private void addImage(){

    }

    private void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> parseQuery = event.getMessageRelation().getQuery();
        parseQuery.include("sender");
        // Configure limit and sort order
        parseQuery.setLimit(50);

        // get the latest 50 messages, order will show up newest to oldest of this group
        parseQuery.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        parseQuery.findInBackground(new FindCallback<Message>() {
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

    public interface OnViewTouchListener {
        void onViewTouch(View v);
    }
}
