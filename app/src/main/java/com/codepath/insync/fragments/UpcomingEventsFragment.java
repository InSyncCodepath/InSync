package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepath.insync.R;
import com.codepath.insync.adapters.UpcomingEventAdapter;
import com.codepath.insync.databinding.FragmentUpcomingEventListBinding;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.codepath.insync.R.id.swipeContainer;
import static com.codepath.insync.R.string.username;
import static com.codepath.insync.models.parse.UserEventRelation.getEventPointerKey;


public class UpcomingEventsFragment extends Fragment implements UpcomingEventAdapter.EventDetailClickHandling {
    FragmentUpcomingEventListBinding binding;
    OnEventClickListener eventClickListener;
    SwipeRefreshLayout swipeContainer;
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerView upcomingList;
    ArrayList<Event> events = new ArrayList<>();
    UpcomingEventAdapter upcomingEventAdapter;
    //
    int bufferHours = -3;

    Calendar cal = Calendar.getInstance(); // creates calendar

    public UpcomingEventsFragment() {
    }

    public static UpcomingEventsFragment newInstance(int sectionNumber) {
        UpcomingEventsFragment fragment = new UpcomingEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming_event_list, container, false);
        View view = binding.getRoot();
        upcomingList = binding.upcomingList;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        upcomingEventAdapter = new UpcomingEventAdapter(this, getContext(), events);
        upcomingList.setAdapter(upcomingEventAdapter);
        upcomingList.setLayoutManager(linearLayoutManager);
        swipeContainer = binding.swipeContainer;

        //binding.swipeContainer;
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showEvents();
            }

        });
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, bufferHours); // subtract 3 hours
        eventClickListener = (OnEventClickListener) getActivity();
        showEvents();

        return view;
    }

    public void showEvents(){
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
                    if(!userEvent.hasEnded() && (userEvent.getEndDate().compareTo(cal.getTime()) > 0)) {
                        events.add(userEvent);
                    }
                }
                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        return event1.getStartDate().compareTo(event2.getStartDate());
                    }
                });
                upcomingEventAdapter.notifyDataSetChanged();


            }
        });
        swipeContainer.setRefreshing(false);
    }
    @Override
    public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack, ImageView imageView) {
       eventClickListener.onItemClick(eventId, isCurrent, canTrack, imageView);
    }

    public void reloadList() {
//        upcomingEventAdapter.notifyDataSetChanged();
        showEvents();
    }

    public void addEvent(Event event) {
        events.add(0, event);
        upcomingEventAdapter.notifyItemInserted(0);
    }

    public void removeEvent(Event event) {
        for (int i=0; i < events.size(); i++) {
            if (event.getObjectId().equals(events.get(i).getObjectId())) {
                events.remove(i);
                upcomingEventAdapter.notifyItemRemoved(i);
                return;
            }
        }
    }
}

