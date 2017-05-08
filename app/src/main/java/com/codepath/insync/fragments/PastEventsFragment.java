package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepath.insync.R;
import com.codepath.insync.adapters.PastEventAdapter;
import com.codepath.insync.databinding.FragmentPastEventListBinding;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;


public class PastEventsFragment extends Fragment implements PastEventAdapter.EventDetailClickHandling {
    FragmentPastEventListBinding binding;
    int bufferHours = -3;
    Calendar cal = Calendar.getInstance();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "PastEventsFragment";
    RecyclerView pastList;
    OnEventClickListener eventClickListener;
    ArrayList<Event> events = new ArrayList<>();
    PastEventAdapter pastEventAdapter;
    CardView emptyListCard;
    SwipeRefreshLayout swipeContainer;
    LinearLayoutManager linearLayoutManager;
    public PastEventsFragment() {

    }

    public static PastEventsFragment newInstance(int sectionNumber) {
        PastEventsFragment fragment = new PastEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_past_event_list, container, false);
        View view = binding.getRoot();
        pastList = binding.pastList;
        linearLayoutManager = new LinearLayoutManager(getContext());
        pastList.setLayoutManager(linearLayoutManager);
        pastEventAdapter = new PastEventAdapter(this, getContext(), events);
        pastList.setAdapter(pastEventAdapter);
        FadeInAnimator fadeInAnimator = new FadeInAnimator();
        fadeInAnimator.setAddDuration(500);
        pastList.setItemAnimator(fadeInAnimator);
        eventClickListener = (OnEventClickListener) getActivity();
        emptyListCard = binding.emptyListCard;
        swipeContainer = binding.swipeContainer;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showPastEvents();
            }

        });
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, bufferHours); // subtract 3 hours
        eventClickListener = (OnEventClickListener) getActivity();
        showPastEvents();

        return view;
    }

    private void showPastEvents() {
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
                    if(userEvent.hasEnded() || (userEvent.getEndDate().compareTo(cal.getTime()) <= 0)){
                        events.add(userEvent);
                    }
                }
                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        return event2.getStartDate().compareTo(event1.getStartDate());

                    }
                });
                pastEventAdapter.notifyDataSetChanged();
                if(events.size() == 0){
                    pastList.setVisibility(View.GONE);
                    emptyListCard.setVisibility(View.VISIBLE);
                }

            }
        });
        swipeContainer.setRefreshing(false);
    }

    public void addEvent(Event event) {
        events.add(0, event);
        pastEventAdapter.notifyItemInserted(0);
        linearLayoutManager.scrollToPosition(0);
    }

    public void removeEvent(Event event) {
        for (int i=0; i < events.size(); i++) {
            if (event.getObjectId().equals(events.get(i).getObjectId())) {
                events.remove(i);
                pastEventAdapter.notifyItemRemoved(i);
                return;
            }
        }
    }

    @Override
    public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack, ImageView imageView) {
        eventClickListener.onItemClick(eventId, isCurrent, canTrack, imageView);
    }
}


