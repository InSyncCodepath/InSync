package com.codepath.insync.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.insync.R;
import com.codepath.insync.databinding.FragmentMessageSendBinding;
import com.codepath.insync.models.Message;
import com.codepath.insync.models.User;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class MessageSendFragment extends Fragment {
    private static String TAG = "MessageSendFragment";
    FragmentMessageSendBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                Message message = new Message();
                message.setBody(data);

                message.setSender(User.getCurrentUser());

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(getActivity(), "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();

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
