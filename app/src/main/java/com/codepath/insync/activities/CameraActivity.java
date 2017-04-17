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
    MaterialCamera camera;
    private final static int CAMERA_RQ = 6969;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera = new MaterialCamera(this);
        camera.stillShot()
                .start(CAMERA_RQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
