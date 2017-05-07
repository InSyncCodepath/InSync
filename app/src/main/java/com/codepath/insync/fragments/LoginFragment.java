package com.codepath.insync.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.insync.R;
import com.codepath.insync.databinding.FragmentLoginBinding;
import com.codepath.insync.listeners.OnLoginListener;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.utils.CommonUtil;
import com.codepath.insync.utils.FormatUtil;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;


public class LoginFragment extends Fragment {
    FragmentLoginBinding binding;
    OnLoginListener loginListener;
    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        loginListener = (OnLoginListener) getActivity();

        String formatStr = getResources().getString(R.string.not_registered_create_account);
        binding.tvLoginSignup.setText(FormatUtil.buildSpan(formatStr, 0, 19, 20, formatStr.length()));
        setupUI(binding.rlLogin);
        setupTextChangedListeners();
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupTextChangedListeners() {
        binding.etLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvLoginBtn.setEnabled(s.length() > 0 && binding.etLoginPassword.getText().length() > 0);
                int color = binding.tvLoginBtn.isEnabled() ? R.color.primary : R.color.very_light_white;
                binding.tvLoginBtn.setTextColor(ContextCompat.getColor(getContext(), color));


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvLoginBtn.setEnabled(s.length() > 0 && binding.etLogin.getText().length() > 0);
                int color = binding.tvLoginBtn.isEnabled() ? R.color.primary : R.color.very_light_white;
                binding.tvLoginBtn.setTextColor(ContextCompat.getColor(getContext(), color));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                        binding.etLogin.getText().toString(),
                        binding.etLoginPassword.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {
                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.put("userId", user.getObjectId());
                                    installation.saveInBackground();
                                    binding.tvLoginSignup.setVisibility(View.INVISIBLE);
                                    CommonUtil.createSnackbar(binding.rlLogin, getContext(),
                                            "Login successful!", R.color.primary);
                                    loginListener.onLoginSuccess();
                                } else {
                                    CommonUtil.createSnackbar(binding.rlLogin, getContext(),
                                            "Error logging in. Please try again later!", R.color.primary);
                                }
                            }
                        });
            }
        });

        binding.btnFblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnFblogin.setVisibility(View.GONE);
                loginWithFB();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        public Bitmap bitmap;
        String url;
        User user;

        public ProfilePhotoAsync(String url, User user) {
            this.url = url;
            this.user = user;
        }

        @Override
        protected String doInBackground(String... params) {
            bitmap = downloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (bitmap != null) {
                updateParseUserInfo(bitmap, user);
            }

        }
    }

    private void updateParseUserInfo(Bitmap bitmap, final User user) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imgByteArray = baos.toByteArray();
        final ParseFile file = new ParseFile("user_profile_file", imgByteArray);

        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    user.setProfileImage(file);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Facebook user's info updated successfully.");
                            }
                        }
                    });
                }

            }
        });
    }

    public static Bitmap downloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting profile pic bitmap", e);
        }
        return bm;
    }


    private void loginWithFB() {
        List<String> permissions = Arrays.asList("public_profile", "email", "user_friends");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException err) {

                if (parseUser == null) {
                    Log.d(TAG, "Cancelled or error in Facebook login.");
                } else if (parseUser.isNew()) {
                    Log.d(TAG, "New user successfully logged into Facebook");
                    loginListener.onLoginSuccess();
                    getUserDataFromFB(parseUser);
                } else {
                    Log.d(TAG, "Existing Facebook user logged in successfully!");
                    loginListener.onLoginSuccess();
                }
            }
        });

    }

    private void getUserDataFromFB(final ParseUser parseUser) {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            Log.d(TAG, "Graph request response: "+response.getRawResponse());

                            parseUser.setEmail(response.getJSONObject().getString("email"));
                            User user = new User(parseUser);

                            user.setName(response.getJSONObject().getString("name"));
                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            //  Returns a 50x50 profile picture
                            String pictureUrl = data.getString("url");

                            Log.d(TAG, "Facebook profile pic url: " + pictureUrl);

                            new ProfilePhotoAsync(pictureUrl, user).execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.etLogin.clearFocus();
                    binding.etLoginPassword.clearFocus();

                    return false;
                }
            });
        }
    }

}
