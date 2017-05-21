package com.codepath.insync.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import com.codepath.insync.R;
import com.codepath.insync.adapters.EDImageAdapter;
import com.codepath.insync.databinding.ActivityFullScreenImageBinding;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class FullScreenImageActivity extends AppCompatActivity {

    ActivityFullScreenImageBinding binding;
    ArrayList<String> images;
    int position;
    TextView tvShare;

    Handler tbHandler = new Handler();

    Runnable tbRunnable =new Runnable() {

        @Override
        public void run() {
            binding.tbFullScreenImage.setVisibility(View.INVISIBLE);
            if (tvShare != null) {
                tvShare.setVisibility(View.INVISIBLE);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_screen_image);
        images = getIntent().getStringArrayListExtra("images");
        position = getIntent().getIntExtra("position", 0);
        setupRecyclerView(images, position);
        tvShare = (TextView) findViewById(R.id.tvShare);
        setSupportActionBar(binding.tbFullScreenImage);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            binding.tvFullScreenImage.setText(getIntent().getStringExtra("eventName"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tbHandler.removeCallbacks(tbRunnable);
        tbHandler.postDelayed(tbRunnable, 3000);


    }

    private void setupRecyclerView(final ArrayList<String> images, int position) {
        EDImageAdapter galleryImageAdapter = new EDImageAdapter(this, images, R.layout.item_galleryimage, 0);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(galleryImageAdapter);
        alphaAdapter.setDuration(500);
        binding.rvGallery.setAdapter(alphaAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvGallery.setLayoutManager(linearLayoutManager);
        linearLayoutManager.scrollToPosition(position);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvGallery);
        galleryImageAdapter.setOnItemClickListener(new EDImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (itemView.getId() == R.id.tvShare) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, images.get(position));
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                }

                tvShare = (TextView) findViewById(R.id.tvShare);
                tvShare.setVisibility(View.VISIBLE);

                binding.tbFullScreenImage.setVisibility(View.VISIBLE);
                tbHandler.removeCallbacks(tbRunnable);
                tbHandler.postDelayed(tbRunnable, 3000);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
