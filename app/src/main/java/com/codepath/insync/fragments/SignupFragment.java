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

import com.codepath.insync.R;
import com.codepath.insync.databinding.FragmentSignupBinding;


public class SignupFragment extends Fragment {
    FragmentSignupBinding binding;
    OnLoginListener loginListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.etSignupEmail.clearFocus();
                    binding.etSignupName.clearFocus();
                    binding.etSignupPassword.clearFocus();
                    binding.etSignupPhone.clearFocus();

                    return false;
                }
            });
        }
    }

    public interface OnLoginListener {
        void onLogin();
    }
}
