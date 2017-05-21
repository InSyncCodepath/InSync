package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ItemEventBinding;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.utils.CommonUtil;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private ArrayList<Event> events;
    Context context;
    private OnEventClickListener listener;

    class EventViewHolder extends RecyclerView.ViewHolder {
        ItemEventBinding binding;
        ImageView ivEventImage;
        ImageView ivEventImagePL;
        TextView eventName, eventDate, eventAddress;

        EventViewHolder(View itemView) {
            super(itemView);
            binding = ItemEventBinding.bind(itemView);
            ivEventImage = binding.ivHighlights;
            ivEventImagePL = binding.ivHighlightsPL;
            eventName = binding.tvEventName;
            eventDate = binding.tvDate;
            eventAddress = binding.tvAddress;
        }
    }


    public EventAdapter(Fragment fragment, Context context, ArrayList<Event> events) {
        this.listener = (OnEventClickListener) fragment;
        this.context = context;
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        final Event event = events.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, -3); // subtract 3 hours
        final boolean isCurrent = (!event.hasEnded() && (event.getEndDate().compareTo(cal.getTime()) > 0));
        final boolean canTrack = CommonUtil.canTrackGuests(event.getStartDate(), event.getEndDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(event.getObjectId(), isCurrent, canTrack, holder.binding.ivHighlights);
            }
        });
        holder.eventName.setText(event.getName());
        holder.binding.tvAddress.setText(event.getAddress());
        holder.eventDate.setText(CommonUtil.getDateTimeInFormat(event.getStartDate()));
        ParseFile profileImage = event.getProfileImage();

        String imgUrl;
        if (profileImage != null) {
            imgUrl = profileImage.getUrl();
        } else {
            imgUrl = event.getString("imageUrl");
        }

        holder.binding.ivHighlights.setImageResource(R.drawable.ic_camera_alt_white_48px);
        holder.binding.ivHighlights.setVisibility(View.INVISIBLE);
        holder.binding.ivHighlightsPL.setVisibility(View.VISIBLE);
        if (imgUrl != null) {
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_camera_alt_white_48px)
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.binding.ivHighlightsPL.setVisibility(View.GONE);
                            holder.binding.ivHighlights.setVisibility(View.VISIBLE);
                            holder.binding.ivHighlights.setImageDrawable(resource);
                        }
                    });
        }
        ViewCompat.setTransitionName(holder.itemView, event.getObjectId());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
