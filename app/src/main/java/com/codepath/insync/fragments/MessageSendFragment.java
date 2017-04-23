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


public class MessageSendFragment extends Fragment {
    private static String TAG = "MessageSendFragment";
    FragmentMessageSendBinding binding;
    Event event;
    ParseFile parseFile;
    public static final int REQUEST_CAMERA_ACTIVITY = 1027;
    public static final int SELECT_PICTURE = 1028;
    Context context;

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
        setupMessagePosting();
        setupClickListeners();
        setupTextListener();
        return binding.getRoot();
    }

    private void setupTextListener() {
        binding.fabEDSend.setVisibility(View.GONE);
        binding.etEDMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    binding.fabEDSend.setVisibility(View.GONE);
                    binding.floatingActionMenu.setVisibility(View.VISIBLE);
                } else {
                    binding.fabEDSend.setVisibility(View.VISIBLE);
                    binding.floatingActionMenu.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupClickListeners() {
        binding.menuItemCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivityForResult(intent, REQUEST_CAMERA_ACTIVITY);
            }
        });

        binding.menuItemGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    void setupImagePosting(ParseFile parseFile) {
        final Message message = new Message();
        message.setMedia(parseFile);
        message.setSender(User.getCurrentUser());
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(getActivity(), "Your Photo was successfully sent!",
                            Toast.LENGTH_SHORT).show();
                    event.getMessageRelation().add(message);
                    event.updateEvent(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Message was successfully linked to the event.");
                            } else {
                                Log.e(TAG, "Message link to event failed with error: "+ e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to save message", e);
                }
            }
        });
    }

    void setupMessagePosting() {

        // When send button is clicked, create message object on Parse
        binding.fabEDSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = binding.etEDMessage.getText().toString();

                // Using new `Message` Parse-backed model now
                final Message message = new Message();
                message.setBody(data);
                message.setSender(User.getCurrentUser());

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(getActivity(), "Your message was successfully sent!",
                                    Toast.LENGTH_SHORT).show();

                            event.getMessageRelation().add(message);
                            event.updateEvent(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d(TAG, "Message was successfully linked to the event.");
                                    } else {
                                        Log.e(TAG, "Message link to event failed with error: "+ e.getLocalizedMessage());
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
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getStringExtra("filePath");
                File file = new File(filePath);
                parseFile = new ParseFile(file);
                setupImagePosting(parseFile);
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    parseFile = new ParseFile(Camera.readBytes(context, selectedImageUri));
                    setupImagePosting(parseFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clearViewFocus() {
        binding.etEDMessage.clearFocus();
    }
}
