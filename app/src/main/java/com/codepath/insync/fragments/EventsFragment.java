package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepath.insync.R;
import com.codepath.insync.adapters.EventAdapter;
import com.codepath.insync.databinding.FragmentEventListBinding;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;


public class EventsFragment extends Fragment implements OnEventClickListener {
    FragmentEventListBinding binding;
    int bufferHours = -3;
    Calendar cal = Calendar.getInstance();
    boolean isCurrent;
    OnEventClickListener eventClickListener;
    ArrayList<Event> events;
    EventAdapter eventAdapter;
    LinearLayoutManager linearLayoutManager;
    public EventsFragment() {

    }

    public static EventsFragment newInstance(boolean isCurrent) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putBoolean("isCurrent", isCurrent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_list, container, false);
        isCurrent = getArguments().getBoolean("isCurrent");
        events = new ArrayList<>();
        eventClickListener = (OnEventClickListener) getActivity();
        setupRecyclerView();



        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showEvents();
            }

        });
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, bufferHours); // subtract 3 hours
        eventClickListener = (OnEventClickListener) getActivity();
        showEvents();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.eventList.setLayoutManager(linearLayoutManager);
        eventAdapter = new EventAdapter(this, getContext(), events);
        binding.eventList.setAdapter(eventAdapter);
        FadeInAnimator fadeInAnimator = new FadeInAnimator();
        fadeInAnimator.setAddDuration(500);
        binding.eventList.setItemAnimator(fadeInAnimator);
    }

    private void showEvents() {
        events.clear();
        User currentUser = User.getCurrentUser();
        ParseQuery<UserEventRelation> query = ParseQuery.getQuery(UserEventRelation.class);
        query.include("event");
        query.whereEqualTo("userId", currentUser.getObjectId());
        query.findInBackground(new FindCallback<UserEventRelation>() {
            @Override
            public void done(List<UserEventRelation> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    Event userEvent = (Event) objects.get(i).get("event");
                    if (isCurrent) {
                        if(!userEvent.hasEnded() && (userEvent.getEndDate().compareTo(cal.getTime()) > 0)) {
                            events.add(userEvent);
                        }
                    } else {
                        if(userEvent.hasEnded() || (userEvent.getEndDate().compareTo(cal.getTime()) <= 0)){
                            events.add(userEvent);
                        }
                    }
                }
                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        if (isCurrent) {
                            return event1.getStartDate().compareTo(event2.getStartDate());
                        } else {
                            return event2.getStartDate().compareTo(event1.getStartDate());
                        }
                    }
                });
                eventAdapter.notifyDataSetChanged();
                if(events.size() == 0){
                    binding.eventList.setVisibility(View.GONE);
                    binding.emptyListCard.setVisibility(View.VISIBLE);
                    String noEventMessage = isCurrent ? "Create a new event" : "You dont have any past events";
                    binding.noEvent.setText(noEventMessage);
                }

            }
        });
        binding.swipeContainer.setRefreshing(false);
    }

    public void addEvent(Event event) {
        events.add(0, event);
        eventAdapter.notifyItemInserted(0);
        linearLayoutManager.scrollToPosition(0);
    }

    @Override
    public void onItemClick(String eventId, boolean isCurrent, boolean canTrack, ImageView imageView) {
        eventClickListener.onItemClick(eventId, isCurrent, canTrack, imageView);
    }

    public void removeEvent(Event event) {
        for (int i=0; i < events.size(); i++) {
            if (event.getObjectId().equals(events.get(i).getObjectId())) {
                events.remove(i);
                eventAdapter.notifyItemRemoved(i);
                return;
            }
        }
    }
}


