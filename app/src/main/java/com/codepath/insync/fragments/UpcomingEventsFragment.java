package com.codepath.insync.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.insync.R;

/**
 * Created by Gauri Gadkari on 4/6/17.
 */

    public class UpcomingEventsFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

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
            View rootView = inflater.inflate(R.layout.fragment_upcoming_event_list, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

