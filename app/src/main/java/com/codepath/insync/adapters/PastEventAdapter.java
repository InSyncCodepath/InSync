package com.codepath.insync.adapters;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;


import com.codepath.insync.R;
import com.codepath.insync.databinding.PastEventItemBinding;
import com.codepath.insync.listeners.OnVideoUpdateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.utils.VideoPlayer;

import java.util.ArrayList;
import java.util.Date;


public class PastEventAdapter extends RecyclerView.Adapter<PastEventAdapter.PastEventViewHolder> implements TextureView.SurfaceTextureListener{
    ArrayList<Event> events;
    Context context;
    EventDetailClickHandling listener;
    @Override
    public PastEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(context).
                inflate(R.layout.past_event_item, parent, false);

        return new PastEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PastEventViewHolder holder, int position) {

        final Event event = events.get(position);
        Date now = new Date();
        final boolean isCurrent = event.getEndDate().compareTo(now) >= 0;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEventItemClick(event.getObjectId(), false, false);
            }
        });
        holder.binding.tvEventName.setText(event.getName());
        //Glide.with(context).load(event.getHighlightsVideo().getUrl()).into(holder.binding.highlightsVideo);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public PastEventAdapter(Fragment fragment, Context context, ArrayList<Event> events) {
        this.listener = (EventDetailClickHandling) fragment;
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

    public interface EventDetailClickHandling {
        public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
