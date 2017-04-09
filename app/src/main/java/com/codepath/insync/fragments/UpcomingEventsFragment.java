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
import com.codepath.insync.interfaces.OnEventClickListener;
import com.codepath.insync.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class UpcomingEventsFragment extends Fragment implements UpcomingEventAdapter.EventDetailClickHandling {
    FragmentUpcomingEventListBinding binding;
    OnEventClickListener eventClickListener;

    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerView upcomingList;
    ArrayList<Event> events = new ArrayList<>();
    UpcomingEventAdapter upcomingEventAdapter;
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
        eventClickListener = (OnEventClickListener) getActivity();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                events.addAll(objects);
                upcomingEventAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onEventItemClick(String objectId) {
       eventClickListener.onItemClick(objectId);
    }

    public void reloadList(){
        upcomingEventAdapter.notifyDataSetChanged();
    }
}

