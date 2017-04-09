package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.insync.R;
import com.codepath.insync.adapters.EDImageAdapter;
import com.codepath.insync.databinding.FragmentPastEventDetailBinding;
import com.codepath.insync.models.Event;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PastEventDetailFragment extends Fragment implements TextureView.SurfaceTextureListener {
    public static final String TAG = "PastEventDetailFragment";
    FragmentPastEventDetailBinding binding;
    String eventId;
    List<ParseFile> edImages;
    EDImageAdapter edImageAdapter;
    LinearLayoutManager linearLayoutManager;
    String videoUrl;


    public static PastEventDetailFragment newInstance(String eventId, String videoUrl) {

        Bundle args = new Bundle();

        PastEventDetailFragment pastEventDetailFragment = new PastEventDetailFragment();
        args.putString("eventId", eventId);
        args.putString("videoUrl", videoUrl);

        pastEventDetailFragment.setArguments(args);
        return pastEventDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventId = getArguments().getString("eventId");
        videoUrl = getArguments().getString("videoUrl");
        edImages = new ArrayList<>();
        edImageAdapter = new EDImageAdapter(getActivity(), edImages);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_past_event_detail, container, false);

        binding.tvHighlights.setSurfaceTextureListener(this);
        setupRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Event event = new Event();
        event.setObjectId(eventId);

        event.getAlbumRelation().getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    edImages.clear();
                    for (ParseObject imageObject: objects) {
                        edImages.add(imageObject.getParseFile("image"));
                    }
                    edImageAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error fetching event album");
                }
            }
        });

    }

    private void setupRecyclerView() {
        binding.rvEDImages.setAdapter(edImageAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvEDImages.setLayoutManager(linearLayoutManager);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setSurface(new Surface(surface));
        try {
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
