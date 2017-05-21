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
import com.codepath.insync.listeners.OnImageClickListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.utils.CommonUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UpcomingEventDetailFragment extends Fragment {
    private static String TAG = "UpcomingEventDFragment";
    FragmentUpcomingEventDetailBinding binding;
    List<Message> messages;
    MessageAdapter messageAdapter;
    LinearLayoutManager linearLayoutManager;
    BroadcastReceiver messageReceiver;
    OnViewTouchListener viewTouchListener;
    OnImageClickListener onImageClickListener;
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
        onImageClickListener = (OnImageClickListener) getActivity();
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

        messageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Message message = messages.get(position);
                if (message.getMedia() != null) {
                    findGalleryImages(position);
                }
            }
        });

    }

    private void findGalleryImages(int position) {
        ArrayList<String> chatImages = new ArrayList<>();
        int newPosition = messages.size() - position - 1;
        for (int i=messages.size()-1; i >= 0; i--) {
            String imageUrl;
            if (messages.get(i).getMedia() != null) {
                imageUrl = messages.get(i).getMedia().getUrl();
                chatImages.add(imageUrl);
            } else {
                if (i > position) {
                    newPosition--;
                }
            }
        }
        onImageClickListener.onItemClick(chatImages, newPosition);
    }

    public void updateMessage(Message message) {
        messages.add(0, message);
        messageAdapter.notifyItemInserted(0);
    }

    public void updateScroll() {
        //linearLayoutManager.scrollToPosition(0);
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
                    if (newMessages.size() == 0) {
                        return;
                    }
                    Date currDate = newMessages.get(newMessages.size()-1).getCreatedAt();
                    Message dateMsg = new Message();
                    dateMsg.setBody(CommonUtil.getRelativeTimeAgo(currDate));
                    messages.add(dateMsg);
                    for (int i=newMessages.size()-1; i >= 0; i--) {
                        boolean isEqual = CommonUtil.compareDates(currDate, newMessages.get(i).getCreatedAt());
                        if (!isEqual) {
                            currDate = newMessages.get(i).getCreatedAt();
                            Message newDateMsg = new Message();
                            newDateMsg.setBody(CommonUtil.getRelativeTimeAgo(currDate));
                            messages.add(0, newDateMsg);
                        }
                        messages.add(0, newMessages.get(i));
                    }

                    messageAdapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(0);
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
