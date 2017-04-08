package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.databinding.UpcomingEventItemBinding;
import com.codepath.insync.models.Event;

import java.util.ArrayList;

import static com.codepath.insync.R.id.tvEventName;

/**
 * Created by Gauri Gadkari on 4/6/17.
 */

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.UpcomingEventViewHolder> {
    ArrayList<Event> events;
    Context context;
    @Override
    public UpcomingEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(context).
                inflate(R.layout.upcoming_event_item, parent, false);

        return new UpcomingEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UpcomingEventViewHolder holder, int position) {
        Event event = events.get(position);
        //holder.binding.tvDate.setText(event.getStartDate().toString());
        holder.eventName.setText(event.getName());
                //.setText("Event Name");
                //.setText(event.getName());
        holder.binding.tvAddress.setText(event.getAddress());
        Glide.with(context).load(event.getProfileImage().getUrl()).into(holder.binding.ivEventImage);
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
        TextView eventName, eventDate, eventTime, eventLocation;
        ImageView eventImage;
        public UpcomingEventViewHolder(View itemView) {
            super(itemView);
            binding = UpcomingEventItemBinding.bind(itemView);
            eventName = (TextView) itemView.findViewById(R.id.tvEventName);


        }
    }
}

