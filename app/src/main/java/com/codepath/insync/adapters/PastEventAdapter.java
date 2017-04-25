package com.codepath.insync.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.databinding.PastEventItemBinding;
import com.codepath.insync.listeners.OnVideoUpdateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.utils.VideoPlayer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.animation;
import static com.codepath.insync.R.id.ivEventImage;


public class PastEventAdapter extends RecyclerView.Adapter<PastEventAdapter.PastEventViewHolder> implements TextureView.SurfaceTextureListener {
    ArrayList<Event> events;
    Context context;
    EventDetailClickHandling listener;
    ArrayList<String> edImages;
    List<ParseFile> parseFiles;

    @Override
    public PastEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(context).
                inflate(R.layout.past_event_item, parent, false);

        return new PastEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PastEventViewHolder holder, int position) {
        if (parseFiles == null) {
            parseFiles = new ArrayList<>();
        }
        if (edImages == null) {
            edImages = new ArrayList<>();
        }
        final Event event = events.get(position);
        Date now = new Date();
        final boolean isCurrent = event.getEndDate().compareTo(now) >= 0;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEventItemClick(event.getObjectId(), false, false, holder.binding.ivHighlights);
            }
        });
        holder.eventName.setText(event.getName());
        ParseFile profileImage = event.getProfileImage();
        if (profileImage != null) {
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .placeholder(R.drawable.ic_attach_file_white_48px)
                    .crossFade()
                    .into(holder.binding.ivHighlights);
        }
        ViewCompat.setTransitionName(holder.itemView, event.getObjectId());
//        Glide.with(context).load(event.getProfileImage()).into(holder.binding.ivHighlights);
//
//        ParseQuery<Message> parseQuery = event.getMessageRelation().getQuery();
//        parseQuery.whereExists("media");
//
//        parseQuery.findInBackground(new FindCallback<Message>() {
//            @Override
//            public void done(List<Message> objects, ParseException e) {
//                if (e == null) {
//                    edImages.clear();
//                    parseFiles.clear();
//                    for (ParseObject imageObject : objects) {
//                        String imageUrl = null;
//                        ParseFile imgFile = imageObject.getParseFile("media");
//                        if (imgFile != null) {
//                            imageUrl = imageObject.getParseFile("media").getUrl();
//                            parseFiles.add(imgFile);
//                            edImages.add(imageUrl);
//
//
//                        }
//                    }
//                }
//                if (parseFiles.size() > 0) {
//                    AnimationDrawable animation = new AnimationDrawable();
//                    //for (int i = 0; i < 3; i++) {
//                        byte[] bitmapdata = parseFiles.get(0).getUrl().getBytes();
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
//                        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
//                        animation.addFrame(d, 2000);
//                    //}
////            animation.addFrame(context.getResources().getDrawable(R.drawable.ic_arrow_back), 2000);
////            animation.addFrame(context.getResources().getDrawable(R.drawable.ic_decline_selected), 2000);
//                    animation.setOneShot(false);
//
//                    holder.highlights.setImageDrawable(animation);
//                    animation.start();
//                } else {
//                    //set profile pic
//                    Glide.with(context).load(event.getProfileImage().getUrl()).into(holder.binding.ivHighlights);
//                }
//
//            }
//        });
//
//        //Glide.with(context).load(event.getHighlightsVideo().getUrl()).into(holder.binding.highlightsVideo);

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
        ImageView highlights;
        TextView eventName;

        public PastEventViewHolder(View itemView) {
            super(itemView);
            binding = PastEventItemBinding.bind(itemView);
            highlights = binding.ivHighlights;
            eventName = binding.tvEventName;
        }
    }

    public interface EventDetailClickHandling {
        public void onEventItemClick(String eventId, boolean isCurrent, boolean canTrack, ImageView imageView);
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
