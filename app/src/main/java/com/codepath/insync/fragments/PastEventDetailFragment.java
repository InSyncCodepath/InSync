package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.insync.R;
import com.codepath.insync.adapters.EDImageAdapter;
import com.codepath.insync.databinding.FragmentPastEventDetailBinding;
import com.codepath.insync.listeners.OnVideoCreateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.utils.Constants;
import com.codepath.insync.utils.MediaClient;
import com.codepath.insync.utils.VideoPlayer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class PastEventDetailFragment extends Fragment implements TextureView.SurfaceTextureListener,
        OnVideoCreateListener
{
    public static final String TAG = "PastEventDetailFragment";
    FragmentPastEventDetailBinding binding;
    Event event;
    List<ParseFile> edImages;
    EDImageAdapter edImageAdapter;
    LinearLayoutManager linearLayoutManager;
    VideoPlayer videoPlayer;

    public static PastEventDetailFragment newInstance(String eventId, String eventName) {

        Bundle args = new Bundle();

        PastEventDetailFragment pastEventDetailFragment = new PastEventDetailFragment();
        args.putString("eventId", eventId);
        args.putString("eventName", eventName);

        pastEventDetailFragment.setArguments(args);
        return pastEventDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        event = new Event();
        event.setObjectId(getArguments().getString("eventId"));
        event.setName(getArguments().getString("eventName"));
        edImages = new ArrayList<>();
        edImageAdapter = new EDImageAdapter(getActivity(), edImages);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_past_event_detail, container, false);

        binding.tvHighlights.setSurfaceTextureListener(this);
        videoPlayer = new VideoPlayer(getContext(), this, binding.tvHighlights);

        setupRecyclerView();
        setupTouchListener();
        return binding.getRoot();
    }

    private void setupTouchListener() {
        binding.tvHighlights.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP){
                    videoPlayer.showController();
                }
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvHighlights.setOpaque(false);
        ParseQuery<ParseObject> parseQuery = event.getAlbumRelation().getQuery();

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    edImages.clear();
                    for (ParseObject imageObject: objects) {
                        edImages.add(imageObject.getParseFile("image"));
                    }
                    edImageAdapter.notifyDataSetChanged();
                    //animateImages();
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
        videoPlayer.setupPlayer(surface);

        MediaClient mediaClient = new MediaClient(this);
        mediaClient.createHighlights(getContext(), event, edImages, "party");
        binding.pbMediaUpdate.setVisibility(View.VISIBLE);

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

    public void animateImages() {
        AnimationDrawable anim = new AnimationDrawable();
        for (ParseFile image : edImages) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(image.getDataStream());
                anim.addFrame(new BitmapDrawable(getResources(), bitmap), Constants.CLIP_DURATION*1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        binding.ivHighlights.setImageDrawable(anim);
        anim.setEnterFadeDuration(Constants.FADE_DURATION);
        anim.setExitFadeDuration(Constants.FADE_DURATION);
        anim.setOneShot(false);
        anim.start();
    }

    @Override
    public void onPrepare() {
        binding.pbMediaUpdate.setVisibility(View.GONE);
        binding.tvHighlights.setOpaque(true);
    }

    @Override
    public void onCreateSuccess(String videoUrl) {
        Log.d(TAG, "Video created successfully. Url: "+videoUrl);
        videoPlayer.playVideo(videoUrl);


    }

    @Override
    public void onCreateFailure(int status, String message) {
        Log.e(TAG, "Video could not be created. status: "+status+", message: "+message);
        binding.tvHighlights.setVisibility(View.INVISIBLE);
        binding.pbMediaUpdate.setVisibility(View.GONE);

        animateImages();
    }

    @Override
    public void onPause() {
        videoPlayer.stopVideo();
        super.onPause();
    }
}
