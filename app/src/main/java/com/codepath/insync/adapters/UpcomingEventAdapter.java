package com.codepath.insync.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.insync.R;
import com.codepath.insync.databinding.UpcomingEventItemBinding;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.utils.DateUtil;
import com.codepath.insync.models.parse.User;

import java.util.ArrayList;

/**
 * Created by Gauri Gadkari on 4/6/17.
 */

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.UpcomingEventViewHolder> {
    ArrayList<Event> events;
    Context context;
    EventDetailClickHandling listener;
    @Override
    public UpcomingEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(context).
                inflate(R.layout.upcoming_event_item, parent, false);
        return new UpcomingEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UpcomingEventViewHolder holder, int position) {
        final Event event = events.get(position);
        final boolean canTrack = DateUtil.canTrackGuests(event.getStartDate(), event.getEndDate());
        User currentUser = User.getCurrentUser();

        //boolean isUserInvited
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEventItemClick(event.getObjectId(), true, canTrack);
            }
        });
        //holder.binding.tvDate.setText(event.getStartDate().toString());
        holder.eventName.setText(event.getName());
        holder.binding.tvAddress.setText(event.getAddress());

        Bitmap profileBitmap = event.getProfileImageBitmap();
        if (profileBitmap != null) {
            holder.binding.ivEventImage.setImageBitmap(profileBitmap);
        }

        //holder.binding.tvTime.setText();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public UpcomingEventAdapter(Fragment fragment, Context context, ArrayList<Event> events) {
        this.listener = (EventDetailClickHandling) fragment;
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

    public interface EventDetailClickHandling {
        public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack);
    }
}

