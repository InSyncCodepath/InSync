package com.codepath.insync.interfaces;

import android.net.Uri;

/**
 * Created by Gauri Gadkari on 4/22/17.
 */

public interface ImageInterface {
    public void cameraImage(String filePath);
    public void galleryImage(Uri fileUri);
}
