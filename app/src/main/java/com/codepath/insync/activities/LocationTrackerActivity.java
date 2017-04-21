package com.codepath.insync.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.insync.R;
import com.codepath.insync.adapters.CustomMapWindowAdapter;
import com.codepath.insync.databinding.ActivityLocationTrackerBinding;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.R.attr.resource;


public class LocationTrackerActivity extends AppCompatActivity {

    private static final String TAG = "LocationTrackerActivity";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    LatLng eventLocation;
    String eventId;
    Event event;
    //RelativeLayout mapsContainer;
    View mapsContainer;
    ActivityLocationTrackerBinding binding;
    Map<String, Marker> userMap;
    boolean mfirstLoad;
    BroadcastReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_tracker);
        //mapsContainer = binding.rlLocationTracker;
        mapsContainer = findViewById(R.id.mapContainer);
        userMap = new HashMap<>();
        mfirstLoad = true;
        processIntent();

        mapsContainer.setVisibility(View.INVISIBLE);
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.setInfoWindowAdapter(new CustomMapWindowAdapter(getLayoutInflater()));
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPause() {
        unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.codepath.insync.Users");
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Broadcast received. Updating location");
                updateUsersLocations();
            }
        };
        registerReceiver(messageReceiver, filter);
    }

    private void processIntent() {
        eventId = getIntent().getStringExtra("eventId");
        eventLocation = new LatLng(
                getIntent().getDoubleExtra("eventLatitude", 0.0),
                getIntent().getDoubleExtra("eventLongitude", 0.0));
        event = new Event();
        event.setObjectId(eventId);

        updateUsersLocations();
    }

    private void updateUsersLocations() {
        ParseQuery<UserEventRelation> userEventQuery = ParseQuery.getQuery(UserEventRelation.class);
        userEventQuery.whereEqualTo("event", event);

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereMatchesKeyInQuery("objectId", "userId", userEventQuery);

        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Found the following users: "+parseUsers.size());
                    for (ParseUser parseUser : parseUsers) {
                        final User user = new User(parseUser);
                        ParseGeoPoint userLocation = user.getLocation();
                        if (userLocation == null) {
                            continue;
                        }
                        final LatLng currLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                        if (userMap.containsKey(user.getObjectId())) {
                            Marker userMarker = userMap.get(user.getObjectId());
                            animateMarker(userMarker, currLocation, false);
                        } else {
                            String imageUrl = null;
                            if (user.getProfileImage() != null) {
                                imageUrl = user.getProfileImage().getUrl();
                            }
                            if (imageUrl != null) {
                                Glide.with(getApplicationContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_profile)
                                        .crossFade()
                                        .bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 4, 0))
                                        .into(new SimpleTarget<GlideDrawable>() {
                                            @Override
                                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                                                Marker userMarker = map.addMarker(new MarkerOptions()
                                                        .title(user.getName())
                                                        //.snippet("Snippet")
                                                        //.icon(customMarker)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(resource)))
                                                        .position(currLocation)
                                                        .anchor(0.5f, 1));
                                                userMap.put(user.getObjectId(), userMarker);
                                                animateMarker(userMarker, currLocation, false);
                                            }
                                        });
                            } else {
                                Marker userMarker = map.addMarker(new MarkerOptions()
                                        .title(user.getName())
                                        //.snippet("Snippet")
                                        //.icon(customMarker)
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(null)))
                                        .position(currLocation)
                                        .anchor(0.5f, 1));
                                userMap.put(user.getObjectId(), userMarker);
                                animateMarker(userMarker, currLocation, false);
                            }
                        }
                    }
                    ParseGeoPoint currUserGeo = User.getCurrentUser().getParseGeoPoint("location");
                    if (currUserGeo != null) {
                        LatLng currUserLoc = new LatLng(currUserGeo.getLatitude(), currUserGeo.getLongitude());
                        if (mfirstLoad) {
                            setLocation(currUserLoc);
                            circularRevealActivity();
                            mfirstLoad = false;
                            /*ViewTreeObserver viewTreeObserver = mapsContainer.getViewTreeObserver();
                            if (viewTreeObserver.isAlive()) {
                                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        circularRevealActivity();
                                        mapsContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    }
                                });
                            }*/
                        }

                    }
                } else {
                    Snackbar.make(mapsContainer, "Could not locate guests at this time. Please try later.",
                            Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void circularRevealActivity() {

        int cx = mapsContainer.getLeft();
        int cy = mapsContainer.getTop();

        float finalRadius = Math.max(mapsContainer.getWidth(), mapsContainer.getHeight()) * 2.0f;

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            circularReveal = ViewAnimationUtils.createCircularReveal(mapsContainer, cx, cy, 0, finalRadius);
        }
        circularReveal.setDuration(2000);

        // make the view visible and start the animation
        mapsContainer.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        super.onStop();
    }




    private void setLocation(LatLng latLng) {

        Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        //map.animateCamera(cameraUpdate);
        map.moveCamera(cameraUpdate);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 15);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private Bitmap getMarkerBitmap(GlideDrawable glideDrawable) {
        final View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_custom_map_marker, null);
        final ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.iv_custom_marker);
        final IconGenerator iconGenerator = new IconGenerator(LocationTrackerActivity.this);

        if (glideDrawable == null) {
            markerImageView.setImageResource(R.drawable.ic_profile);
        } else {
            markerImageView.setImageDrawable(glideDrawable);
        }
/*        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);*/

        // Define the size you want from dimensions file

        Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(),
                R.drawable.shape_circle, null);
        //iconGenerator.setBackground(shapeDrawable);

        iconGenerator.setContentView(customMarkerView);
        iconGenerator.setColor(R.color.accent);
        iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
        return iconGenerator.makeIcon();
    }

    private void showAlertDialogForPoint(Marker marker) {
        // inflate message_item.xml view

        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setMessage("Send a nudge to: "+marker.getTitle());

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Configure dialog button (OK)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> payload = new HashMap<>();
                        payload.put("customData", "My message");
                        ParseCloud.callFunctionInBackground("pushChannelTest", payload, new FunctionCallback<Object>() {

                            @Override
                            public void done(Object object, ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Error sending push to cloud: " + e.toString ());
                                } else {
                                    Log.d(TAG, "Push sent successfully!");
                                }
                            }
                        });
                    }
                });

        // Configure dialog button (Cancel)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Display the dialog
        alertDialog.show();
    }


}
