package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.insync.databinding.UpcomingEventItemBinding;
import com.codepath.insync.models.Event;

import java.util.ArrayList;

/**
 * Created by Gauri Gadkari on 4/6/17.
 */

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.UpcomingEventViewHolder> {
    ArrayList<Event> events;
    Context context;
    @Override
    public UpcomingEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(UpcomingEventViewHolder holder, int position) {
        Event event = events.get(position);
        //holder.binding.tvDate.setText(event.getStartDate().toString());
        holder.binding.tvEventName.setText(event.getName());
        holder.binding.tvLocation.setText(event.getAddress());
        //holder.binding.tvTime.setText();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public UpcomingEventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    public static class UpcomingEventViewHolder extends RecyclerView.ViewHolder {
        UpcomingEventItemBinding binding;
//        TextView eventName, eventDate, eventTime, eventLocation;
//        ImageView eventImage;
        public UpcomingEventViewHolder(View itemView) {
            super(itemView);
            binding = UpcomingEventItemBinding.bind(itemView);
        }
    }
}

