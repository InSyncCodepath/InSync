package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.codepath.insync.R;
import com.codepath.insync.adapters.LocationAdapter;
import com.codepath.insync.databinding.LocationFragmentBinding;

import java.util.ResourceBundle;

import static java.security.AccessController.getContext;

/**
 * Created by Gauri Gadkari on 4/8/17.
 */

public class LocationFragment extends Fragment {
    LocationFragmentBinding binding;

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private final String API_KEY = getActivity().getResources().getString(R.string.google_maps_api_key);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.location_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AutoCompleteTextView autoCompView = binding.autoCompleteTextView;
        //autoCompView.setAdapter(new LocationAdapter(this, R.layout.location_item));

    }
}
