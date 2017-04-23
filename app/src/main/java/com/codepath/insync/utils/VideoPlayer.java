package com.codepath.insync.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.MediaController;

import com.codepath.insync.R;
import com.codepath.insync.listeners.OnVideoUpdateListener;

public class VideoPlayer implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaController.MediaPlayerControl {
    private MediaPlayer mediaPlayer;
    private MediaController mcontroller;
    private Context mContext;
    private OnVideoUpdateListener videoUpdateListener;
    private View controllerAnchorView;

    public VideoPlayer(Context context, OnVideoUpdateListener listener, View anchorView) {
        mContext = context;
        videoUpdateListener = listener;
        controllerAnchorView = anchorView;
    }

    public void setupPlayer(SurfaceTexture surface) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setSurface(new Surface(surface));
    }

    public void prepareVideo(String videoUrl) {
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
                mcontroller = new MediaController(mContext);
                mcontroller.setBackgroundColor(ContextCompat.getColor(mContext, R.color.accent));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playVideo() {
        start();

        mcontroller.setMediaPlayer(this);
        mcontroller.setAnchorView(controllerAnchorView);
        mcontroller.setEnabled(true);
        mcontroller.show(Constants.CONTROL_SHOW_DURATION);
    }

    public void stopVideo() {
        if(mediaPlayer !=null) {

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            if (mcontroller != null) {
                mcontroller.hide();
            }

        }
    }

    public void showController() {
        mcontroller.show(Constants.CONTROL_SHOW_DURATION);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("VideoPlayer", "Completed");
        mp.seekTo(0);
        videoUpdateListener.onComplete();

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoUpdateListener.onPrepare();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void start() {
        mediaPlayer.start();
        videoUpdateListener.onStartVideo();
    }

    @Override
    public void pause() {

        mediaPlayer.pause();
        videoUpdateListener.onPauseVideo();
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

}
