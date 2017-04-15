package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.insync.R;
import com.codepath.insync.databinding.FragmentMessageSendBinding;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.User;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class MessageSendFragment extends Fragment {
    private static String TAG = "MessageSendFragment";
    FragmentMessageSendBinding binding;
    Event event;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_send, container, false);
        setupMessagePosting();
        return binding.getRoot();
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

    public void clearViewFocus() {
        binding.etEDMessage.clearFocus();
    }
}
