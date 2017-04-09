package com.codepath.insync.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.insync.R;
import com.codepath.insync.databinding.FragmentSignupBinding;
import com.codepath.insync.interfaces.OnLoginListener;
import com.codepath.insync.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupFragment extends Fragment {
    FragmentSignupBinding binding;
    OnLoginListener loginListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        loginListener = (OnLoginListener) getActivity();
        setupUI(binding.svSignup);
        setupClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setupClickListeners() {
        binding.tvSignupLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginListener.onLogin();
            }
        });
        binding.tvSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the ParseUser
                final User user = new User();
                // Set core properties
                user.setUsername(binding.etSignupUsername.getText().toString());
                user.setPassword(binding.etSignupPassword.getText().toString());
                user.setEmail(binding.etSignupEmail.getText().toString());
                // Set custom properties
                user.setName(binding.etSignupName.getText().toString());
                user.setPhoneNumber(binding.etSignupPhone.getText().toString());
                // Invoke signUpInBackground
                user.signup(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(
                                    getActivity(),
                                    "Sign up successful!", Toast.LENGTH_SHORT)
                                    .show();
                            user.login(
                                    binding.etSignupUsername.getText().toString(),
                                    binding.etSignupPassword.getText().toString(),
                                    new LogInCallback() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if (e == null) {
                                                loginListener.onLoginSuccess();
                                            } else {
                                                Toast.makeText(
                                                        getActivity(),
                                                        "Error logging in. Please try again later!", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Error signing up. Please try again later!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
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
                    binding.etSignupEmail.clearFocus();
                    binding.etSignupPassword.clearFocus();
                    binding.etSignupPhone.clearFocus();

                    return false;
                }
            });
        }
    }
}
