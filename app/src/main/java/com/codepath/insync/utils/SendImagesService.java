package com.codepath.insync.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.codepath.insync.Manifest;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import permissions.dispatcher.NeedsPermission;

import static java.security.AccessController.getContext;


public class SendImagesService extends Service {
    String LATITUDE, LATITUDE_REF, LONGITUDE, LONGITUDE_REF;
    Float Latitude, Longitude;
    boolean isEventImage = false;
    ParseFile parseFile;
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public boolean getLocation(String photoFilePath, double lat, double lng) {
        // Create and configure BitmapFactory
        float [] latLong = new float[2];
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
            LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            LATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            LONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

            if((LATITUDE !=null)
                    && (LATITUDE_REF !=null)
                    && (LONGITUDE != null)
                    && (LONGITUDE_REF !=null)) {

                if (LATITUDE_REF.equals("N")) {
                    Latitude = convertToDegree(LATITUDE);
                } else {
                    Latitude = 0 - convertToDegree(LATITUDE);
                }

                if (LONGITUDE_REF.equals("E")) {
                    Longitude = convertToDegree(LONGITUDE);
                } else {
                    Longitude = 0 - convertToDegree(LONGITUDE);
                }
            }
            latLong[0] = Latitude;
            latLong[1] = Longitude;

            if(Latitude == lat && Longitude == lng){
                return true;
            } else {
                return false;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getEventImages(Context context, Event event){
        Calendar startCal = Calendar.getInstance();
        Date start = event.getStartDate();
        //startCal.getTime(start);
        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media.DATE_TAKEN + " > ? AND " + MediaStore.Images.Media.DATE_TAKEN + " < ? "
                //+"AND "
                //+  MediaStore.Images.Media.LATITUDE + " = ? AND " + MediaStore.Images.Media.LONGITUDE + "= ?"
                ;
        String[] selectionArgs = { String.valueOf(event.getStartDate().getTime()), String.valueOf(event.getEndDate().getTime())
                //, String.valueOf(event1.getLocation().getLatitude()), String.valueOf(event1.getLocation().getLongitude())
        };
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        //exif
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(0);

                if(imagePath.contains("amera")){
                    isEventImage = getLocation(imagePath, event.getLocation().getLatitude(), event.getLocation().getLongitude());
                    if(isEventImage) {
                        File file = new File(imagePath);
                        parseFile = new ParseFile(file);
                    }
                }
            }
        }
        cursor.close();
    }

    private Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

        result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

        return result;


    }
    //@SuppressWarnings("all")
    //@NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})

}