package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.adapters.EDImageAdapter;
import com.codepath.insync.databinding.FragmentPastEventWaitBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static android.R.attr.pointerIcon;
import static android.R.attr.width;
import static com.facebook.FacebookSdk.getApplicationContext;


public class PastEventWaitFragment extends Fragment {
    FragmentPastEventWaitBinding binding;
    boolean doWait;

    public static PastEventWaitFragment newInstance(boolean wait, ArrayList<String> images, int position) {
        Bundle args = new Bundle();

        PastEventWaitFragment pastEventWaitFragment = new PastEventWaitFragment();
        args.putBoolean("do_wait", wait);
        if (images != null) {
            args.putStringArrayList("images", images);
            args.putInt("position", position);
        }
        pastEventWaitFragment.setArguments(args);
        return pastEventWaitFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doWait = getArguments().getBoolean("do_wait", true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_past_event_wait, container, false);
        if (doWait) {
            binding.rvGallery.setVisibility(View.GONE);
        } else {
            binding.pbMediaUpdate.setVisibility(View.GONE);
            ArrayList<String> images = getArguments().getStringArrayList("images");
            int position = getArguments().getInt("position");

            setupRecyclerView(images, position);
        }
        return binding.getRoot();
    }

    private void setupRecyclerView(ArrayList<String> images, int position) {
        EDImageAdapter galleryImageAdapter = new EDImageAdapter(getActivity(), images, R.layout.item_galleryimage, 0);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(galleryImageAdapter);
        ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        scaleInAdapter.setDuration(500);
        binding.rvGallery.setAdapter(scaleInAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvGallery.setLayoutManager(linearLayoutManager);
        linearLayoutManager.scrollToPosition(position);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvGallery);
    }

}
