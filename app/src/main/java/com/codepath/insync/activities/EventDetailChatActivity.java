package com.codepath.insync.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.codepath.insync.Manifest;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventDetailBinding;
import com.codepath.insync.fragments.ConfirmationFragment;
import com.codepath.insync.fragments.MessageSendFragment;
import com.codepath.insync.fragments.UpcomingEventDetailFragment;
import com.codepath.insync.listeners.OnImageClickListener;
import com.codepath.insync.listeners.OnImageUploadClickListener;
import com.codepath.insync.listeners.OnMessageChangeListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.utils.BitmapScaler;
import com.codepath.insync.utils.Constants;
import com.parse.GetCallback;
import com.parse.ParseException;

import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class EventDetailChatActivity extends AppCompatActivity implements
        UpcomingEventDetailFragment.OnViewTouchListener,
        ConfirmationFragment.UpdateDraftDialogListener,
        OnImageClickListener,
        OnMessageChangeListener,
        OnImageUploadClickListener{

    private static final String TAG = "EventDetailChatActivity";
    private static final int REQUEST_CAMERA_ACTIVITY = 1027;
    private static final int SELECT_PICTURE = 1028;

    ActivityEventDetailBinding binding;
    Event event;
    String eventId;
    boolean canTrack;
    MessageSendFragment messageSendFragment;
    FragmentManager fragmentManager;
    RelativeLayout rlEventDetail;
    UpcomingEventDetailFragment upcomingEventDetailFragment;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);

        fragmentManager = getSupportFragmentManager();
        rlEventDetail = binding.rlEventDetail;

        postponeEnterTransition();
        processIntent();
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for the current event
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return super.onCreateOptionsMenu(menu);
        }
        if (canTrack) {
            getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void processIntent() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        canTrack = intent.getBooleanExtra("canTrack", false);
        binding.ivEDProfile.setTransitionName(intent.getStringExtra("transition_name"));
        Event.findEvent(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event eventObj, ParseException e) {
                if (e == null) {
                    event = eventObj;
                    loadViews();
                    loadFragments();
                } else {
                    event = null;
                    Log.e(TAG, "Error finding event.");
                    supportFinishAfterTransition();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_track:
                Intent intent = new Intent(EventDetailChatActivity.this, LocationTrackerActivity.class);
                intent.putExtra("eventId", event.getObjectId());
                intent.putExtra("eventLatitude", event.getLocation().getLatitude());
                intent.putExtra("eventLongitude", event.getLocation().getLongitude());
                startActivity(intent);
                break;
            case R.id.action_highlights:
                handleHighlightsAction();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleHighlightsAction() {
        String message = "Are you sure you want to end this event? This will prevent you from posting any information or media to this event.";
        ConfirmationFragment confirmationFragment = ConfirmationFragment.newInstance(message, "End Event Now", "Cancel");
        confirmationFragment.show(fragmentManager, "fragment_confirmation");
    }
    @Override
    public void onConfirmUpdateDialog(int position) {
        if (position == DialogInterface.BUTTON_POSITIVE) {
            event.setHasEnded(true);
            event.updateEvent(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Past event status updated successfully!");
                    } else {
                        Log.e(TAG, "Could not update past event flag");
                    }
                }
            });

            Intent intent = new Intent();
            intent.putExtra("hasEnded", true);
            intent.putExtra("eventName", event.getName());
            intent.putExtra("eventId", event.getObjectId());
            intent.putExtra("eventDate", event.getStartDate().getTime());
            intent.putExtra("eventAddress", event.getAddress());
            intent.putExtra("eventImage", event.getProfileImage().getUrl());
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText || view instanceof FloatingActionButton)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideAndClearFocus(v);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideAndClearFocus(View v) {
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        messageSendFragment.clearViewFocus();
    }

    private void loadViews() {
        ParseFile profileImage = event.getProfileImage();
        if (profileImage != null) {
            Picasso.with(this).load(profileImage.getUrl())
                    .placeholder(R.drawable.ic_camera_alt_white_48px)
                    .resize(binding.ivEDProfile.getWidth(), 0)
                    .transform(new RoundedCornersTransformation(10, 0)).into(binding.ivEDProfile,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            // Call the "scheduleStartPostponedTransition()" method
                            // below when you know for certain that the shared element is
                            // ready for the transition to begin.
                            scheduleStartPostponedTransition(binding.ivEDProfile);
                        }

                        @Override
                        public void onError() {
                            // ...
                        }
                    });

        }

        binding.tvEDName.setText(event.getName());

    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        binding.ivEDProfile.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );
    }

    private void setupToolbar() {

        //collapsingToolbar = binding.ctlEventDetail;

        setSupportActionBar(binding.tbEventDetail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.tbEventDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle animationBundle =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_left).toBundle();


                Intent eventDetailMoreIntent = new Intent(EventDetailChatActivity.this, EventDetailMoreActivity.class);
                eventDetailMoreIntent.putExtra("eventId", eventId);
                eventDetailMoreIntent.putExtra("canTrack", canTrack);
                startActivity(eventDetailMoreIntent, animationBundle);

            }
        });
    }

    private void loadFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Load current and upcoming event detail
        upcomingEventDetailFragment = UpcomingEventDetailFragment.newInstance(eventId);
        ft.replace(R.id.flMessages, upcomingEventDetailFragment);
        messageSendFragment = MessageSendFragment.newInstance(eventId);
        ft.replace(R.id.flMessageSend, messageSendFragment);
        setupUI(binding.rlEventDetail);

        ft.commit();
    }

    @Override
    public void onViewTouch(View v) {
        hideAndClearFocus(v);
    }

    @Override
    public void onItemClick(ArrayList<String> images, int position) {
        Bundle animationBundle =
                ActivityOptions.makeCustomAnimation(this, R.anim.do_not_move, R.anim.do_not_move).toBundle();

        Intent fullScreenImageIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenImageIntent.putExtra("images", images);
        fullScreenImageIntent.putExtra("position", position);
        fullScreenImageIntent.putExtra("eventName", event.getName());

        startActivity(fullScreenImageIntent,animationBundle);

    }

    @Override
    public void onMessageCreated(Message message) {
        upcomingEventDetailFragment.updateMessage(message);
    }

    @Override
    public void onFocused() {
        upcomingEventDetailFragment.updateScroll();
    }

    public byte[] resizeImageUri(Bitmap rawTakenImage) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, Constants.RESIZE_WIDTH);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_ACTIVITY && resultCode == RESULT_OK) {
            String message = data.getStringExtra("message");
            String filePath = data.getStringExtra("filePath");
            ParseFile parseFile = null;
            if (filePath != null) {
                Uri resultUri = Uri.parse(filePath);
                Bitmap rawTakenImage = BitmapFactory.decodeFile(resultUri.getPath());

                parseFile = new ParseFile(""+System.currentTimeMillis()+".png", resizeImageUri(rawTakenImage));
            } else {
                try {
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    parseFile = new ParseFile(""+System.currentTimeMillis()+".png", resizeImageUri(selectedImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = null;
            }

            if (parseFile != null) {
                messageSendFragment.setupImagePosting(message, parseFile);
            }

        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(EventDetailChatActivity.this, CameraActivity.class);
            imageUri = data.getData();
            intent.putExtra("image_uri", imageUri.toString());
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, REQUEST_CAMERA_ACTIVITY);
        }
    }

    @Override
    public void onImageUploadClick(boolean isCamera) {
        if (isCamera) {
            Intent intent = new Intent(EventDetailChatActivity.this, CameraActivity.class);
            startActivityForResult(intent, REQUEST_CAMERA_ACTIVITY);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE);
        }
    }
}
