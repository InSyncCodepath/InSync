package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.insync.R;
import com.codepath.insync.adapters.PastEventAdapter;
import com.codepath.insync.databinding.FragmentPastEventListBinding;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.models.parse.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.R.id.list;


public class PastEventsFragment extends Fragment implements PastEventAdapter.EventDetailClickHandling {
    FragmentPastEventListBinding binding;
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerView pastList;
    OnEventClickListener eventClickListener;
    ArrayList<Event> events = new ArrayList<>();
    PastEventAdapter pastEventAdapter;
    int bufferHours = 3;
    Calendar cal = Calendar.getInstance(); // creates calendar
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        pastList.setLayoutManager(linearLayoutManager);
        pastEventAdapter = new PastEventAdapter(this, getContext(), events);
        pastList.setAdapter(pastEventAdapter);
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, bufferHours); // adds three buffer hours
        eventClickListener = (OnEventClickListener) getActivity();
        ParseQuery<Event> queryTime = ParseQuery.getQuery(Event.class);
        queryTime.whereLessThan("endDate", cal.getTime());

        ParseQuery<Event> queryEnded = ParseQuery.getQuery(Event.class);
        queryEnded.whereEqualTo("hasEnded", true);

        List<ParseQuery<Event>> queryList = new ArrayList<ParseQuery<Event>>();
        queryList.add(queryTime);
        queryList.add(queryEnded);

        ParseQuery<Event> query = ParseQuery.or(queryList);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                events.addAll(objects);
                pastEventAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack) {
        eventClickListener.onItemClick(eventId, isCurrent, canTrack);
    }
}


