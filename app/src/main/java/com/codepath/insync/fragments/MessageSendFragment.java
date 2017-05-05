package com.codepath.insync.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.insync.R;
import com.codepath.insync.activities.CameraActivity;
import com.codepath.insync.databinding.FragmentMessageSendBinding;
import com.codepath.insync.listeners.OnMessageChangeListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.utils.Camera;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class MessageSendFragment extends Fragment {
    private static String TAG = "MessageSendFragment";
    FragmentMessageSendBinding binding;
    Event event;
    Context context;
    private Uri imageUri;

    OnMessageChangeListener messageChangeListener;
    private static final int REQUEST_CAMERA_ACTIVITY = 1027;
    private static final int SELECT_PICTURE = 1028;

    public static MessageSendFragment newInstance(String eventId) {

        Bundle args = new Bundle();

        MessageSendFragment messageSendFragment = new MessageSendFragment();
        args.putString("eventId", eventId);

        messageSendFragment.setArguments(args);
        return messageSendFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageChangeListener = (OnMessageChangeListener) getActivity();
        event = new Event();
        event.setObjectId(getArguments().getString("eventId"));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_send, container, false);
        setupTextListener();
        setupChatClickListeners();
        return binding.getRoot();
    }

    private void setupTextListener() {
        binding.etEDMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.fabEDCamera.setVisibility(View.VISIBLE);
                    binding.fabEDGallery.setVisibility(View.VISIBLE);
                    binding.fabMessageSend.setVisibility(View.GONE);
                } else {
                    binding.fabEDCamera.setVisibility(View.GONE);
                    binding.fabEDGallery.setVisibility(View.GONE);
                    binding.fabMessageSend.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etEDMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    messageChangeListener.onFocused();
                }
            }
        });
    }

    private void setupChatClickListeners() {
        binding.fabMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupMessagePosting();
            }
        });
        binding.fabEDCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivityForResult(intent, REQUEST_CAMERA_ACTIVITY);
            }
        });

        binding.fabEDGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_ACTIVITY && resultCode == RESULT_OK) {
            String message = data.getStringExtra("message");
            String filePath = data.getStringExtra("filePath");
            ParseFile parseFile = null;
            if (filePath != null) {
                File file = new File(filePath);
                parseFile = new ParseFile(file);
            } else {
                try {
                    parseFile = new ParseFile(Camera.readBytes(getApplicationContext(), imageUri));
                    imageUri = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (parseFile != null) {
                setupImagePosting(message, parseFile);
            }

        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            imageUri = data.getData();
            intent.putExtra("image_uri", imageUri.toString());
            startActivityForResult(intent, REQUEST_CAMERA_ACTIVITY);
        }
    }

    public void setupImagePosting(String messageBody, ParseFile parseFile) {
        final Message message = new Message();
        message.setMedia(parseFile);
        message.setSender(User.getCurrentUser());
        if (messageBody != null) {
            message.setBody(messageBody);
        }

        messageChangeListener.onMessageCreated(message);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Your Photo was successfully sent!",
                            Toast.LENGTH_SHORT).show();
                    event.getMessageRelation().add(message);
                    event.updateEvent(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Message was successfully linked to the event.");
                            } else {
                                Log.e(TAG, "Message link to event failed with error: " + e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to save message", e);
                }
            }
        });
    }

    public void setupMessagePosting() {

        // When send button is clicked, create message object on Parse

        String data = binding.etEDMessage.getText().toString();

        // Using new `Message` Parse-backed model now
        final Message message = new Message();
        message.setBody(data);
        message.setSender(User.getCurrentUser());
        messageChangeListener.onMessageCreated(message);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Your message was successfully sent!",
                            Toast.LENGTH_SHORT).show();

                    event.getMessageRelation().add(message);
                    event.updateEvent(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Message was successfully linked to the event.");
                            } else {
                                Log.e(TAG, "Message link to event failed with error: " + e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to save message", e);
                }
            }
        });
        binding.etEDMessage.setText(null);

    }

    public void clearViewFocus() {
        binding.etEDMessage.clearFocus();
    }
}
