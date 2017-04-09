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
import com.codepath.insync.databinding.FragmentLoginBinding;
import com.codepath.insync.interfaces.OnLoginListener;
import com.codepath.insync.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginFragment extends Fragment {
    FragmentLoginBinding binding;
    OnLoginListener loginListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        loginListener = (OnLoginListener) getActivity();
        setupUI(binding.svLogin);
        setupClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setupClickListeners() {
        binding.tvLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginListener.onSignup();
            }
        });

        binding.tvLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = new User();
                user.login(
                        binding.etLoginEmail.getText().toString(),
                        binding.etLoginPassword.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {
                                    Toast.makeText(
                                            getActivity(),
                                            "Login successful!", Toast.LENGTH_SHORT)
                                            .show();
                                    loginListener.onLoginSuccess();
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

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.etLoginEmail.clearFocus();
                    binding.etLoginPassword.clearFocus();

                    return false;
                }
            });
        }
    }

}
