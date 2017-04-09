package com.codepath.insync.fragments;

import android.support.v4.app.Fragment;

import com.codepath.insync.R;

import java.util.ResourceBundle;

import static java.security.AccessController.getContext;

/**
 * Created by Gauri Gadkari on 4/8/17.
 */

public class LocationFragment extends Fragment {
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private final String API_KEY = getActivity().getResources().getString(R.string.google_maps_api_key);

}
