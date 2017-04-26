package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.codepath.insync.R;
import com.codepath.insync.adapters.EDImageAdapter;
import com.codepath.insync.databinding.FragmentPastEventDetailBinding;
import com.codepath.insync.listeners.OnImageClickListener;
import com.codepath.insync.listeners.OnVideoUpdateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.Music;
import com.codepath.insync.utils.VideoPlayer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class PastEventDetailFragment extends Fragment implements TextureView.SurfaceTextureListener,
        OnVideoUpdateListener
{
    public static final String TAG = "PastEventDetailFragment";
    FragmentPastEventDetailBinding binding;
    Event event;
    ArrayList<String> edImages;
    List<ParseFile> parseFiles;
    EDImageAdapter edImageAdapter;
    GridLayoutManager gridLayoutManager;
    VideoPlayer videoPlayer;
    ImageView slide_0;
    ImageView slide_1;
    ImageView lastSlide;
    int count;
    int lastDownAnim;
    int lastUpAnim;
    OnImageClickListener onImageClickListener;
    boolean isPlayerEnabled;

    private Handler timerHandler = new Handler();



    public static PastEventDetailFragment newInstance(String eventId, String eventName, String theme) {

        Bundle args = new Bundle();

        PastEventDetailFragment pastEventDetailFragment = new PastEventDetailFragment();
        args.putString("eventId", eventId);
        args.putString("eventName", eventName);
        args.putString("theme", theme);

        pastEventDetailFragment.setArguments(args);
        return pastEventDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isPlayerEnabled = false;
        event = new Event();
        event.setObjectId(getArguments().getString("eventId"));
        event.setName(getArguments().getString("eventName"));
        String theme = getArguments().getString("theme");
        if (theme != null) {
            event.setTheme(theme);
        }

        parseFiles = new ArrayList<>();
        edImages = new ArrayList<>();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        edImageAdapter = new EDImageAdapter(getActivity(), edImages, R.layout.item_edimage, width);
        onImageClickListener = (OnImageClickListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_past_event_detail, container, false);

        binding.tvHighlights.setSurfaceTextureListener(this);
        videoPlayer = new VideoPlayer(getContext(), this, binding.tvHighlights);
        slide_0 = binding.slide1;
        slide_1 = binding.slide2;


        setupRecyclerView();
        setupClickListener();
        return binding.getRoot();
    }

    private void setupClickListener() {
        setupTouchListener();
        binding.fabEDPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.playVideo();
                count = 0;
                lastSlide = slide_0;
                lastDownAnim = R.anim.transition_down_center;
                lastUpAnim = R.anim.transition_up_center;
                binding.fabEDPlay.setVisibility(View.GONE);
            }
        });
    }

    private void setupTouchListener() {
        binding.tvHighlights.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP && isPlayerEnabled){
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
        binding.pbMediaUpdate.setVisibility(View.VISIBLE);

        ParseQuery<Message> parseQuery = event.getMessageRelation().getQuery();
        parseQuery.whereExists("media");
        parseQuery.orderByAscending("createdAt");

        parseQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e == null) {
                    edImages.clear();
                    parseFiles.clear();
                    for (ParseObject imageObject: objects) {
                        String imageUrl = null;
                        ParseFile imgFile = imageObject.getParseFile("media");
                        if (imgFile != null) {
                            imageUrl = imageObject.getParseFile("media").getUrl();
                            parseFiles.add(imgFile);
                            edImages.add(imageUrl);

                        }
                    }
                    edImageAdapter.notifyDataSetChanged();
                    if (parseFiles.size() > 0) {
                        binding.tvTitleGallery.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e(TAG, "Error fetching event album");
                }
            }
        });
    }

    private void setupRecyclerView() {
        binding.rvEDImages.setAdapter(edImageAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        binding.rvEDImages.setLayoutManager(gridLayoutManager);
        edImageAdapter.setOnItemClickListener(new EDImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                onImageClickListener.onItemClick(edImages, position);
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        videoPlayer.setupPlayer(surface);
        String theme = event.getTheme() == null ? "default": event.getTheme();
        Music.findMusic(theme, new FindCallback<Music>() {
            @Override
            public void done(List<Music> musics, ParseException e) {
                if (e == null) {
                    String audioUrl = musics.get(0).getAudio().getUrl();
                    videoPlayer.prepareVideo(audioUrl);
                } else {
                    Log.e(TAG, "Music lookup failed with error: "+e.getLocalizedMessage());

                }
            }
        });

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
    public void onPrepare() {
        binding.pbMediaUpdate.setVisibility(View.GONE);
        if (parseFiles.size() <= 0) {
            binding.tvEDWarning.setVisibility(View.VISIBLE);
            return;
        }
        binding.fabEDPlay.setVisibility(View.VISIBLE);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(parseFiles.get(0).getDataStream());
            slide_1.setImageBitmap(bitmap);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStartVideo() {
        isPlayerEnabled = true;
        animateSlideShow();
    }

    @Override
    public void onPauseVideo() {
        stopAnimation();
    }

    @Override
    public void onComplete() {
        if (parseFiles.size() <= 0) {
            binding.tvEDWarning.setVisibility(View.VISIBLE);
            return;
        }
        binding.fabEDPlay.setVisibility(View.VISIBLE);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(parseFiles.get(0).getDataStream());
            slide_1.setImageBitmap(bitmap);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        isPlayerEnabled = false;
        stopAnimation();
    }

    @Override
    public void onPause() {
        videoPlayer.stopVideo();
        stopAnimation();
        super.onPause();
    }

    @Override
    public void onStop() {
        videoPlayer.stopVideo();
        stopAnimation();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        videoPlayer.stopVideo();
        stopAnimation();
        super.onDestroy();
    }

    private void animateSlideShow() {
        if (parseFiles.size() == 0) {
            return;
        }
        timerHandler.post(timer);
    }

    private Runnable timer = new Runnable() {
        public void run() {
            if (lastSlide == slide_0) {
                slide_1.setImageResource(0);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(parseFiles.get((count) % (parseFiles.size())).getDataStream());
                    slide_1.setImageBitmap(bitmap);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                lastDownAnim = lastDownAnim == R.anim.transition_down ? R.anim.transition_down_center : R.anim.transition_down;
                slide_1.startAnimation(AnimationUtils
                        .loadAnimation(getContext(),
                                lastDownAnim));
                lastSlide = slide_1;
            } else {
                slide_0.setImageResource(0);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(parseFiles.get((count) % (parseFiles.size())).getDataStream());
                    slide_0.setImageBitmap(bitmap);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                lastUpAnim = lastUpAnim == R.anim.transition_up ? R.anim.transition_up_center : R.anim.transition_up;
                slide_0.startAnimation(AnimationUtils
                        .loadAnimation(getContext(),
                                lastUpAnim));
                lastSlide = slide_0;
            }
            count++;
            timerHandler.postDelayed(timer, 4000);
        }
    };

    public void stopAnimation() {
        timerHandler.removeCallbacks(timer);
        slide_0.clearAnimation();
        slide_1.clearAnimation();
    }
}
