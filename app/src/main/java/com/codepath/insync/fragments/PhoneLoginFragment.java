package com.codepath.insync.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.insync.R;
import com.codepath.insync.activities.CameraActivity;
import com.codepath.insync.databinding.FragmentPhoneLoginBinding;
import com.codepath.insync.listeners.OnLoginListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.CommonUtil;
import com.codepath.insync.utils.Constants;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.phoneNumber;
import static android.app.Activity.RESULT_OK;


public class PhoneLoginFragment extends Fragment {
    private final static String TAG = "PhoneLoginFragment";
    FragmentPhoneLoginBinding binding;
    OnLoginListener loginListener;
    String phoneNum;
    String eventId;
    ParseFile parseFile;

    public static PhoneLoginFragment newInstance(String phoneNum, String eventId) {

        Bundle args = new Bundle();

        PhoneLoginFragment phoneLoginFragment = new PhoneLoginFragment();
        args.putString("phoneNum", phoneNum);
        args.putString("eventId", eventId);

        phoneLoginFragment.setArguments(args);
        return phoneLoginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_login, container, false);
        loginListener = (OnLoginListener) getActivity();
        phoneNum = getArguments().getString("phoneNum");
        eventId = getArguments().getString("eventId");
        parseFile = null;
        setupUI(binding.svLogin);
        setupClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1024) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getStringExtra("filePath");
                File file = new File(filePath);
                parseFile = new ParseFile(file);

                binding.civProfilePic.setImageBitmap(BitmapFactory.decodeFile(filePath));
                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            CommonUtil.createSnackbar(binding.svLogin, getContext(), "Your profile picture could not be added! Please try again later.", R.color.primary);
                        }
                    }
                });
            }
        }
    }

    private void setupClickListeners() {
        binding.tvLoginResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendVerificationCode(binding.etLoginPNum.getText().toString());
                loginListener.onSignup();
            }
        });
        binding.fabSignUpAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                intent.putExtra("is_profile_pic", true);
                startActivityForResult(intent, 1024);
            }
        });



        binding.tvPhoneLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tilLoginCode.getVisibility() == View.GONE) {
                    binding.tilLoginCode.setVisibility(View.VISIBLE);
                    binding.tvLoginResend.setVisibility(View.VISIBLE);
                    binding.tvPhoneLoginBtn.setText(R.string.login);
                    sendVerificationCode(binding.etLoginPNum.getText().toString());
                    return;
                }
                final User loginUser = new User();


                loginUser.login(
                        binding.etLoginPNum.getText().toString(),
                        binding.etLoginCode.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(final ParseUser user, ParseException e) {
                                if (e == null) {
                                    if (parseFile != null) {
                                        loginUser.setProfileImage(parseFile);
                                        loginUser.saveInBackground();
                                    }
                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.put("userId", user.getObjectId());
                                    installation.saveInBackground();
                                    Toast.makeText(
                                            getActivity(),
                                            "Login successful!", Toast.LENGTH_SHORT)
                                            .show();
                                    Event.findEvent(eventId, new GetCallback<Event>() {
                                        @Override
                                        public void done(Event event, ParseException e) {
                                            UserEventRelation userEventRelation = new UserEventRelation(event, user.getObjectId(), false, true, true, true, 2);
                                            userEventRelation.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    loginListener.onLoginSuccess();
                                                }
                                            });
                                        }
                                    });


                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Error logging in. Please try again later!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
            }
        });
    }

    public void sendVerificationCode(String phoneNum) {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("phoneNumber", phoneNum);
        payload.put("name", "James Bond");
        ParseCloud.callFunctionInBackground("sendVerificationCode", payload, new FunctionCallback<Object>() {

            @Override
            public void done(Object object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error sending sms push to cloud: " + e.toString());
                } else {
                    Log.d(TAG, "SMS Push sent successfully!");
                }
            }
        });
    }

    public void setupUI(View view) {
        binding.etLoginPNum.setText(phoneNum);
        binding.tilLoginCode.setVisibility(View.GONE);
        binding.tvLoginResend.setVisibility(View.GONE);
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.etLoginPNum.clearFocus();
                    binding.etLoginCode.clearFocus();

                    return false;
                }
            });
        }
    }

}
