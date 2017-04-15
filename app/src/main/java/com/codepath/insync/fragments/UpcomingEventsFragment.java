package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Date;
import java.util.List;

import static com.codepath.insync.R.string.username;
import static com.codepath.insync.models.parse.UserEventRelation.getEventPointerKey;


public class UpcomingEventsFragment extends Fragment implements UpcomingEventAdapter.EventDetailClickHandling {
    FragmentUpcomingEventListBinding binding;
    OnEventClickListener eventClickListener;

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

        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, bufferHours); // subtract 3 hours
        eventClickListener = (OnEventClickListener) getActivity();
        User currentUser = User.getCurrentUser();
        ParseQuery<UserEventRelation> query = ParseQuery.getQuery(UserEventRelation.class);
        query.selectKeys(Arrays.asList("event"));
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
                upcomingEventAdapter.notifyDataSetChanged();


            }
        });

        return view;
    }


    @Override
    public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack) {
       eventClickListener.onItemClick(eventId, isCurrent, canTrack);
    }

    public void reloadList() {
        upcomingEventAdapter.notifyDataSetChanged();
    }
}

