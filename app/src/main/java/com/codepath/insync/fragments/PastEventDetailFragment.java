package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.SeekBar;

import com.codepath.insync.R;
import com.codepath.insync.adapters.EDImageAdapter;
import com.codepath.insync.databinding.FragmentPastEventDetailBinding;
import com.codepath.insync.listeners.OnVideoCreateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.utils.Constants;
import com.codepath.insync.utils.MediaClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class PastEventDetailFragment extends Fragment implements TextureView.SurfaceTextureListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaController.MediaPlayerControl,
        OnVideoCreateListener
{
    public static final String TAG = "PastEventDetailFragment";
    FragmentPastEventDetailBinding binding;
    Event event;
    List<ParseFile> edImages;
    EDImageAdapter edImageAdapter;
    LinearLayoutManager linearLayoutManager;
    private MediaPlayer mediaPlayer;
    private MediaController mcontroller;

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
        setupRecyclerView();
        setupTouchListener();
        return binding.getRoot();
    }

    private void setupTouchListener() {
        binding.tvHighlights.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP){
                    mcontroller.show(Constants.CONTROL_SHOW_DURATION);
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
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setSurface(new Surface(surface));
        /*try {
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mcontroller = new MediaController(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        binding.pbMediaUpdate.setVisibility(View.GONE);
        binding.tvHighlights.setOpaque(true);
        start();

        mcontroller.setMediaPlayer(this);
        mcontroller.setAnchorView(binding.tvHighlights);
        mcontroller.setEnabled(true);
        mcontroller.show(Constants.CONTROL_SHOW_DURATION);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void start() {

        mediaPlayer.start();

    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
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
    public void onCreateSuccess(String videoUrl) {
        Log.d(TAG, "Video created successfully. Url: "+videoUrl);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setDataSource(videoUrl);
                mediaPlayer.prepare();
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer.setOnVideoSizeChangedListener(this);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                mcontroller = new MediaController(getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCreateFailure(int status, String message) {
        Log.e(TAG, "Video could not be created. status: "+status+", message: "+message);
        binding.tvHighlights.setVisibility(View.INVISIBLE);
        binding.pbMediaUpdate.setVisibility(View.GONE);
        

        animateImages();
    }
}
