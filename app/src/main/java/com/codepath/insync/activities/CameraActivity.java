package com.codepath.insync.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.codepath.insync.R;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.File;

import static com.codepath.insync.R.id.camera;

public class CameraActivity extends AppCompatActivity {
//    CameraView cameraView;//camerakit
    MaterialCamera camera;
    private final static int CAMERA_RQ = 6969;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //camerakit
//        cameraView = (CameraView) findViewById(camera);
//
//        cameraView.setCameraListener(new CameraListener() {
//            @Override
//            public void onPictureTaken(byte[] picture) {
//                super.onPictureTaken(picture);
//
//                // Create a bitmap
//                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
//            }
//        });
//
//        cameraView.setCameraListener(new CameraListener() {
//            @Override
//            public void onVideoTaken(File video) {
//                super.onVideoTaken(video);
//                // The File parameter is an MP4 file.
//            }
//        });
        camera = new MaterialCamera(this);
//        camera.saveDir(new File(Environment.getExternalStorageDirectory()+"/camera/"));
        camera.stillShot()
                .start(CAMERA_RQ);
    }

//    public void takePhoto(){
////        cameraView.captureImage();
//        new MaterialCamera(this)
//                .stillShot()
//                .start(CAMERA_RQ);
//    }
//
//    public void shootVideo(){
//        cameraView.startRecordingVideo();
//        cameraView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cameraView.stopRecordingVideo();
//            }
//        }, 2500);
//    }
////camerakit
//    @Override
//    protected void onResume() {
//        super.onResume();
//        cameraView.start();
//    }
//
//    @Override
//    protected void onPause() {
//        cameraView.stop();
//        super.onPause();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {

            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, "Saved to: " + data.getDataString(), Toast.LENGTH_LONG).show();
                String filePath = data.getData().getPath();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("filePath",filePath);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return;
            } else if(data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
