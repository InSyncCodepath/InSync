package com.codepath.insync.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.codepath.insync.R;
import com.codepath.insync.activities.CameraActivity;
import com.codepath.insync.databinding.FragmentSignupBinding;
import com.codepath.insync.listeners.OnLoginListener;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.utils.CommonUtil;
import com.codepath.insync.utils.FormatUtil;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class SignupFragment extends Fragment {
    FragmentSignupBinding binding;
    OnLoginListener loginListener;
    ParseFile parseFile;
    // Create the ParseUser
    final User user = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        loginListener = (OnLoginListener) getActivity();
        parseFile = null;
        String formatStr = getResources().getString(R.string.already_a_member_login);
        binding.tvSignupLogin.setText(FormatUtil.buildSpan(formatStr, 0, 17, 18, formatStr.length()));
        setupUI(binding.rlSignup);
        setupClickListeners();
        setupTextChangedListeners();
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
                            CommonUtil.createSnackbar(binding.rlSignup, getContext(), "Your profile picture could not be added! Please try again later.", R.color.primary);
                        }
                    }
                });
            }
        }
    }

    private void setupTextChangedListeners() {
        binding.etSignupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvSignupBtn.setEnabled(s.length() > 0 && binding.etSignupUsername.getText().length() > 0 && binding.etSignupPassword.getText().length() > 0 && binding.etSignupPhone.getText().length() > 0);
                int color = binding.tvSignupBtn.isEnabled() ? R.color.primary : R.color.very_light_white;
                binding.tvSignupBtn.setTextColor(ContextCompat.getColor(getContext(), color));


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etSignupUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvSignupBtn.setEnabled(s.length() > 0 && binding.etSignupName.getText().length() > 0 && binding.etSignupPassword.getText().length() > 0 && binding.etSignupPhone.getText().length() > 0);
                int color = binding.tvSignupBtn.isEnabled() ? R.color.primary : R.color.very_light_white;
                binding.tvSignupBtn.setTextColor(ContextCompat.getColor(getContext(), color));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etSignupPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvSignupBtn.setEnabled(s.length() > 0 && binding.etSignupName.getText().length() > 0 && binding.etSignupUsername.getText().length() > 0 && binding.etSignupPhone.getText().length() > 0);
                int color = binding.tvSignupBtn.isEnabled() ? R.color.primary : R.color.very_light_white;
                binding.tvSignupBtn.setTextColor(ContextCompat.getColor(getContext(), color));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etSignupPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvSignupBtn.setEnabled(s.length() > 0 && binding.etSignupName.getText().length() > 0 && binding.etSignupUsername.getText().length() > 0 && binding.etSignupPassword.getText().length() > 0);
                int color = binding.tvSignupBtn.isEnabled() ? R.color.primary : R.color.very_light_white;
                binding.tvSignupBtn.setTextColor(ContextCompat.getColor(getContext(), color));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupClickListeners() {
        binding.fabSignUpAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                intent.putExtra("is_profile_pic", true);
                startActivityForResult(intent, 1024);
            }
        });
        binding.tvSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set core properties
                user.setUsername(binding.etSignupUsername.getText().toString());
                user.setPassword(binding.etSignupPassword.getText().toString());
                // Set custom properties
                user.setName(binding.etSignupName.getText().toString());
                user.setPhoneNumber(binding.etSignupPhone.getText().toString());
                if (parseFile != null) {
                    user.setProfileImage(parseFile);
                }
                // Invoke signUpInBackground
                user.signup(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            binding.tvSignupLogin.setVisibility(View.INVISIBLE);
                            CommonUtil.createSnackbar(binding.rlSignup, getContext(), "Sign up successful!", R.color.primary);
                            user.login(
                                    binding.etSignupUsername.getText().toString(),
                                    binding.etSignupPassword.getText().toString(),
                                    new LogInCallback() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if (e == null) {
                                                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                                installation.put("userId", user.getObjectId());
                                                installation.saveInBackground();
                                                loginListener.onLoginSuccess();
                                            } else {
                                                CommonUtil.createSnackbar(binding.rlSignup, getContext(),
                                                        "Error logging in. Please try again later!", R.color.primary);
                                            }
                                        }
                                    });
                        } else {
                            CommonUtil.createSnackbar(binding.rlSignup, getContext(), "Error signing up. Please try again later!", R.color.primary);
                        }
                    }
                });
            }


        });
        binding.tvSignupLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginListener.onLogin();
            }
        });
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.etSignupName.clearFocus();
                    binding.etSignupUsername.clearFocus();
                    binding.etSignupPassword.clearFocus();
                    binding.etSignupPhone.clearFocus();

                    return false;
                }
            });
        }
    }
}
