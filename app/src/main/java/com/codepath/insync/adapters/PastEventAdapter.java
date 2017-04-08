package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import com.codepath.insync.databinding.PastEventItemBinding;
import com.codepath.insync.models.Event;

import java.util.ArrayList;

/**
 * Created by Gauri Gadkari on 4/6/17.
 */

public class PastEventAdapter extends RecyclerView.Adapter<PastEventAdapter.PastEventViewHolder> {
    ArrayList<Event> events;
    Context context;

    @Override
    public PastEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(PastEventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.binding.tvEventName.setText(event.getName());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public PastEventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    public static class PastEventViewHolder extends RecyclerView.ViewHolder {
        PastEventItemBinding binding;
        public PastEventViewHolder(View itemView) {
            super(itemView);
            binding = PastEventItemBinding.bind(itemView);

        }
    }
}
