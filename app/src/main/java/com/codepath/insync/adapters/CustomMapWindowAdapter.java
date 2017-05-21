package com.codepath.insync.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.codepath.insync.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


public class CustomMapWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater mInflater;

    public CustomMapWindowAdapter(LayoutInflater inflater){
        mInflater = inflater;
    }

    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {

        // Getting view from the layout file
        View v = mInflater.inflate(R.layout.layout_custom_map_window, null);
        // Populate fields
        TextView title = (TextView) v.findViewById(R.id.tv_map_user_info);
        title.setText(marker.getTitle());

        // Return info window contents
        return v;
    }
}