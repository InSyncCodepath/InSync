package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.databinding.FragmentPastEventWaitBinding;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.facebook.FacebookSdk.getApplicationContext;


public class PastEventWaitFragment extends Fragment {
    FragmentPastEventWaitBinding binding;
    boolean doWait;

    public static PastEventWaitFragment newInstance(boolean wait, String imageUrl) {
        Bundle args = new Bundle();

        PastEventWaitFragment pastEventWaitFragment = new PastEventWaitFragment();
        args.putBoolean("do_wait", wait);
        if (imageUrl != null) {
            args.putString("image_url", imageUrl);
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
            binding.ivScaleImage.setVisibility(View.GONE);
        } else {
            binding.pbMediaUpdate.setVisibility(View.GONE);
            String imageUrl = getArguments().getString("image_url");
            if (imageUrl != null) {
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_camera_alt_white_48px)
                        .crossFade()
                        .bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 4, 0))
                        .into(binding.ivScaleImage);
            }


        }
        return binding.getRoot();
    }

}
