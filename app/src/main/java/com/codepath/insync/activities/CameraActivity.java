package com.codepath.insync.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.insync.R;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.File;

import static com.codepath.insync.R.id.camera;

public class CameraActivity extends AppCompatActivity {
    CameraView cameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = (CameraView) findViewById(camera);

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                // Create a bitmap
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            }
        });

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                // The File parameter is an MP4 file.
            }
        });
    }

    public void takePhoto(){
        cameraView.captureImage();
    }

    public void shootVideo(){
        cameraView.startRecordingVideo();
        cameraView.postDelayed(new Runnable() {
            @Override
            public void run() {
                cameraView.stopRecordingVideo();
            }
        }, 2500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }
}
