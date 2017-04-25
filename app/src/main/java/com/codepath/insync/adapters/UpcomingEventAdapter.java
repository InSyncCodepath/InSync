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

import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.databinding.UpcomingEventItemBinding;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.utils.CommonUtil;
import com.codepath.insync.models.parse.User;
import com.parse.ParseFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd");

        final Event event = events.get(position);
        final boolean canTrack = CommonUtil.canTrackGuests(event.getStartDate(), event.getEndDate());
        User currentUser = User.getCurrentUser();
        Date now = new Date();


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
        Date eventDate = event.getStartDate();
        int month = eventDate.getMonth() + 1;
        int date = eventDate.getDate();
        int hours = eventDate.getHours();
        int min = eventDate.getMinutes();
        String startTime = new SimpleDateFormat("hh:mm aa").format(eventDate);
        String startDate = month+"/"+date;
        holder.eventDate.setText(startDate);
        holder.eventTime.setText(startTime);
        ParseFile profileImage = event.getProfileImage();
        if (profileImage != null) {
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .placeholder(R.drawable.ic_attach_file_white_48px)
                    .crossFade()
                    .into(holder.binding.ivEventImage);
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
            eventDate = binding.tvDate;
            eventTime = binding.tvTime;
        }
    }

    public interface EventDetailClickHandling {
        public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack);
    }
}

