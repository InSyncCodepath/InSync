package com.codepath.insync.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.insync.R;

import java.util.ArrayList;

/**
 * Created by Gauri Gadkari on 4/10/17.
 */

public class InviteeAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.UpcomingEventViewHolder>  {
    ArrayList<String> invitees;
    @Override
    public UpcomingEventAdapter.UpcomingEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(android.R.layout.simple_list_item_1, parent, false);

        return new UpcomingEventAdapter.UpcomingEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UpcomingEventAdapter.UpcomingEventViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return invitees.size();
    }
}
