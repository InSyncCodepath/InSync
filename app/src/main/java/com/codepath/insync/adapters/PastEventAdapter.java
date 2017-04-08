package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.codepath.insync.R;
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
        View itemView = LayoutInflater.
                from(context).
                inflate(R.layout.past_event_item, parent, false);

        return new PastEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PastEventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.binding.tvEventName.setText(event.getName());
        //Glide.with(context).load(event.getHighlightsVideo().getUrl()).into(holder.binding.highlightsVideo);
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
