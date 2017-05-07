package com.codepath.insync.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityCameraBinding;

import java.io.IOException;


public class CameraActivity extends AppCompatActivity {
    MaterialCamera camera;
    private final static int CAMERA_RQ = 6969;
    ActivityCameraBinding binding;
    String filePath;
    boolean isProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        setupClickListener();
        camera = new MaterialCamera(this)
//                .iconFrontCamera(R.drawable.mcam_camera_front)
//                .iconRearCamera(R.drawable.mcam_camera_rear)
                .primaryColorAttr(R.attr.colorPrimary)
        ;
        filePath = null;

        String imageUri = getIntent().getStringExtra("image_uri");
        isProfilePic = getIntent().getBooleanExtra("is_profile_pic", false);
        if (imageUri == null) {
            camera.stillShot()
                    .start(CAMERA_RQ);
        } else {
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imageUri));
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }

            binding.ivSelectedImage.setVisibility(View.VISIBLE);
            binding.clSendSelected.setVisibility(View.VISIBLE);
            binding.camera.setVisibility(View.GONE);
            binding.ivSelectedImage.setImageBitmap(selectedImage);

        }

    }

    private void setupClickListener() {
        binding.fabEDSendSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("filePath",filePath);
                returnIntent.putExtra("message", binding.etSelectedMessage.getText().toString());

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_RQ) {

            if (resultCode == RESULT_OK) {
                filePath = data.getData().getPath();
                if (isProfilePic) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("filePath",filePath);

                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                } else {
                    binding.ivSelectedImage.setVisibility(View.VISIBLE);
                    binding.clSendSelected.setVisibility(View.VISIBLE);
                    binding.camera.setVisibility(View.GONE);
                    binding.ivSelectedImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
                }


            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
        }
    }


}
