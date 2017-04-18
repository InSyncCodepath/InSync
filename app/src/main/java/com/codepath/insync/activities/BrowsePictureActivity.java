package com.codepath.insync.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;

/**
 * Created by Gauri Gadkari on 4/17/17.
 */

public class BrowsePictureActivity extends Activity {

//    // this is the action code we use in our intent,
//    // this way we know we're looking at the response from our own action
//    private static final int SELECT_PICTURE = 1;
//
//    private String selectedImagePath;
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.gallery);
//
////        findViewById(R.id.ivAttach)
////                .setOnClickListener(new View.OnClickListener() {
////
////                    public void onClick(View arg0) {
////
////                        // in onCreate or any event where your want the user to
////                        // select a file
////                        Intent intent = new Intent();
////                        intent.setType("image/*");
////                        intent.setAction(Intent.ACTION_GET_CONTENT);
////                        startActivityForResult(Intent.createChooser(intent,
////                                "Select Picture"), SELECT_PICTURE);
////                    }
////                });
////    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                Uri selectedImageUri = data.getData();
//                selectedImagePath = getPath(selectedImageUri);
//                Glide.with(this).load(selectedImagePath).into((ImageView) findViewById(R.id.image));
//            }
//        }
//    }
//
//    /**
//     * helper to retrieve the path of an image URI
//     */
//    public String getPath(Uri uri) {
//        // just some safety built in
//        if( uri == null ) {
//            // TODO perform some logging or show user feedback
//            return null;
//        }
//        // try to retrieve the image from the media store first
//        // this will only work for images selected from gallery
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if( cursor != null ){
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            String path = cursor.getString(column_index);
//            cursor.close();
//            return path;
//        }
//        // this is our fallback here
//        return uri.getPath();
//    }

}